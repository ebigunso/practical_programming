package newlang4;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExprNode extends Node {
	List<Node> operandNodes = new ArrayList<Node>();
	List<Node> operatorNodes = new ArrayList<Node>();

	private static final Set<LexicalType> FIRSTSET = EnumSet.of(
			LexicalType.NAME,
			LexicalType.INTVAL,
			LexicalType.DOUBLEVAL,
			LexicalType.LITERAL,
			LexicalType.SUB,
			LexicalType.LP
			);

	public static boolean isFirst(LexicalUnit unit) {
		return FIRSTSET.contains(unit.getType());
	}

	public static Node getHandler(LexicalUnit first, Environment env) throws Exception {
		if(!isFirst(first)){
			throw new Exception("Syntax Error: Invalid start for ExprNode");
		} else {
			return new ExprNode(env);
		}
	}

	private static final Map<LexicalType, Integer> OPERATORS = new HashMap<LexicalType, Integer>();
	static {
		OPERATORS.put(LexicalType.DIV, 1);
		OPERATORS.put(LexicalType.MUL, 1);
		OPERATORS.put(LexicalType.SUB, 2);
		OPERATORS.put(LexicalType.ADD, 2);
	}

	private ExprNode(Environment env) {
		super(env);
		type = NodeType.EXPR;
	}

	@Override
	public boolean parse() throws Exception {
		while(true) {
			Node operand = getOperand(); //May throw Exception
			operandNodes.add(operand);

			//getOperator() returns null if the next unit is not an operator
			Node operator = getOperator();
			if(operator == null) {
				break;
			}
			operatorNodes.add(operator);
		}

		return true;
	}

	private Node getOperand() throws Exception {
		switch(env.getInput().peek().getType()) {
		case LP:
			//Read LP, give the next LexicalUnit to a new ExprNode
			//That will come back on a RP, or a non-operator invalid unit
			env.getInput().get();
			Node exprHandler = ExprNode.getHandler(env.getInput().peek(), env); //May throw Exception
			exprHandler.parse();
			if(env.getInput().expect(LexicalType.RP)) {
				env.getInput().get();
			} else {
				throw new Exception("Syntax Error: Missing \")\" in ExprNode");
			}
			return exprHandler;
		case SUB:
			LexicalUnit peeked2 = env.getInput().peek(2);
			if (ExprNode.isFirst(peeked2)
					&& !(peeked2.getType() == LexicalType.SUB)
					&& !(peeked2.getType() == LexicalType.LITERAL)) {
				//NAME, INTVAL, DOUBLEVAL, or LP comes in
				//"-X" is translated to "-1 * X"
				env.getInput().get();
				LexicalUnit negative = new LexicalUnit(LexicalType.INTVAL, new ValueImpl(-1));
				operandNodes.add(ConstNode.getHandler(negative, env));
				operatorNodes.add(BinExprNode.getHandler(LexicalType.MUL));
				return getOperand();
			} else {
				throw new Exception("Syntax Error: Invalid unit after \"-\" in ExprNode");
			}
		case INTVAL:
		case DOUBLEVAL:
		case LITERAL:
			Node constHandler = ConstNode.getHandler(env.getInput().get(), env);
			return constHandler;
		case NAME:
			if(env.getInput().expect(2, LexicalType.LP)) {
//				Node funcHandler = CallFuncNode.getHandler(env.getInput().peek(), env);
//				funcHandler.parse();
//				return funcHandler;
			} else {
				Node varHandler = VariableNode.getHandler(env.getInput().peek().getType(), env);
				env.getInput().get();
				return varHandler;
			}
			break;
		default:
			throw new Exception("Syntax Error: Invalid start for ExprNode");
		}

		return null;
	}

	private Node getOperator() throws Exception {
		//Signal end of Expr when next unit is not an operator
		LexicalUnit peeked = env.getInput().peek();
		if(!OPERATORS.containsKey(peeked.getType())) {
			return null;
		}

		//BinExprNode.getHandler() expects LexicalType of operator as input
		return BinExprNode.getHandler(env.getInput().get().getType());
	}

	@Override
	public String toString() {
		String out = "";
		for(int i = 0; (i < operandNodes.size()) && (i < operatorNodes.size()+1); i++) {
			out += operandNodes.get(i).toString();
			if(i >= operatorNodes.size()) {
				break;
			} else {
				out += " ";
			}
			out += operatorNodes.get(i).toString() + " ";
		}
		return out;
	}

	@Override
	public Value getValue() {
		//todo
		return null;
	}
}
