package b100.json.element;

import b100.utils.InvalidCharacterException;
import b100.utils.StringReader;
import b100.utils.StringWriter;

public class JsonBoolean implements JsonElement{
	
	public boolean value;
	
	public JsonBoolean(boolean value) {
		this.value = value;
	}
	
	public JsonBoolean(StringReader reader) {
		if(reader.isNext("false")) {
			this.value = false;
			reader.skip(5);
		}
		else if(reader.isNext("true")) {
			this.value = true;
			reader.skip(4);
		}
		else throw new InvalidCharacterException(reader);
	}
	
	public String toString() {
		return "" + value;
	}

	public void write(StringWriter writer) {
		writer.write("" + value);
	}
	
}
