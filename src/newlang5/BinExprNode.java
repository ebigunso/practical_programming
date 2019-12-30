package newlang5;

import java.util.HashMap;
import java.util.Map;

public class BinExprNode extends Node {
	Node left = null, right = null;
	LexicalType op = null;


	private static final Map<LexicalType, Integer> OPERATORS = new HashMap<LexicalType, Integer>();
	static {
		OPERATORS.put(LexicalType.DIV, 1);
		OPERATORS.put(LexicalType.MUL, 1);
		OPERATORS.put(LexicalType.SUB, 2);
		OPERATORS.put(LexicalType.ADD, 2);
	}

	public static boolean isOperator(LexicalType inputType) {
		return OPERATORS.containsKey(inputType);
	}

	public static BinExprNode getHandler(LexicalType operator) throws Exception {
		if(!isOperator(operator)){
			throw new Exception("Syntax Error: Input for BinExprNode is not a valid operator");
		} else {
			return new BinExprNode(operator);
		}
	}

	private BinExprNode(LexicalType operator) {
		op = operator;
		type = NodeType.BIN_EXPR;
	}

	public void setLeft(Node leftNode) {
		left = leftNode;
	}

	public void setRight(Node rightNode) {
		right = rightNode;
	}

	public int getOperatorPriority() {
		return OPERATORS.get(op);
	}

	@Override
	public boolean parse() throws Exception {
		throw new Exception("Parsing Error: Parse cannnot be run for BinExprNode");
	}

	@Override
	public String toString() {
		String operator = "";
		switch(op) {
		case DIV:
			operator = "/";
			break;
		case MUL:
			operator = "*";
			break;
		case SUB:
			operator = "-";
			break;
		case ADD:
			operator = "+";
			break;
		default:
		}
		return operator + "[" + left.toString() + "," + right.toString() + "]" ;
	}

	@Override
	public Value getValue() {
		//todo
		return null;
	}

}
