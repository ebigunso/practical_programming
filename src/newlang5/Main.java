package newlang5;

import java.io.FileInputStream;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	       FileInputStream fin = null;
	        LexicalAnalyzer lex;
	        LexicalUnit		first;
	        Environment		env;
	        Node			prog;
	  
	        System.out.println("basic parser");
	        try {
	            fin = new FileInputStream("test.txt");
	        }
	        catch(Exception e) {
	            System.out.println("file not found");
	            System.exit(-1);
	        }
	        lex = new LexicalAnalyzerImpl(fin);
	        env = new Environment(lex);
	        
	        try {
		        first = lex.get();
		        lex.unget(first);
		        if (Program.isFirst(first)) {
		        	prog = Program.getHandler(first, env);
		        	if (prog != null && prog.parse()) {
		        		prog.getValue();
		        	}
		        	else System.out.println("syntax error");
		        }
		        else System.out.println("syntax error");
	        }
	        catch(Exception e) {
	        	System.out.println("execution error");
	        }
	}

}
