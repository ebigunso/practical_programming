package newlang5;

import java.util.EnumSet;
import java.util.Set;

public class ForNode extends Node {
	Node init = null;
	Node limit = null;
	Node operation = null;
	VariableNode controlVar = null;

	private static final Set<LexicalType> FIRSTSET = EnumSet.of(
			LexicalType.FOR
			);

	public static boolean isFirst(LexicalUnit unit) {
		return FIRSTSET.contains(unit.getType());
	}

	public static Node getHandler(LexicalUnit first, Environment env) throws Exception {
		if(!isFirst(first)){
			throw new Exception("Syntax Error: Invalid start for ForNode");
		} else {
			return new ForNode(env);
		}
	}

	private ForNode(Environment env) {
		super(env);
		type = NodeType.FOR_STMT;
	}

	@Override
	public boolean parse() throws Exception {
		if(env.getInput().expect(LexicalType.FOR)) {
			env.getInput().get();
		} else {
			throw new Exception("Syntax Error: Invalid start for ForNode");
		}

		init = SubstNode.getHandler(env.getInput().peek(), env); //May throw Exception
		init.parse();

		if(env.getInput().expect(LexicalType.TO)) {
			env.getInput().get();
		} else {
			throw new Exception("Syntax Error: Missing TO in ForNode");
		}

		if(env.getInput().expect(LexicalType.INTVAL)) {
			//Note: ConstNode dosen't run get(), and is not parsable
			limit = ConstNode.getHandler(env.getInput().peek(), env); //May throw Exception
			env.getInput().get();
		} else {
			throw new Exception("Syntax Error: Invalid limit in ForNode");
		}

		if(env.getInput().expect(LexicalType.NL)) {
			env.getInput().get();
		} else {
			throw new Exception("Syntax Error: Missing NL after limit in ForNode");
		}

		operation = StmtListNode.getHandler(env.getInput().peek(), env); //May throw Exception
		operation.parse();

		if(env.getInput().expect(LexicalType.NL)) {
			getNL();
		} else {
			throw new Exception("Syntax Error: Missing NL after operation in ForNode");
		}

		if(env.getInput().expect(LexicalType.NEXT)) {
			env.getInput().get();
		} else {
			throw new Exception("Syntax Error: Missing NEXT at the end of ForNode");
		}

		//Note: VariableNode doesn't run get()
		controlVar = (VariableNode)VariableNode.getHandler(env.getInput().peek().getType(), env);
		if(controlVar == null) {
			throw new Exception("Syntax Error: Invalid control variable after NEXT in ForNode");
		}
		env.getInput().get();

		return true;
	}

	//Skip any number of NLs
	private void getNL() throws Exception {
		while(env.getInput().expect(LexicalType.NL)) {
			env.getInput().get();
		}
	}

	@Override
	public String toString() {
		return "FOR: [Init: " + init.toString() + "] [Limit: " + limit.toString() + "]\n"
				+ operation.toString() + "[Next: " + controlVar.toString() + "]";
	}

	@Override
	public Value getValue() throws Exception {
		int controlVarIntVal;
		init.getValue();
		//Loop while controlVar <= limit
		while(true) {
			if(controlVar.getValue().getIValue() > limit.getValue().getIValue()) {
				return null;
			}
			operation.getValue();
			controlVarIntVal = controlVar.getValue().getIValue();
			controlVarIntVal++;
			controlVar.setValue(new ValueImpl(controlVarIntVal));
		}
	}
}
