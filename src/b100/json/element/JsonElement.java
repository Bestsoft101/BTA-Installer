package b100.json.element;

import b100.utils.InvalidCharacterException;
import b100.utils.StringReader;
import b100.utils.Writable;

public interface JsonElement extends Writable{
	
	public static JsonElement readElement(StringReader reader) {
		reader.skipWhitespace();
		
		if(reader.get() == '"') return new JsonString(reader);
		if(reader.get() == '{') return new JsonObject(reader);
		if(reader.get() == '[') return new JsonArray(reader);
		if((reader.get() >= '0' && reader.get() <= '9') || reader.get() == '-') return new JsonNumber(reader);
		if(reader.isNext("true") || reader.isNext("false")) return new JsonBoolean(reader);
		
		throw new InvalidCharacterException(reader);
	}
	
	public default <E extends JsonElement> E getAs(Class<E> clazz) {
		return clazz.cast(this);
	}
	
	public default JsonObject getAsObject() {
		return getAs(JsonObject.class);
	}
	
	public default JsonArray getAsArray() {
		return getAs(JsonArray.class);
	}
	
	public default JsonNumber getAsNumber() {
		return getAs(JsonNumber.class);
	}
	
	public default JsonString getAsString() {
		return getAs(JsonString.class);
	}
	
	public default JsonBoolean getAsBoolean() {
		return getAs(JsonBoolean.class);
	}
	
	public default boolean is(Class<? extends JsonElement> clazz) {
		return this.getClass().isAssignableFrom(clazz);
	}
	
	public default boolean isObject() {
		return is(JsonObject.class);
	}
	
	public default boolean isArray() {
		return is(JsonArray.class);
	}
	
	public default boolean isNumber() {
		return is(JsonNumber.class);
	}
	
	public default boolean isString() {
		return is(JsonString.class);
	}
	
	public default boolean isBoolean() {
		return is(JsonBoolean.class);
	}
	
}
