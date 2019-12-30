package newlang4;

import java.util.EnumSet;
import java.util.Set;

public class SubstNode extends Node {
	VariableNode leftVar = null;
	Node expr = null;

	private static final Set<LexicalType> FIRSTSET = EnumSet.of(
			LexicalType.NAME
			);

	public static boolean isFirst(LexicalUnit unit) {
		return FIRSTSET.contains(unit.getType());
	}

	public static Node getHandler(LexicalUnit first, Environment env) throws Exception {
		if(!isFirst(first)){
			throw new Exception("Syntax Error: Invalid start for SubstNode");
		} else {
			return new SubstNode(env);
		}
	}

	private SubstNode(Environment env) {
		super(env);
		type = NodeType.ASSIGN_STMT;
	}

	@Override
	public boolean parse() throws Exception {
		LexicalUnit nextUnit = env.getInput().peek();
		if(VariableNode.isFirst(nextUnit.getType())) {
			leftVar = (VariableNode)VariableNode.getHandler(env.getInput().peek().getType(), env);
			env.getInput().get();
		} else {
			throw new Exception("Syntax Error: Invalid start for SubstNode");
		}

		if(env.getInput().expect(LexicalType.EQ)) {
			env.getInput().get();
		} else {
			throw new Exception("Syntax Error: Missing \"=\" in SubstNode");
		}

		nextUnit = env.getInput().peek();
		if(ExprNode.isFirst(nextUnit)) {
			expr = ExprNode.getHandler(nextUnit, env);
			expr.parse();
		} else {
			throw new Exception("Syntax Error: Right side of SubstNode is not a valid ExprNode");
		}

		return true;
	}

	@Override
	public String toString() {
		return "Subst: " + leftVar.toString() + " = [Expr: " + expr.toString() + "]";
	}

	public Value getValue() {
		//todo
		return null;
	}
}
