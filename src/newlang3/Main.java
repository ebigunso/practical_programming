package newlang3;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main {

	public static void main(String[] args) {
		LexicalUnit unit;
		String fname;
		FileInputStream fin;
		if(args.length != 0) {
			fname = args[0];
		}else {
			fname = "sample.bas";
		}
		try {
			fin = new FileInputStream(fname);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		LexicalAnalyzer analyzedContent = new LexicalAnalyzerImpl(fin);

		while (true) {
			try {
				unit = analyzedContent.get();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			System.out.println(unit);
			if(unit.getType() == LexicalType.EOF) {
				break;
			}
		}

	}

}
