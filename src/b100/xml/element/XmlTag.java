package b100.xml.element;

import b100.utils.InvalidCharacterException;
import b100.utils.StringReader;
import b100.utils.StringWriter;

public abstract class XmlTag<E> {
	
	protected String name;
	protected XmlAttributeList attributes;
	protected E content;
	
	public XmlTag(String id, E content) {
		if(id == null)
			throw new NullPointerException();
		if(id.length() == 0)
			throw new RuntimeException("Empty String");
		
		this.name = id;
		this.content = content;
	}

	public abstract StringWriter write(StringWriter writer);
	
	public E content() {
		return content;
	}
	
	public void setContent(E content) {
		this.content = content;
	}
	
	public XmlAttributeList getAttributes() {
		return attributes;
	}
	
	public void setAttributes(XmlAttributeList attributes) {
		this.attributes = attributes;
	}
	
	public String name() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public static XmlTag<?> read(StringReader reader) {
		reader.skipWhitespace();
		reader.expectAndSkip("<");
		
		String id = readId(reader);
		XmlAttributeList attributes = XmlAttributeList.read(reader);
		
		reader.expectAndSkip(">");
//		System.out.println("Tag: "+id+", Attributes: "+attributes.size());
		
		XmlTag<?> tag;
		String closeTag = "</"+id+">";
		
		reader.skipWhitespace();
		
		if(reader.isNext(closeTag)){ //Tag is empty
			reader.expectAndSkip(closeTag);
			tag = new XmlStringTag(id, null);
		}else if(reader.get() == '<') { //Tag is content tag
			tag = readContentTag(id, reader);
		}else { //Tag is string tag
			tag = readStringTag(id, reader);
		}
		
		
		
		tag.setAttributes(attributes);
		return tag;
	}
	
	public static XmlContentTag readContentTag(String id, StringReader reader) {
		XmlContentTag tag = new XmlContentTag(id);
		String closeTag = "</"+id+">";
		
		while(true) {
			tag.add(read(reader));
			reader.skipWhitespace();
			
			if(reader.isNext("</")) {
				reader.expectAndSkip(closeTag);
				break;
			}
		}
		
		return tag;
	}
	
	public static XmlStringTag readStringTag(String id, StringReader reader) {
		String closeTag = "</"+id+">";
		String value = "";
		
		while(true) {
			if(reader.get() == '<') {
				reader.expectAndSkip(closeTag);
				break;
			}else {
				value += reader.getAndSkip();
			}
		}
		
		return new XmlStringTag(id, value);
	}
	
	public static String readId(StringReader reader) {
		String id = "";
		
		while(true) {
			if(reader.get() == '/') {
				throw new InvalidCharacterException(reader);
			}else if(reader.get() == ' ' || reader.get() == '>') {
				return id;
			}else {
				id += reader.getAndSkip();
			}
		}
	}
	
	public String toString() {
		return write(new StringWriter()).toString();
	}
	
	public XmlContentTag getAsContentTag() {
		return (XmlContentTag) this;
	}
	
	public XmlStringTag getAsStringTag() {
		return (XmlStringTag) this;
	}
	
}
