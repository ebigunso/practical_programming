package newlang3;

import java.io.*;
import java.util.HashMap;

public class LexicalAnalyzerImpl implements LexicalAnalyzer {
	static HashMap<String, LexicalType> reservedWords;
	static HashMap<String, LexicalType> reservedSpecialChars;
	File file;
	FileReader fileReader;
	PushbackReader pushbackReader;

	static {
		reservedWords = new HashMap<String, LexicalType>();
		reservedSpecialChars = new HashMap<String, LexicalType>();
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

		//Set reserved special characters
		reservedSpecialChars.put("=", LexicalType.EQ);
		reservedSpecialChars.put("<", LexicalType.LT);
		reservedSpecialChars.put(">", LexicalType.GT);
		reservedSpecialChars.put("<=", LexicalType.LE);
		reservedSpecialChars.put("=<", LexicalType.LE);
		reservedSpecialChars.put(">=", LexicalType.GE);
		reservedSpecialChars.put("=>", LexicalType.GE);
		reservedSpecialChars.put("<>", LexicalType.NE);
		reservedSpecialChars.put("\n", LexicalType.NL); //When reading, fix all \r\n and \r to \n
		reservedSpecialChars.put(".", LexicalType.DOT);
		reservedSpecialChars.put("+", LexicalType.ADD);
		reservedSpecialChars.put("-", LexicalType.SUB);
		reservedSpecialChars.put("*", LexicalType.MUL);
		reservedSpecialChars.put("/", LexicalType.DIV);
		reservedSpecialChars.put("(", LexicalType.LP);
		reservedSpecialChars.put(")", LexicalType.RP);
		reservedSpecialChars.put(",", LexicalType.COMMA);
		reservedSpecialChars.put(String.valueOf((char)(-1)), LexicalType.EOF);
	}

	//Open file, and set reserved words and defined special characters
	public LexicalAnalyzerImpl(String fname) {
		try {
			file = new File(fname);
			fileReader = new FileReader(file);
			pushbackReader = new PushbackReader(fileReader);
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public LexicalUnit get() throws Exception {
		//Note: -1 is returned for EOF
		int readchar = pushbackReader.read();
		if(readchar == -1) {
			return new LexicalUnit(LexicalType.EOF);
		}
		String strReadchar = String.valueOf((char)readchar);

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
		}else if(strReadchar.matches("\r|\n")) {
			return getNewLineUnit(strReadchar);
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
			throw new IOException("Invalid character input for getStringUnit(String): " + inputchar);
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

	private LexicalUnit getNewLineUnit(String inputchar) throws IOException {
		if(!inputchar.matches("\r|\n")) {
			throw new IOException("Invalid character input for getNewLineUnit(): " + inputchar);
		}
		//inputchar is either \r or \n
		char pushbackTmp;

		//Recognize "\r" and "\r\n" as LexicalType.NL
		//Check if it's "\r\n" if not pushback one
		if(inputchar.matches("\r")) {
			pushbackTmp = (char)pushbackReader.read();
			inputchar = String.valueOf(pushbackTmp);
			if(!inputchar.matches("\n")) {
				pushbackReader.unread(pushbackTmp);
			}
			return new LexicalUnit(LexicalType.NL);
		} else {
			return new LexicalUnit(LexicalType.NL);
		}
	}

	private LexicalUnit getSpecialUnit(String inputchar) throws IOException {
		String outputValue = "";
		char pushbackTmp;

		//Read one, only two characters "<,>" has a valid second character
		//If it's one of those, check if second character is a valid two character special code
		//If not pushback one
		outputValue += inputchar;
		if(inputchar.matches("<|>")) {
			String firstChar = inputchar;
			pushbackTmp = (char)pushbackReader.read();
			inputchar = String.valueOf(pushbackTmp);
			switch(firstChar) {
			case "<":
				if(inputchar.matches("=|>")) {
					outputValue += inputchar;
				} else {
					pushbackReader.unread(pushbackTmp);
				}
				break;
			case ">":
				if(inputchar.matches("=")) {
					outputValue += inputchar;
				} else {
					pushbackReader.unread(pushbackTmp);
				}
				break;
			}
		}

		//If resulting String is a reserved word, generate LexicalUnit with that Type
		//Else throw an IOException
		if(reservedSpecialChars.containsKey(outputValue)) {
			return new LexicalUnit(reservedSpecialChars.get(outputValue));
		}else {
			throw new IOException("Invalid character input for getSpecialUnit(String): " + outputValue);
		}
	}

}
