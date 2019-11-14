package newlang3;

public class Main {

	public static void main(String[] args) {
		LexicalUnit unit;
		String fname;
		if(args.length != 0) {
			fname = args[0];
		}else {
			fname = "sample.bas";
		}
		LexicalAnalyzer analyzedContent = new LexicalAnalyzerImpl(fname);

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
