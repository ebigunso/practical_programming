package newlang5;

import java.util.EnumSet;
import java.util.Set;

public class LoopBlockNode extends Node {
	Node cond = null;
	Node operation = null;
	boolean isDo = false;		//Do once regardless of condition
	boolean isUntil = false;	//Flip condition

	private static final Set<LexicalType> FIRSTSET = EnumSet.of(
			LexicalType.DO,
			LexicalType.WHILE
			);

	public static boolean isFirst(LexicalUnit unit) {
		return FIRSTSET.contains(unit.getType());
	}

	public static Node getHandler(LexicalUnit first, Environment env) throws Exception {
		if(!isFirst(first)){
			throw new Exception("Syntax Error: Invalid start for LoopBlockNode");
		} else {
			return new LoopBlockNode(env);
		}
	}

	private LoopBlockNode(Environment env) {
		super(env);
		type = NodeType.LOOP_BLOCK;
	}

	@Override
	public boolean parse() throws Exception {
		if(env.getInput().expect(LexicalType.WHILE)) {
			//WHILE
			env.getInput().get();

			cond = CondNode.getHandler(env.getInput().peek(), env); //May throw Exception
			cond.parse();

			if(env.getInput().expect(LexicalType.NL)) {
				env.getInput().get();
			} else {
				throw new Exception("Syntax Error: Missing NL after condition in LoopBlockNode");
			}

			operation = StmtListNode.getHandler(env.getInput().peek(), env); //May throw Exception
			operation.parse();

			if(env.getInput().expect(LexicalType.NL)) {
				getNL();
			} else {
				throw new Exception("Syntax Error: Missing NL after operation in LoopBlockNode");
			}

			if(env.getInput().expect(LexicalType.WEND)) {
				env.getInput().get();
			} else {
				throw new Exception("Syntax Error: Missing WEND at the end of LoopBlockNode");
			}
		} else if(env.getInput().expect(LexicalType.DO)) {
			//DO
			env.getInput().get();

			isDo = true;
			getCond();

			if(env.getInput().expect(LexicalType.NL)) {
				env.getInput().get();
			} else {
				throw new Exception("Syntax Error: Missing NL after DO or condition");
			}

			operation = StmtListNode.getHandler(env.getInput().peek(), env); //May throw Exception
			operation.parse();

			if(env.getInput().expect(LexicalType.NL)) {
				getNL();
			} else {
				throw new Exception("Syntax Error: Missing NL after operation in LoopBlockNode");
			}

			if(env.getInput().expect(LexicalType.LOOP)) {
				env.getInput().get();
			} else {
				throw new Exception("Syntax Error: Missing LOOP at the end of LoopBlockNode");
			}

			getCond();
			if(cond == null) {
				throw new Exception("Syntax Error: Missing loop condition in LoopBlockNode");
			}
		} else {
			throw new Exception("Syntax Error: Invalid start for LoopBlockNode");
		}

		if(env.getInput().expect(LexicalType.NL)) {
			env.getInput().get();
		} else {
			throw new Exception("Syntax Error: Missing NL at the end of LoopBlockNode");
		}

		return true;
	}

	private void getCond() throws Exception {
		//Do nothing if next unit is neither WHILE nor UNTIL

		if(env.getInput().expect(LexicalType.UNTIL)) {
			isUntil = true;
		}
		if(env.getInput().expect(LexicalType.WHILE)
				|| env.getInput().expect(LexicalType.UNTIL)) {
			//WHILE or UNTIL
			env.getInput().get();

			cond = CondNode.getHandler(env.getInput().peek(), env); //May throw Exception
			cond.parse();
		}
	}

	//Skip any number of NLs
	private void getNL() throws Exception {
		while(env.getInput().expect(LexicalType.NL)) {
			env.getInput().get();
		}
	}

	private boolean judge() throws Exception {
		//true -> continue loop
		if((cond.getValue().getBValue() == true && isUntil == false)
				|| (cond.getValue().getBValue() == false && isUntil == true)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		String out = "LOOP: ";
		if(isDo) {
			out += "DO ";
		}
		if(!isUntil) {
			out += "WHILE ";
		} else {
			out += "UNTIL ";
		}
		out += cond.toString() + "\n";
		out += operation.toString() + "LoopEnd";
		return out;
	}

	public Value getValue() throws Exception {
		//Option to do once regardless of condition
		if(isDo) {
			operation.getValue();
		}

		//Loop while condition is met
		while(true) {
			if(!judge()) {
				return null;
			}
			operation.getValue();
		}
	}
}
