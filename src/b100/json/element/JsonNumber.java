package b100.json.element;

import static b100.utils.Utils.*;

import b100.utils.InvalidCharacterException;
import b100.utils.StringReader;
import b100.utils.StringWriter;

public class JsonNumber implements JsonElement{
	
	public Number value;
	
	public JsonNumber(Number number) {
		this.value = requireNonNull(number);
	}
	
	public JsonNumber(int i) {
		this.value = i;
	}
	
	public JsonNumber(long l) {
		this.value = l;
	}
	
	public JsonNumber(double d) {
		this.value = d;
	}
	
	public JsonNumber(float f) {
		this.value = f;
	}
	
	public JsonNumber(StringReader reader) {
		//https://www.json.org/json-en.html
		StringWriter numberString = new StringWriter();
		
		boolean decimal = false;
		
		if(reader.get() == '-') {
			numberString.write(reader.get());
			reader.next();
		}
		
		readInteger(reader, numberString);
		if(reader.get() == '.') {
			readDecimal(reader, numberString);
			decimal = true;
		}
		
		if(reader.get() == 'e' || reader.get() == 'E') {
			readExponent(reader, numberString);
		}
		
		if(reader.get() == ',' || reader.get() == '}' || reader.get() == ']' || reader.isWhitespace(reader.get())) {
			String str = numberString.toString();
			
			if(decimal) {
				float floatVal = 0.0f;
				double doubleVal = 0.0;
				
				try {
					floatVal = Float.parseFloat(str);
				}catch (Exception e) {}
				doubleVal = Double.parseDouble(str);
				
				if(floatVal == doubleVal) {
					value = floatVal;
				}else {
					value = doubleVal;
				}
			}else {
				int intVal = 0;
				long longVal = 0L;
				
				try {
					intVal = Integer.parseInt(str);
				}catch (Exception e) {}
				longVal = Long.parseLong(str);
				
				if(intVal == longVal) {
					value = intVal;
				}else {
					value = longVal;
				}
			}
		}else {
			throw new InvalidCharacterException(reader);
		}
	}
	
	private void readInteger(StringReader reader, StringWriter numberString) {
		while(true) {
			if(reader.get() == '0') {
				numberString.write(reader.get());
				reader.next();
				return;
			}else if(reader.get() >= '1' && reader.get() <= '9') {
				numberString.write(reader.get());
				reader.next();
				while(true) {
					if(reader.get() >= '0' && reader.get() <= '9') {
						numberString.write(reader.get());
						reader.next();
					}else {
						return;
					}
				}
			}else {
				return;
			}
		}
	}
	
	private void readDecimal(StringReader reader, StringWriter numberString) {
		reader.expectAndSkip('.');
		numberString.write('.');
		while(true) {
			if(reader.get() >= '0' && reader.get() <= '9') {
				numberString.write(reader.get());
				reader.next();
			}else {
				return;
			}
		}
	}
	
	private void readExponent(StringReader reader, StringWriter numberString) {
		reader.expectOne("eE");
		numberString.write(reader.get());
		reader.next();
		
		if(reader.get() == '+' || reader.get() == '-') {
			numberString.write(reader.get());
			reader.next();
		}
		
		while(true) {
			if(reader.get() >= '0' && reader.get() <= '9') {
				numberString.write(reader.get());
				reader.next();
			}else {
				break;
			}
		}
	}
	
	public void write(StringWriter writer) {
		writer.write(value.toString());
	}
	
	public int getInteger() {
		return value.intValue();
	}
	
	public double getDouble() {
		return value.doubleValue();
	}
	
	public float getFloat() {
		return value.floatValue();
	}
	
	public long getLong() {
		return value.longValue();
	}
	
	public byte getByte() {
		return value.byteValue();
	}
	
	public short getShort() {
		return value.shortValue();
	}
	
	public void set(Number n) {
		this.value = requireNonNull(n);
	}
	
	public void set(int i) {
		this.value = i;
	}
	
	public void set(long l) {
		this.value = l;
	}
	
	public void set(float f) {
		this.value = f;
	}
	
	public void set(double d) {
		this.value = d;
	}
	
	public boolean isInteger() {
		return this.value instanceof Integer;
	}
	
	public boolean isLong() {
		return this.value instanceof Long;
	}
	
	public boolean isFloat() {
		return this.value instanceof Float;
	}
	
	public boolean isDouble() {
		return this.value instanceof Double;
	}
	
}
