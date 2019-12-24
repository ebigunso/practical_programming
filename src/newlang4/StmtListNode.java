package newlang4;

import java.util.ArrayList;
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
			LexicalType.DO,
			LexicalType.NL
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
		nodes = new ArrayList<Node>();
		LexicalUnit peeked;
		Node handler;

		while(true) {
			//Skip any extra NL at the start
			while (env.getInput().expect(LexicalType.NL)) {
				env.getInput().get();
			}
			peeked = env.getInput().peek();

			//May throw exception
			if(StmtNode.isFirst(peeked)) {
				handler = StmtNode.getHandler(peeked, env);
//			} else if(IfBlockNode.isFirst(peeked)){
//				handler = IfBlockNode.getHandler(peeked, env);
//			} else if(LoopBlockNode.isFirst(peeked)) {
//				handler = LoopBlockNode.getHandeler(peeked, env);
			} else {
				//Return true if parse successful
				return true;
			}
			handler.parse();
			nodes.add(handler);
		}
	}

	public String toString() {
		String strings = "";
		for(int i = 0; i < nodes.size(); i++) {
			strings += nodes.get(i).toString();
		}
		return strings;
	}

	public Value getValue() {
		//todo
		return null;
	}

}
