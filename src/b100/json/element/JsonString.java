package b100.json.element;

import b100.utils.InvalidCharacterException;
import b100.utils.StringReader;
import b100.utils.StringWriter;

public class JsonString implements JsonElement{
	
	public String value;
	
	public JsonString(String value) {
		this.value = value;
	}
	
	public JsonString(StringReader reader) {
		StringBuilder builder = new StringBuilder();
		
		reader.expectAndSkip('"');
		
		while(true) {
			if(reader.get() == '"') {
				reader.next();
				break;
			}else if(reader.get() == '\\') {
				reader.next();
				char next = reader.get();
				if(next == 'n' || next == 'N') {
					builder.append('\n');
				} else if(next == '\\'){
					builder.append('\\');
				}else {
					throw new InvalidCharacterException(reader);
				}
				reader.next();
			}else {
				builder.append(reader.getAndSkip());
			}
		}
		
		this.value = builder.toString();
	}
	
	public boolean equals(JsonString string2) {
		return value.equals(string2.value);
	}
	
	public String toString() {
		return value;
	}

	public void write(StringWriter writer) {
		writer.write("\"");
		
		for(int i=0; i < value.length(); i++) {
			char c = value.charAt(i);
			
			if(c == '\n') {
				writer.write("\\n");
			}
			
			else if(c == '\t') {
				writer.write("\\t");
			}
			else if(c == '\\') {
				writer.write("\\\\");
			}
			else {
				writer.write(c);
			}
		}
		
		writer.write("\"");
	}
	
}
