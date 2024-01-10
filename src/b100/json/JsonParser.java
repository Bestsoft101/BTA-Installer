package b100.json;

import java.io.File;
import java.io.InputStream;

import b100.json.element.JsonObject;
import b100.utils.ObjectParser;
import b100.utils.Parser;
import b100.utils.ParserCollection;
import b100.utils.StringParser;
import b100.utils.StringReader;
import b100.utils.StringUtils;

public class JsonParser implements StringParser<JsonObject>{
	
	public static final JsonParser instance = new JsonParser();
	
	private final ParserCollection<JsonObject> parsers = new ParserCollection<>();
	
	private final Parser<JsonObject, String> stringParser = (string) -> new JsonObject(new StringReader(string));
	private final Parser<JsonObject, File> fileParser = (file) -> stringParser.parse(StringUtils.getFileContentAsString(file));
	private final Parser<JsonObject, InputStream> streamParser = (stream) -> stringParser.parse(StringUtils.readInputString(stream));
	
	public JsonParser() {
		parsers.add(new ObjectParser<>(String.class, stringParser));
		parsers.add(new ObjectParser<>(File.class, fileParser));
		parsers.add(new ObjectParser<>(InputStream.class, streamParser));
	}
	
	public JsonObject parse(Object object) {
		return parsers.parse(object);
	}
	
	public ParserCollection<JsonObject> getParsers() {
		return parsers;
	}

	public JsonObject parseString(String string) {
		return stringParser.parse(string);
	}

}
