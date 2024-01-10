package b100.xml;

import b100.utils.StringReader;
import b100.utils.StringWriter;
import b100.xml.element.XmlAttributeList;
import b100.xml.element.XmlTag;

public class XmlFile{
	
	private XmlAttributeList meta;
	private XmlTag<?> rootElement;
	
	public XmlFile() {
		meta = new XmlAttributeList();
	}

	public static XmlFile read(StringReader reader) {
		XmlFile file = new XmlFile();
		
		//Meta
		reader.skipWhitespace();
		reader.expectAndSkip("<?xml");
		
		file.meta = XmlAttributeList.read(reader);
		reader.expectAndSkip("?>");
		
		//Content
		file.rootElement = XmlTag.read(reader);
		
		return file;
	}
	
	public StringWriter write(StringWriter writer) {
		writer.write("<?xml");
		meta.write(writer);
		writer.writeln("?>");
		rootElement.write(writer);
		return writer;
	}
	
	public String toString() {
		return write(new StringWriter()).toString();
	}
	
	public XmlTag<?> getRootElement() {
		return rootElement;
	}
	
}
