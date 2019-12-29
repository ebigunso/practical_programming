package newlang4;

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

		right = ExprNode.getHandler(env.getInput().peek(), env); //May throw Exception
		right.parse();

		if(OPERATORS.contains(env.getInput().peek().getType())) {
			operator = env.getInput().get().getType();
		} else {
			throw new Exception("Syntax Error: Invalid operator in CondNode");
		}

		left = ExprNode.getHandler(env.getInput().peek(), env); //May throw Exception
		left.parse();

		return true;
	}

	@Override
	public String toString() {
		return "[Cond: " + left + " " + operator + " " + right + "]";
	}

	@Override
	public Value getValue() {
		//todo
		return null;
	}
}
