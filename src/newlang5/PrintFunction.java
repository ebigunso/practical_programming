package newlang5;

public class PrintFunction extends Function {
	public PrintFunction() {
	}

	@Override
	public Value invoke(ExprListNode args) throws Exception {
		String out;
		if(args == null) {
			out = null;
		} else {
			out = args.getValue(0).getSValue();
		}
		System.out.print(out);
		return null;
	}
}
