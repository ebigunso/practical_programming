package newlang3;

import java.io.*;
import java.util.HashMap;

public class LexicalAnalyzerImpl implements LexicalAnalyzer {
	HashMap<String, LexicalType> reservedWords;
	HashMap<String, LexicalType> definedSpecialChars;
	File file;
	FileReader fileReader;
	PushbackReader pushbackReader;

	//Open file, and set reserved words and defined special characters
	public LexicalAnalyzerImpl(String fname) {
		try {
			file = new File(fname);
			fileReader = new FileReader(file);
			pushbackReader = new PushbackReader(fileReader);
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}

		reservedWords = new HashMap<String, LexicalType>();
		definedSpecialChars = new HashMap<String, LexicalType>();
		//Set reserved words
		reservedWords.put("if", LexicalType.IF);
		reservedWords.put("then", LexicalType.THEN);
		reservedWords.put("else", LexicalType.ELSE);
		reservedWords.put("elseif", LexicalType.ELSEIF);
		reservedWords.put("endif", LexicalType.ENDIF);
		reservedWords.put("for", LexicalType.FOR);
		reservedWords.put("forall", LexicalType.FORALL);
		reservedWords.put("next", LexicalType.NEXT);
		reservedWords.put("sub", LexicalType.FUNC);
		reservedWords.put("dim", LexicalType.DIM);
		reservedWords.put("as", LexicalType.AS);
		reservedWords.put("end", LexicalType.END);
		reservedWords.put("while", LexicalType.WHILE);
		reservedWords.put("do", LexicalType.DO);
		reservedWords.put("until", LexicalType.UNTIL);
		reservedWords.put("loop", LexicalType.LOOP);
		reservedWords.put("to", LexicalType.TO);
		reservedWords.put("wend", LexicalType.WEND);

		//Set defined special characters
		definedSpecialChars.put("=", LexicalType.EQ);
		definedSpecialChars.put("<", LexicalType.LT);
		definedSpecialChars.put(">", LexicalType.GT);
		definedSpecialChars.put("<=", LexicalType.LE);
		definedSpecialChars.put("=<", LexicalType.LE);
		definedSpecialChars.put(">=", LexicalType.GE);
		definedSpecialChars.put("=>", LexicalType.GE);
		definedSpecialChars.put("<>", LexicalType.NE);
		definedSpecialChars.put("\n", LexicalType.NL); //When reading, fix all \r\n and \r to \n
		definedSpecialChars.put(".", LexicalType.DOT);
		definedSpecialChars.put("+", LexicalType.ADD);
		definedSpecialChars.put("-", LexicalType.SUB);
		definedSpecialChars.put("*", LexicalType.MUL);
		definedSpecialChars.put("/", LexicalType.DIV);
		definedSpecialChars.put("(", LexicalType.LP);
		definedSpecialChars.put(")", LexicalType.RP);
		definedSpecialChars.put(",", LexicalType.COMMA);
		definedSpecialChars.put(String.valueOf((char)-1), LexicalType.EOF);
	}

	@Override
	public LexicalUnit get() throws Exception {
		//Note: -1 is returned for EOF
		String strReadchar = String.valueOf((char)pushbackReader.read());

		//Skip if start of unit is a Space or a Tab. Those cases are invalid.
		//Keep reading until a valid starting character is found
		if(strReadchar.matches(" |\t")) {
			do {
				strReadchar = String.valueOf((char)pushbackReader.read());
			}while(strReadchar.matches(" |\t"));
		}

		if(strReadchar.matches("[0-9]")) {
			return getIntUnit(strReadchar);
		}else if(strReadchar.matches("[a-z]|[A-Z]")) {
			return getStringUnit(strReadchar);
		}else if(strReadchar.matches("\"")) {
			return getLiteralUnit(strReadchar);
		}

		return getSpecialUnit(strReadchar);
	}

	@Override
	public boolean expect(LexicalType type) throws Exception {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public void unget(LexicalUnit token) throws Exception {

	}

	private LexicalUnit getIntUnit(String inputchar) throws IOException {
		//todo
		return null;
	}

	private LexicalUnit getStringUnit(String inputchar) throws IOException {
		if(!inputchar.matches("[a-z]|[A-Z]")) {
			throw new IOException("Invalid character input for getStringUnit(String)");
		}
		//Read until invalid character appears, then pushback one
		String outputValue = "";
		char pushbackTmp;
		do {
			outputValue += inputchar;
			pushbackTmp = (char)pushbackReader.read();
			inputchar = String.valueOf(pushbackTmp);
		}while(inputchar.matches("[0-9]|[a-z]|[A-Z]"));
		pushbackReader.unread(pushbackTmp);

		//If resulting String is a reserved word, generate LexicalUnit with that Type
		//Else generate NAME Type with String as Value
		if(reservedWords.containsKey(outputValue.toLowerCase())) {
			return new LexicalUnit(reservedWords.get(outputValue.toLowerCase()));
		}else {
			return new LexicalUnit(LexicalType.NAME, new ValueImpl(outputValue));
		}
	}

	private LexicalUnit getLiteralUnit(String inputchar) throws IOException {
		//todo;
		return null;
	}

	private LexicalUnit getSpecialUnit(String inputchar) throws IOException {
		//Read until invalid character appears, then pushback one
		String outputValue = "";
		char pushbackTmp;
		do {
			outputValue += inputchar;
			pushbackTmp = (char)pushbackReader.read();
			inputchar = String.valueOf(pushbackTmp);
		}while(!inputchar.matches(" |\t|[0-9]|[a-z]|[A-Z]|\""));
		pushbackReader.unread(pushbackTmp);

		//If resulting String is a reserved word, generate LexicalUnit with that Type
		//Else throw an IOException
		if() {
			//todo
			return;
		}else {
			//todo
		}
		//todo
		return null;
	}

}
