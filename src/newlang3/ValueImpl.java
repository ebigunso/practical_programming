package newlang3;

public class ValueImpl extends Value {
	ValueType type;
	String stringVal;
	int intVal;
	double doubleVal;
	boolean boolVal;

	//Set value to corresponding field by type, then set type
	public ValueImpl(String s) {
		super(s);
		stringVal = s;
		type = ValueType.STRING;
	}

	public ValueImpl(int i) {
		super(i);
		intVal = i;
		type = ValueType.INTEGER;
	}

	public ValueImpl(double d) {
		super(d);
		doubleVal = d;
		type = ValueType.DOUBLE;
	}

	public ValueImpl(boolean b) {
		super(b);
		boolVal = b;
		type = ValueType.BOOL;
	}

	public ValueImpl(String s, ValueType t) {
		super(s, t);
		switch(t) {
		case STRING:
			stringVal = s;
			break;
		case INTEGER:
			intVal = Integer.parseInt(s);
			break;
		case DOUBLE:
			doubleVal = Double.parseDouble(s);
			break;
		case BOOL:
			boolVal = Boolean.parseBoolean(s);
			break;
		case VOID:
			break;
		}
		type = t;
	}

	@Override
	public String get_sValue() {
		return getSValue();
	}

	@Override
	public String getSValue() {
		switch(type) {
		case STRING:
			return stringVal;
		case INTEGER:
			return String.valueOf(intVal);
		case DOUBLE:
			return String.valueOf(doubleVal);
		case BOOL:
			return String.valueOf(boolVal);
		default:
			return null;
		}
	}

	@Override
	public int getIValue() {
		switch(type) {
		case STRING:
			return Integer.parseInt(stringVal);
		case INTEGER:
			return intVal;
		case DOUBLE:
			return (int)doubleVal;
		case BOOL:
			return boolVal ? 1 : 0;
		default:
			return 0;
		}
	}

	@Override
	public double getDValue() {
		switch(type) {
		case STRING:
			return Double.parseDouble(stringVal);
		case INTEGER:
			return (double)intVal;
		case DOUBLE:
			return doubleVal;
		case BOOL:
			return boolVal ? 1.0 : 0.0;
		default:
			return 0.0;
		}
	}

	@Override
	public boolean getBValue() {
		switch(type) {
		case STRING:
			return Boolean.parseBoolean(stringVal);
		case INTEGER:
			return (intVal != 0) ? true : false;
		case DOUBLE:
			return (doubleVal != 0) ? true : false;
		case BOOL:
			return boolVal;
		default:
			return false;
		}
	}

	@Override
	public ValueType getType() {
		return type;
	}

}
