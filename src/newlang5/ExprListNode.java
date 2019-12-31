package newlang5;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

//For keeping function arguments
public class ExprListNode extends Node {
	List<Node> exprList = new ArrayList<Node>();

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
			throw new Exception("Syntax Error: Invalid start for ExprListNode");
		} else {
			return new ExprListNode(env);
		}
	}

	private ExprListNode(Environment env) {
		super(env);
		type = NodeType.EXPR_LIST;
	}

	@Override
	public boolean parse() throws Exception {
		Node handler = null;

		//Function arguments are comma separated
		while(true) {
			handler = ExprNode.getHandler(env.getInput().peek(), env); //May throw Exception
			handler.parse();
			exprList.add(handler);

			if(env.getInput().peek().getType() == LexicalType.COMMA) {
				env.getInput().get();
			} else {
				break;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		String out = "";
		for(int i = 0; i < exprList.size(); i++) {
			out += "[Expr: " + exprList.get(i).toString() + "]";
			if(i != exprList.size()-1) {
				out += " , ";
			}
		}
		return out;
	}

	public Value getValue(int i) throws Exception {
		if(i < exprList.size()) {
			return exprList.get(i).getValue();
		} else {
			return null;
		}
	}

	public List<Value> getValues() throws Exception {
		List<Value> values = new ArrayList<Value>();
		for(int i = 0; i < exprList.size(); i++) {
			values.add(exprList.get(i).getValue());
		}
		return values;
	}
}
