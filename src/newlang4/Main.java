package newlang4;

import java.io.FileInputStream;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
	      	FileInputStream fin = null;
	        LexicalAnalyzer lex;
	        LexicalUnit		first;
	        Environment		env;

	        System.out.println("basic parser");
	        fin = new FileInputStream(args[0]);
	        lex = new LexicalAnalyzerImpl(fin);
	        env = new Environment(lex);
	        first = lex.peek();

	        try {
	        	Node handler = Program.getHandler(first, env);
	        	handler.parse();
	        	System.out.println(handler);
	        	System.out.println("value = " + handler.getValue());
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	}

}
