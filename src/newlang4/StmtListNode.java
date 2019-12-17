package newlang4;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class StmtListNode extends Node {
	List<Node> nodes;

	private static final Set<LexicalType> FIRSTSET = EnumSet.of(
			LexicalType.NAME,
			LexicalType.FOR,
			LexicalType.END,
			LexicalType.IF,
			LexicalType.WHILE,
			LexicalType.DO
			);

	public static boolean isFirst(LexicalUnit unit) {
		return FIRSTSET.contains(unit.getType());
	}

	public static Node getHandler(LexicalUnit first, Environment env) throws Exception {
		if(!isFirst(first)){
			throw new Exception("Syntax Error: Invalid start for StmtList");
		} else {
			return new StmtListNode(env);
		}
	}

	private StmtListNode(Environment env) {
		super(env);
		type = NodeType.STMT_LIST;
	}

	public boolean parse() throws Exception {
		LexicalUnit peeked = env.getInput().peek();

		//Return true if parse successful
		return true;
	}

}
