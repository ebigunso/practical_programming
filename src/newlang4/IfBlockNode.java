package newlang4;

import java.util.EnumSet;
import java.util.Set;

public class IfBlockNode extends Node {
	Node cond = null;
	Node op = null;
	Node elseOp = null;

	private static final Set<LexicalType> FIRSTSET = EnumSet.of(
			LexicalType.IF
			);

	public static boolean isFirst(LexicalUnit unit) {
		return FIRSTSET.contains(unit.getType());
	}

	public static Node getHandler(LexicalUnit first, Environment env) throws Exception {
		if(!isFirst(first)){
			throw new Exception("Syntax Error: Invalid start for IfBlockNode");
		} else {
			return new IfBlockNode(env);
		}
	}

	private IfBlockNode(Environment env) {
		super(env);
		type = NodeType.BLOCK;
	}

	@Override
	public boolean parse() throws Exception {
		boolean isELSEIF = false; //ElseIf doesn't need an ENDIF at the end

		//IF or ELSEIF
		if(env.getInput().expect(LexicalType.IF)) {
			env.getInput().get();
		} else if(env.getInput().expect(LexicalType.ELSEIF)) {
			isELSEIF = true;
			env.getInput().get();
		} else {
			throw new Exception("Syntax Error: Missing IF or ELSEIF while parsing IfBlockNode");
		}

		//cond
		cond = CondNode.getHandler(env.getInput().peek(), env); //May throw Exception
		cond.parse();

		//THEN
		if(env.getInput().expect(LexicalType.THEN)) {
			env.getInput().get();
		} else {
			throw new Exception("Syntax Error: Missing THEN while parsing IfBlockNode");
		}

		/*
		 * Possible patterns:
		 * 	Stmt NL
		 *  Stmt ELSE Stmt NL
		 *  NL StmtList ElseBlock ENDIF NL
		 */
		if(!isELSEIF && StmtNode.isFirst(env.getInput().peek())) {
			op = StmtNode.getHandler(env.getInput().peek(), env); //May throw Exception
			op.parse();

			if(env.getInput().expect(LexicalType.ELSE)) {
				env.getInput().get();
				elseOp = StmtNode.getHandler(env.getInput().peek(), env); //May throw Exception
				elseOp.parse();
			}
		} else if(env.getInput().expect(LexicalType.NL)) {
			//NL
			env.getInput().get();

			op = StmtListNode.getHandler(env.getInput().peek(), env); //May throw Exception
			op.parse();

			if(env.getInput().expect(LexicalType.NL)) {
				env.getInput().get();
			} else {
				throw new Exception("Syntax Error: Missing NL after operation for IF or ELSEIF");
			}

			if(env.getInput().expect(LexicalType.ELSEIF)) {
				//ELSEIF
				env.getInput().get();

				elseOp = IfBlockNode.getHandler(env.getInput().peek(), env);
				elseOp.parse();
			} else if(env.getInput().expect(LexicalType.ELSE)) {
				//ELSE
				env.getInput().get();

				if(env.getInput().expect(LexicalType.NL)) {
					env.getInput().get();
				} else {
					throw new Exception("Syntax Error: Missing NL after ELSE in IfBlockNode");
				}

				elseOp = StmtListNode.getHandler(env.getInput().peek(), env); //May throw Exception
				elseOp.parse();

				if(env.getInput().expect(LexicalType.NL)) {
					env.getInput().get();
				} else {
					throw new Exception("Syntax Error: Missing NL after operation for ELSE");
				}
			}

			//Don't read ENDIF while parsing ELSEIF with nested IfBlockNode
			if(!isELSEIF) {
				if(env.getInput().expect(LexicalType.ENDIF)) {
					env.getInput().get();
				} else {
					throw new Exception("Syntax Error: Missing ENDIF in IfBlockNode");
				}
			}
		} else {
			throw new Exception("Syntax Error: Invalid start of operation " + env.getInput().peek() + "in IfBlockNode");
		}

		//Next is ENDIF while parsing ELSEIF with nested IfBlockNode
		//Otherwise it is NL
		if(!isELSEIF) {
			if(env.getInput().expect(LexicalType.NL)) {
				env.getInput().get();
			} else {
				throw new Exception("Syntax Error: Missing NL at the end of IfBlockNode");
			}
		}

		return true;
	}

	@Override
	public String toString() {
		String out = "IF: " + cond.toString() + "\n";
		out += op.toString();
		if(elseOp != null) {
			out += "\nELSE";
			if(elseOp.getType() != NodeType.IF_BLOCK) {
				out += ":\n";
			};
			out += elseOp.toString();
		}
		return out;
	}

	@Override
	public Value getValue() {
		//todo
		return null;
	}
}
