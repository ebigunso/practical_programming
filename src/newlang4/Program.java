package newlang4;

import java.util.EnumSet;
import java.util.Set;

public class Program extends Node {
	Node stmt_list = null;

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
			throw new Exception("Syntax Error: Invalid start for Program");
		} else {
			return new Program(env);
		}
	}

	private Program(Environment env) {
		super(env);
		type = NodeType.PROGRAM;
	}

	@Override
	public boolean parse() throws Exception {
		//Skip any NL at the start
		while(env.getInput().expect(LexicalType.NL)) {
			env.getInput().get();
		}

    	stmt_list = StmtListNode.getHandler(env.getInput().peek(), env); //This may throw an Exception
    	stmt_list.parse();

		//Check if END is actually written at the end of the code
		//Skip NL after END, see if next non NL unit is EOF
		while (env.getInput().expect(LexicalType.NL)) {
			env.getInput().get();
		}
		if(!env.getInput().expect(LexicalType.EOF)) {
			throw new Exception("Syntax Error: Something is written after END");
		}

		//return true if parse successful
		return true;
	}

	@Override
	public String toString() {
		return stmt_list.toString();
	}

	public Value getValue() {
		//todo
		return null;
	}

}
