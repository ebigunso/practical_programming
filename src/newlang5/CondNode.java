package newlang5;

import java.util.EnumSet;
import java.util.Set;

public class CondNode extends Node {
	Node left = null;
	Node right = null;
	LexicalType operator = null;

	private static final Set<LexicalType> FIRSTSET = EnumSet.of(
			LexicalType.NAME,
			LexicalType.INTVAL,
			LexicalType.DOUBLEVAL,
			LexicalType.LITERAL,
			LexicalType.SUB,
			LexicalType.LP
			);

	private static final Set<LexicalType> OPERATORS = EnumSet.of(
			LexicalType.EQ,
			LexicalType.NE,
			LexicalType.GT,
			LexicalType.GE,
			LexicalType.LT,
			LexicalType.LE
			);

	public static boolean isFirst(LexicalUnit unit) {
		return FIRSTSET.contains(unit.getType());
	}

	public static Node getHandler(LexicalUnit first, Environment env) throws Exception {
		if(!isFirst(first)){
			throw new Exception("Syntax Error: Invalid start for CondNode");
		} else {
			return new CondNode(env);
		}
	}

	private CondNode(Environment env) {
		super(env);
		type = NodeType.COND;
	}

	@Override
	public boolean parse() throws Exception {
		//Expected input: Expr operator Expr

		left = ExprNode.getHandler(env.getInput().peek(), env); //May throw Exception
		left.parse();

		if(OPERATORS.contains(env.getInput().peek().getType())) {
			operator = env.getInput().get().getType();
		} else {
			throw new Exception("Syntax Error: Invalid operator in CondNode");
		}

		right = ExprNode.getHandler(env.getInput().peek(), env); //May throw Exception
		right.parse();

		return true;
	}

	@Override
	public String toString() {
		String opOut = null;
		switch(operator) {
		case EQ:
			opOut = "=";
			break;
		case NE:
			opOut = "!=";
			break;
		case GT:
			opOut = ">";
			break;
		case GE:
			opOut = ">=";
			break;
		case LT:
			opOut = "<";
			break;
		case LE:
			opOut = "<=";
			break;
		default:
		}
		return "[Cond: " + left + " " + opOut + " " + right + "]";
	}

	@Override
	public Value getValue() throws Exception {
		if(left == null || right == null) {
			throw new Exception("Calculation Error: One or more operands were null in CondNode");
		}
		Value leftVal = left.getValue();
		Value rightVal = right.getValue();
		if(leftVal == null || rightVal == null) {
			throw new Exception("Calculation Error: One or more operands were null in CondNode");
		}

		if(leftVal.getType() == ValueType.STRING || rightVal.getType() == ValueType.STRING) {
			switch(operator) {
			case EQ:
				return new ValueImpl(leftVal.getSValue().equals(rightVal.getSValue()));
			case NE:
				return new ValueImpl(!leftVal.getSValue().equals(rightVal.getSValue()));
			default:
				throw new Exception("Calculation Error: Invalid calculation type was attempted against a String value");
			}
		}

		switch(operator) {
		case EQ:
			return new ValueImpl(leftVal.getDValue() == rightVal.getDValue());
		case NE:
			return new ValueImpl(leftVal.getDValue() != rightVal.getDValue());
		case GT:
			return new ValueImpl(leftVal.getDValue() > rightVal.getDValue());
		case GE:
			return new ValueImpl(leftVal.getDValue() >= rightVal.getDValue());
		case LT:
			return new ValueImpl(leftVal.getDValue() < rightVal.getDValue());
		case LE:
			return new ValueImpl(leftVal.getDValue() <= rightVal.getDValue());
		default:
			throw new Exception("Calculation Error: Invalid operator was set in CondNode");
		}
	}
}
