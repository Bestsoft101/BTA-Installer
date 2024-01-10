package b100.xml;

import java.io.File;
import java.io.InputStream;

import b100.utils.ObjectParser;
import b100.utils.Parser;
import b100.utils.ParserCollection;
import b100.utils.StringParser;
import b100.utils.StringReader;
import b100.utils.StringUtils;

public class XmlParser implements StringParser<XmlFile>{
	
	public static final XmlParser instance = new XmlParser();
	
	private final ParserCollection<XmlFile> parsers = new ParserCollection<>();	

	private final Parser<XmlFile, String> stringParser = (string) -> XmlFile.read(new StringReader(string));
	private final Parser<XmlFile, File> fileParser = (file) -> stringParser.parse(StringUtils.getFileContentAsString(file));
	private final Parser<XmlFile, InputStream> streamParser = (stream) -> stringParser.parse(StringUtils.readInputString(stream));
	
	public XmlParser() {
		parsers.add(new ObjectParser<>(String.class, stringParser));
		parsers.add(new ObjectParser<>(File.class, fileParser));
		parsers.add(new ObjectParser<>(InputStream.class, streamParser));
	}
	
	public XmlFile parse(Object object) {
		return parsers.parse(object);
	}
	
	public ParserCollection<XmlFile> getParsers() {
		return parsers;
	}

	public XmlFile parseString(String string) {
		return stringParser.parse(string);
	}

}
