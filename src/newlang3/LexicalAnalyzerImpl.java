package newlang3;

import java.io.*;

public class LexicalAnalyzerImpl implements LexicalAnalyzer {
	File file;
	FileReader filereader;

	//Read file and create lexical units
	public LexicalAnalyzerImpl(String fname) {
		try {
			file = new File(fname);
			filereader = new FileReader(file);
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public LexicalUnit get() throws Exception {
		String strReadchar = String.valueOf((char)filereader.read());

		//Skip if start of unit is a Space or a Tab. Those cases are invalid.
		if(strReadchar.matches(" |\t")) {
			strReadchar = String.valueOf((char)filereader.read());
		}

		if(strReadchar.matches("[0-9]")) {
			return intget();
		}else if(strReadchar.matches("[a-z]|[A-Z]")) {
			return stringGet();
		}else if(strReadchar.matches("\"")) {
			return literalGet();
		}

		return specialGet();
	}

	@Override
	public boolean expect(LexicalType type) throws Exception {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public void unget(LexicalUnit token) throws Exception {
		// TODO 自動生成されたメソッド・スタブ

	}

}
