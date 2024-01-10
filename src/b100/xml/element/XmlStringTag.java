package b100.xml.element;

import b100.utils.StringWriter;

public class XmlStringTag extends XmlTag<String>{

	public XmlStringTag(String id, String value) {
		super(id, value);
	}

	public StringWriter write(StringWriter writer) {
		writer.write("<"+name);
		attributes.write(writer);
		writer.write(">"+(content!=null?content:"")+"</"+name+">");
		return writer;
	}

	public long getLong() {
		return Long.parseLong(content);
	}

	public int getInt() {
		return Integer.parseInt(content);
	}

}
