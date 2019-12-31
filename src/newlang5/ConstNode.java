package newlang5;

import java.util.EnumSet;
import java.util.Set;

public class ConstNode extends Node {
	Value value = null;

	private static final Set<LexicalType> FIRSTSET = EnumSet.of(
			LexicalType.INTVAL,
			LexicalType.DOUBLEVAL,
			LexicalType.LITERAL
			);

	public static boolean isFirst(LexicalUnit unit) {
		return FIRSTSET.contains(unit.getType());
	}

	public static Node getHandler(LexicalUnit first, Environment env) throws Exception {
		if(!isFirst(first)){
			throw new Exception("Syntax Error: Invalid input for ConstNode");
		} else {
			return new ConstNode(first, env);
		}
	}

	//Sets value here
	//WARNING: ConstNode itself does not get(), do it when calling handler if needed
	private ConstNode(LexicalUnit first, Environment env) throws Exception {
		super(env);
		switch(first.getType()) {
		case INTVAL:
			type = NodeType.INT_CONSTANT;
			break;
		case DOUBLEVAL:
			type = NodeType.DOUBLE_CONSTANT;
			break;
		case LITERAL:
			type = NodeType.STRING_CONSTANT;
			break;
		default:
			throw new Exception("Syntax Error: Invalid input for ConstNode");
		}

		value = first.getValue();
	}

	@Override
	public boolean parse() throws Exception {
		throw new Exception("Parsing Error: Parse cannot be run for ConstNode");
	}

	@Override
	public Value getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "[Const: " + value.getSValue() + "]";
	}

}
