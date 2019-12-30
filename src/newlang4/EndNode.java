package newlang4;

import java.util.EnumSet;
import java.util.Set;

public class EndNode extends Node {

	private static final Set<LexicalType> FIRSTSET = EnumSet.of(
			LexicalType.END
			);

	public static boolean isFirst(LexicalUnit unit) {
		return FIRSTSET.contains(unit.getType());
	}

	public static Node getHandler(LexicalUnit first, Environment env) throws Exception {
		if(!isFirst(first)){
			throw new Exception("Syntax Error: Invalid start for EndNode");
		} else {
			return new EndNode(env);
		}
	}

	private EndNode(Environment env) {
		super(env);
		type = NodeType.END;
	}

	@Override
	public boolean parse() throws Exception {
		if(env.getInput().expect(LexicalType.END)) {
			env.getInput().get();
			//return true if parse successful
			return true;
		} else {
			throw new Exception("Parsing Error: EndNode did not have END as input");
		}
	}

	@Override
	public String toString() {
		return "END";
	}

	public Value getValue() {
		System.exit(0);
		return null;
	}
}
