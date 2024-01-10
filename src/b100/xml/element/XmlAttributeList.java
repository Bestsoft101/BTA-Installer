package b100.xml.element;

import java.util.ArrayList;

import b100.utils.StringReader;
import b100.utils.StringWriter;

public class XmlAttributeList extends ArrayList<XmlAttribute>{

	private static final long serialVersionUID = 1L;
	
	public static XmlAttributeList read(StringReader reader) {
		XmlAttributeList list = new XmlAttributeList();
		
		reader.skipWhitespace();
		while(true) {
			if(reader.get() == '>' || reader.get() == '?') {
				break;
			}
			
			list.add(XmlAttribute.read(reader));
			reader.skipWhitespace();
		}
		
		return list;
	}
	
	public StringWriter write(StringWriter writer) {
		for(XmlAttribute attribute : this) {
			writer.write(" ");
			attribute.write(writer);
		}
		
		return writer;
	}

}
