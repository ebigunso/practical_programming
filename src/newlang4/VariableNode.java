package newlang4;

public class VariableNode extends Node {
	   String var_name;
	    Value v;

	    /** Creates a new instance of variable */
	    public VariableNode(String name) {
	        var_name = name;
	        type = NodeType.VAR;
	    }
	    public VariableNode(LexicalUnit u) {
	        var_name = u.getValue().getSValue();
	        type = NodeType.VAR;
	    }

	    public static boolean isFirst(LexicalType first) {
	        return (first == LexicalType.NAME);
	    }

	    public static Node getHandler(LexicalType first, Environment my_env) {
	        if (first == LexicalType.NAME) {
	        	VariableNode vn;

	        	try {
		        	LexicalUnit lu = my_env.getInput().get();
		        	my_env.getInput().unget(lu);
		            String s = lu.getValue().getSValue();
		            vn = my_env.getVariable(s);
		            return vn;
	        	}
	        	catch(Exception e) {}
	        }
	        return null;
	    }

	    public void setValue(Value my_v) {
	        v = my_v;
	    }

	    public Value getValue() {
	        return v;
	    }

	    public String toString() {
	    	String str = "[Var: " + var_name +"]";

	    	return str;
	    }

}
