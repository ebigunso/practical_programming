package newlang4;

import java.io.*;
import java.util.*;

public class LexicalAnalyzerImpl implements LexicalAnalyzer {
	private static Map<String, LexicalType> reservedWords;
	private static Map<String, LexicalType> reservedSpecialChars;
	private List<LexicalUnit> ungetBuffer;
	private InputStreamReader inputStreamReader;
	private PushbackReader pushbackReader;

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
		reservedSpecialChars.put(">=", LexicalType.GE);
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

	//Create reader and buffer
	public LexicalAnalyzerImpl(FileInputStream fin) {
		inputStreamReader = new InputStreamReader(fin);
		pushbackReader = new PushbackReader(inputStreamReader);
		ungetBuffer = new ArrayList<LexicalUnit>();
	}

	@Override
	public LexicalUnit get() throws Exception {
		//Return from end of ungetBuffer if anything is inside
		if(!ungetBuffer.isEmpty()) {
			LexicalUnit retUnit = ungetBuffer.get(ungetBuffer.size()-1);
			ungetBuffer.remove(ungetBuffer.size()-1);
			return retUnit;
		}

		//Note: -1 is returned for EOF
		char readchar = (char)pushbackReader.read();
		String strReadchar = String.valueOf(readchar);

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

	//Returns true when "type" matches the type of the next LexicalUnit
	@Override
	public boolean expect(LexicalType type) throws Exception {
		return type == peek().getType();
	}

	//Do expect for LexicalUnit specified units ahead
	@Override
	public boolean expect(int ahead, LexicalType type) throws Exception {
		return type == peek(ahead).getType();
	}

	@Override
	public void unget(LexicalUnit token) throws Exception {
		ungetBuffer.add(token);
	}

	//Take a peek at the next LexicalUnit, not advancing the pointer
	@Override
	public LexicalUnit peek() throws Exception {
		LexicalUnit retUnit = get();
		unget(retUnit);

		return retUnit;
	}

	//Take a peek at the LexicalUnit specified units ahead
	@Override
	public LexicalUnit peek(int ahead) throws Exception {
		List<LexicalUnit> peekBuffer = new ArrayList<LexicalUnit>();
		LexicalUnit nowPeeking = null;
		for(int i = 0; i < ahead; i++) {
			nowPeeking = get();
			peekBuffer.add(nowPeeking);
		}

		while(!peekBuffer.isEmpty()) {
			int bufferEnd = peekBuffer.size() - 1;
			unget(peekBuffer.get(bufferEnd));
			peekBuffer.remove(bufferEnd);
		}

		return nowPeeking;
	}

	private LexicalUnit getIntUnit(String inputchar) throws Exception {
		if(!inputchar.matches("[0-9]")) {
			throw new Exception("Invalid character input for getIntUnit(String): " + inputchar);
		}
		//Read until invalid character appears
		//Anything but a number or the first dot followed by a number is invalid
		//When an invalid character appears, pushback one. If the previous character is a dot, pushback that also
		boolean hasDot = false;
		boolean previouslyDot = false;
		String outputValue = "";
		char pushbackTmp;

		do {
			previouslyDot = false;
			outputValue += inputchar;
			pushbackTmp = (char)pushbackReader.read();
			inputchar = String.valueOf(pushbackTmp);
			if(inputchar.matches("\\.")) {
				if(hasDot) {
					break;
				} else {
					hasDot = true;
					previouslyDot = true;
					//Hold off adding dot to output
					//Read next, if it's valid (ie. a number) add the dot and loop back to keep going
					pushbackTmp = (char)pushbackReader.read();
					inputchar = String.valueOf(pushbackTmp);
					if(inputchar.matches("[0-9]")) {
						outputValue += ".";
					}
				}
			}
		}while(inputchar.matches("[0-9]"));

		pushbackReader.unread(pushbackTmp);
		if(previouslyDot) {
			pushbackReader.unread('.');
		}

		if(hasDot) {
			return new LexicalUnit(LexicalType.DOUBLEVAL, new ValueImpl(Double.parseDouble(outputValue)));
		} else {
			return new LexicalUnit(LexicalType.INTVAL, new ValueImpl(Integer.parseInt(outputValue)));
		}
	}

	private LexicalUnit getStringUnit(String inputchar) throws Exception {
		if(!inputchar.matches("[a-z]|[A-Z]")) {
			throw new Exception("Invalid character input for getStringUnit(String): " + inputchar);
		}
		//Read until invalid character appears, then pushback one
		String outputValue = "";
		char pushbackTmp;
		do {
			outputValue += inputchar;
			pushbackTmp = (char)pushbackReader.read();
			inputchar = String.valueOf(pushbackTmp);
		}while(inputchar.matches("[0-9]|[a-z]|[A-Z]|_"));
		pushbackReader.unread(pushbackTmp);

		//If resulting String is a reserved word, generate LexicalUnit with that Type
		//Else generate NAME Type with String as Value
		if(reservedWords.containsKey(outputValue.toLowerCase())) {
			return new LexicalUnit(reservedWords.get(outputValue.toLowerCase()));
		}else {
			return new LexicalUnit(LexicalType.NAME, new ValueImpl(outputValue));
		}
	}

	private LexicalUnit getLiteralUnit(String inputchar) throws Exception {
		if(!inputchar.matches("\"")) {
			throw new Exception("Invalid character input for getLiteralUnit(String): " + inputchar);
		}
		//Read until a second " appears
		String outputValue = "";
		while(true) {
			inputchar = String.valueOf((char)pushbackReader.read());
			//If coder forgot to close a literal unit (EOF comes up while reading), throw an exception
			if(inputchar.matches(String.valueOf((char)-1))) {
				throw new Exception("EOF was read while reading a literal unit.");
			}
			//Break when literal is closed
			if(inputchar.matches("\"")) {
				break;
			}

			outputValue += inputchar;
		}

		//Generate a LITERAL type LexicalUnit with output as Value
		return new LexicalUnit(LexicalType.LITERAL, new ValueImpl(outputValue));
	}

	private LexicalUnit getNewLineUnit(String inputchar) throws Exception {
		if(!inputchar.matches("\r|\n")) {
			throw new Exception("Invalid character input for getNewLineUnit(): " + inputchar);
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

	private LexicalUnit getSpecialUnit(String inputchar) throws Exception {
		String outputValue ="";
		char pushbackTmp;

		//If inputchar is a reserved word, keep reading for a longest match
		//Once a longest match is found, generate LexicalUnit with that Type
		//Else throw an Exception
		if(reservedSpecialChars.containsKey(inputchar)) {
			do {
				outputValue += inputchar;
				pushbackTmp = (char)pushbackReader.read();
				inputchar = String.valueOf(pushbackTmp);
			}while(reservedSpecialChars.containsKey(outputValue + inputchar));
			pushbackReader.unread(pushbackTmp);

			return new LexicalUnit(reservedSpecialChars.get(outputValue.toString()));
		}else {
			throw new Exception("Invalid character input for getSpecialUnit(String): " + outputValue.toString());
		}
	}

}