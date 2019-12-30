package newlang5;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExprNode extends Node {
	List<Node> operandNodes = new ArrayList<Node>();
	List<BinExprNode> binExprNodes = new ArrayList<BinExprNode>();
	Node primedNode = null; //Final result of setBinExprNodes() goes in here

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
			BinExprNode operator = getOperator();
			if(operator == null) {
				break;
			}
			binExprNodes.add(operator);
		}

		if(operandNodes.size() == 1 && binExprNodes.size() == 0) {
			//For cases with only one operand
			primedNode = operandNodes.get(0);
		} else {
			primedNode = setBinExprNodes();
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
				binExprNodes.add(BinExprNode.getHandler(LexicalType.MUL));
				return getOperand();
			} else {
				throw new Exception("Syntax Error: Invalid unit after \"-\" in ExprNode");
			}
		case INTVAL:
		case DOUBLEVAL:
		case LITERAL:
			Node constHandler = ConstNode.getHandler(env.getInput().peek(), env);
			env.getInput().get();
			return constHandler;
		case NAME:
			if(env.getInput().expect(2, LexicalType.LP)) {
				Node funcHandler = CallFuncNode.getHandler(env.getInput().peek(), env);
				funcHandler.parse();
				return funcHandler;
			} else {
				Node varHandler = VariableNode.getHandler(env.getInput().peek().getType(), env);
				env.getInput().get();
				return varHandler;
			}
		default:
			throw new Exception("Syntax Error: Invalid start for ExprNode");
		}
	}

	private BinExprNode getOperator() throws Exception {
		//Signal end of Expr when next unit is not an operator
		LexicalUnit peeked = env.getInput().peek();
		if(!OPERATORS.containsKey(peeked.getType())) {
			return null;
		}

		//BinExprNode.getHandler() expects LexicalType of operator as input
		return BinExprNode.getHandler(env.getInput().get().getType());
	}

	private Node setBinExprNodes() throws Exception {
		if(operandNodes.size() < 2 || binExprNodes.size() < 1) {
			throw new Exception("Parsing Error: Not enough operands or operators in ExprNode");
		}
		boolean hasPending = false;
		int pendingID = 999;
		int priorityPrev, priorityNow;

		binExprNodes.get(0).setLeft(operandNodes.get(0));
		for(int i = 1; i < operandNodes.size()-1; i++) {
			priorityPrev = binExprNodes.get(i-1).getOperatorPriority();
			priorityNow = binExprNodes.get(i).getOperatorPriority();
			if(priorityPrev == priorityNow) {
				//Take current operand and setRight() it to the previous binExprNode
				//Then take previous binExprNode and setLeft() it to the current binExprNode
				binExprNodes.get(i-1).setRight(operandNodes.get(i));
				binExprNodes.get(i).setLeft(binExprNodes.get(i-1));
			} else if(priorityPrev > priorityNow) {
				//When current priority is higher
				hasPending = true;
				pendingID = i-1;
				binExprNodes.get(i).setLeft(operandNodes.get(i));
			} else {
				//When current priority is lower
				binExprNodes.get(i-1).setRight(operandNodes.get(i));
				if(!hasPending) {
					binExprNodes.get(i).setLeft(binExprNodes.get(i-1));
				} else {
					binExprNodes.get(pendingID).setRight(binExprNodes.get(i-1));
					binExprNodes.get(i).setLeft(binExprNodes.get(pendingID));
					hasPending = false;
				}
			}
		}
		BinExprNode lastBinExpr = binExprNodes.get(operandNodes.size()-2);
		Node lastOperand = operandNodes.get(operandNodes.size()-1);
		lastBinExpr.setRight(lastOperand);

		//For when Expr ends on a high priority operator
		if(hasPending) {
			binExprNodes.get(pendingID).setRight(lastBinExpr);
			return binExprNodes.get(pendingID);
		}

		return lastBinExpr;
	}

	@Override
	public String toString() {
		return primedNode.toString();
	}

	@Override
	public Value getValue() throws Exception {
		return primedNode.getValue();
	}
}
