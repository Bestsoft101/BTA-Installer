package b100.xml.element;

import b100.utils.InvalidCharacterException;
import b100.utils.StringReader;
import b100.utils.StringWriter;

public class XmlAttribute{
	
	private String id;
	private String value;
	
	public XmlAttribute(String id, String value) {
		super();
		this.id = id;
		this.value = value;
	}
	
	public static XmlAttribute read(StringReader reader) {
		String id = "";
		String value = "";
		
		while(true) {
			if(reader.get() == '=') {
				reader.next();
				break;
			}
			if(reader.get() == ' ') {
				throw new InvalidCharacterException(reader);
			}
			
			id += reader.get();
			reader.next();
		}
		
		reader.expectAndSkip("\"");
		
		while(true) {
			if(reader.get() == '"') {
				reader.next();
				break;
			}else {
				value += reader.get();
				reader.next();
			}
		}
		
		return new XmlAttribute(id, value);
	}

	public StringWriter write(StringWriter writer) {
		writer.write(id + "=\"" + value + "\"");
		return writer;
	}
	
	
	
}
