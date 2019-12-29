package newlang4;

import java.util.EnumSet;
import java.util.Set;

public class BlockNode extends Node {
	private static final Set<LexicalType> FIRSTSET = EnumSet.of(
			LexicalType.DO,
			LexicalType.WHILE,
			LexicalType.IF
			);

	public static boolean isFirst(LexicalUnit unit) {
		return FIRSTSET.contains(unit.getType());
	}

	public static Node getHandler(LexicalUnit first, Environment env) throws Exception {
		if(IfBlockNode.isFirst(first)) {
			return IfBlockNode.getHandler(first, env);
//		} else if(LoopBlockNode.isFirst(first)) {
//			return LoopBlockNode.getHandler(first, env);
		} else {
			throw new Exception("Syntax Error: Invalid start for BlockNode");
		}
	}

	private BlockNode(Environment env) {
		super(env);
		type = NodeType.BLOCK;
	}

	@Override
	public boolean parse() throws Exception {
		throw new Exception("Parsing Error: Parse cannot be run for BlockNode");
	}

	@Override
	public String toString() {
		return "Block";
	}
}
