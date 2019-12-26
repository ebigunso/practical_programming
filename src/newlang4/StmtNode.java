package newlang4;

import java.util.EnumSet;
import java.util.Set;

public class StmtNode extends Node {

	private static final Set<LexicalType> FIRSTSET = EnumSet.of(
			LexicalType.NAME,
			LexicalType.FOR,
			LexicalType.END
			);

	public static boolean isFirst(LexicalUnit unit) {
		return FIRSTSET.contains(unit.getType());
	}

	public static Node getHandler(LexicalUnit first, Environment env) throws Exception {
		switch(first.getType()) {
		case NAME:
			if(env.getInput().expect(2, LexicalType.EQ)) {
				return SubstNode.getHandler(first, env);
//			} else if (env.getInput().expect(2, LexicalType.LP)) {
//				return CallFuncNode.getHandler(first, env);
			} else {
				throw new Exception("Syntax Error: Invalid start for SubstNode or CallFuncNode(" + first + ")");
			}
//		case FOR:
		case END:
			return EndNode.getHandler(first, env);
		default:
			throw new Exception("Syntax Error: Invalid start for StmtNode(" + first + ")");
		}
	}

	private StmtNode(Environment env) {
		super(env);
		type = NodeType.STMT;
	}

	@Override
	public boolean parse() throws Exception {
		throw new Exception("Parsing Error: Parse cannnot be run for StmtNode");
	}

	//unused
	@Override
	public String toString() {
		return "foo_stmt";
	}

	public Value getValue() {
		//todo
		return null;
	}
}
