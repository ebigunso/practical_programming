package newlang5;

import java.util.EnumSet;
import java.util.Set;

public class CallFuncNode extends Node {
	String funcName = null;
	Node arguments = null;

	private static final Set<LexicalType> FIRSTSET = EnumSet.of(
			LexicalType.NAME
			);

	public static boolean isFirst(LexicalUnit unit) {
		return FIRSTSET.contains(unit.getType());
	}

	public static Node getHandler(LexicalUnit first, Environment env) throws Exception {
		if(!isFirst(first)){
			throw new Exception("Syntax Error: Invalid start for CallFuncNode");
		} else {
			return new CallFuncNode(env);
		}
	}

	private CallFuncNode(Environment env) {
		super(env);
		type = NodeType.FUNCTION_CALL;
	}

	@Override
	public boolean parse() throws Exception {
		//Expected input: funcName(arguments)

		//Function name
		if(env.getInput().peek().getType() == LexicalType.NAME) {
			funcName = env.getInput().get().getValue().getSValue();
		} else {
			throw new Exception("Syntax Error: \"" + env.getInput().peek() + "\" is not a valid function name");
		}

		//LP
		if(env.getInput().peek().getType() == LexicalType.LP) {
			env.getInput().get();
		} else {
			throw new Exception("Syntax Error: Missing ( while processing CallFuncNode");
		}

		//Arguments
		if(env.getInput().peek().getType() == LexicalType.RP) {
			//If there is no argument, do nothing
		} else if(ExprListNode.isFirst(env.getInput().peek())) {
			arguments = ExprListNode.getHandler(env.getInput().peek(), env);
			arguments.parse();
		} else {
			throw new Exception("Syntax Error: Invalid start for argument in CallFuncNode");
		}

		//RP
		if(env.getInput().peek().getType() == LexicalType.RP) {
			env.getInput().get();
		} else {
			throw new Exception("Syntax Error: Missing ) while processing CallFuncNode");
		}

		return true;
	}

	@Override
	public String toString() {
		if(arguments == null) {
			return "CallFunc: " + funcName + "()";
		} else {
			return "CallFunc: " + funcName + "(" + arguments + ")";
		}
	}

	@Override
	public Value getValue() {
		//todo
		return null;
	}

}
