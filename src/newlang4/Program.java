package newlang4;

import java.util.HashSet;
import java.util.Set;

public class Program extends Node {
	Node stmt_list;

	private static Set<LexicalType> first = new HashSet<LexicalType>();

	static {
		first.add(LexicalType.NAME);
		first.add(LexicalType.FOR);
		first.add(LexicalType.END);
		first.add(LexicalType.IF);
		first.add(LexicalType.WHILE);
		first.add(LexicalType.DO);
		first.add(LexicalType.NL);
	}

	public static boolean isMatch(LexicalUnit unit) {
		return first.contains(unit.getType());
	}

	public Program(Environment env) {
		super(env);
		type = NodeType.PROGRAM;
	}

	public boolean parse() throws Exception {
		//todo
		//return true if parse successful
		return true;
	}

}
