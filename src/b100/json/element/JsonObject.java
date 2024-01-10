package b100.json.element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import b100.utils.InvalidCharacterException;
import b100.utils.StringReader;
import b100.utils.StringWriter;

public class JsonObject implements JsonElement, Iterable<JsonEntry>{
	
	private List<JsonEntry> entries;
	
	private boolean compact = false;
	
	public JsonObject() {
		entries = new ArrayList<>();
	}
	
	public JsonObject(StringReader reader) {
		this();
		
		reader.skipWhitespace();
		reader.expectAndSkip('{');
		
		while(true) {
			reader.skipWhitespace();
			if(reader.get() == '"') {
				//Read ID
				String id = new JsonString(reader).value;
				
				reader.skipWhitespace();
				reader.expectAndSkip(':');
				
				//Read element
				JsonElement element = JsonElement.readElement(reader);
				
				//Insert element
				set(id, element);
				
				//Read next element in this object or leave
				reader.skipWhitespace();
				if(reader.get() == ',') {
					reader.next();
					continue;
				}else if(reader.get() == '}') {
					reader.next();
					break;
				}else {
					throw new InvalidCharacterException(reader);
				}
			}else if(reader.get() == '}') {
				reader.next();
				break;
			}else {
				throw new InvalidCharacterException(reader);
			}
		}
	}

	public void write(StringWriter writer) {
		writer.write("{");
		writer.addTab();
		
		int i=0;
		for(JsonEntry entry : entries) {
			if(!isCompact()) writer.write('\n');
			else writer.write(' ');
			new JsonString(entry.name).write(writer);
			writer.write(": ");
			entry.value.write(writer);
			if(i < entries.size() - 1) writer.write(',');
			i++;
		}
		
		if(i > 0) {
			if(!isCompact()) {
				writer.write('\n');
			}else{
				writer.write(' ');
			}
			
		}
		writer.removeTab();
		writer.write("}");
	}
	
	public String toString() {
		StringWriter writer = new StringWriter();
		write(writer);
		return writer.toString();
	}
	
	// Getter
	
	public JsonObject getOrCreateObject(String id) {
		JsonObject object = getObject(id);
		if(object == null) {
			object = new JsonObject();
			set(id, object);
		}
		return object;
	}
	
	public JsonEntry getOrCreateEntry(String string) {
		JsonEntry entry = getEntry(string);
		if(entry == null) {
			entry = new JsonEntry(string, null);
			entries.add(entry);
		}
		return entry;
	}
	
	public JsonEntry getEntry(String string) {
		for(JsonEntry e : entries) {
			if(e.equalsId(string)) {
				return e;
			}
		}
		return null;
	}
	
	// JSON Getters
	
	public JsonElement get(String id) {
		JsonEntry entry = getEntry(id);
		return entry != null ? entry.value : null;
	}

	public JsonObject getObject(String id) {
		JsonElement element = get(id);
		return element != null ? element.getAsObject() : null;
	}

	public JsonArray getArray(String id) {
		JsonElement element = get(id);
		return element != null ? element.getAsArray() : null;
	}
	
	public JsonString getJsonString(String id) {
		return get(id).getAsString();
	}
	
	public JsonNumber getJsonNumber(String id) {
		return get(id).getAsNumber();
	}
	
	public JsonBoolean getJsonBoolean(String id) {
		return get(id).getAsBoolean();
	}

	// Getters
	
	public String getString(String id) {
		return get(id).getAsString().value;
	}
	
	public Number getNumber(String id) {
		return get(id).getAsNumber().value;
	}
	
	public int getInt(String id) {
		return getNumber(id).intValue();
	}

	public long getLong(String id) {
		return getNumber(id).longValue();
	}

	public double getDouble(String id) {
		return getNumber(id).doubleValue();
	}

	public float getFloat(String id) {
		return getNumber(id).floatValue();
	}

	public byte getByte(String id) {
		return getNumber(id).byteValue();
	}

	public short getShort(String id) {
		return getNumber(id).shortValue();
	}
	
	public boolean getBoolean(String id) {
		return get(id).getAsBoolean().value;
	}
	
	// Getters with default Values
	
	public int getInt(String id, int defaultValue) {
		JsonEntry entry = getOrCreateEntry(id);
		if(!(entry.value != null && entry.value instanceof JsonNumber)) entry.value = new JsonNumber(defaultValue);
		return entry.value.getAsNumber().getInteger();
	}
	
	public long getLong(String id, long defaultValue) {
		JsonEntry entry = getOrCreateEntry(id);
		if(!(entry.value != null && entry.value instanceof JsonNumber)) entry.value = new JsonNumber(defaultValue);
		return entry.value.getAsNumber().getLong();
	}
	
	public float getFloat(String id, float defaultValue) {
		JsonEntry entry = getOrCreateEntry(id);
		if(!(entry.value != null && entry.value instanceof JsonNumber)) entry.value = new JsonNumber(defaultValue);
		return entry.value.getAsNumber().getFloat();
	}
	
	public double getDouble(String id, double defaultValue) {
		JsonEntry entry = getOrCreateEntry(id);
		if(!(entry.value != null && entry.value instanceof JsonNumber)) entry.value = new JsonNumber(defaultValue);
		return entry.value.getAsNumber().getDouble();
	}
	
	public short getShort(String id, short defaultValue) {
		JsonEntry entry = getOrCreateEntry(id);
		if(!(entry.value != null && entry.value instanceof JsonNumber)) entry.value = new JsonNumber(defaultValue);
		return entry.value.getAsNumber().getShort();
	}
	
	public byte getByte(String id, byte defaultValue) {
		JsonEntry entry = getOrCreateEntry(id);
		if(!(entry.value != null && entry.value instanceof JsonNumber)) entry.value = new JsonNumber(defaultValue);
		return entry.value.getAsNumber().getByte();
	}
	
	public boolean getBoolean(String id, boolean defaultValue) {
		JsonEntry entry = getOrCreateEntry(id);
		if(!(entry.value != null && entry.value instanceof JsonBoolean)) entry.value = new JsonBoolean(defaultValue);
		return entry.value.getAsBoolean().value;
	}
	
	// Setters
	
	public JsonObject set(String id, JsonElement element) {
		getOrCreateEntry(id).value = element;
		return this;
	}
	
	public JsonObject set(String id, String s) {
		return set(id, new JsonString(s));
	}
	
	public JsonObject set(String id, int n) {
		return set(id, new JsonNumber(n));
	}
	
	public JsonObject set(String id, long n) {
		return set(id, new JsonNumber(n));
	}
	
	public JsonObject set(String id, float n) {
		return set(id, new JsonNumber(n));
	}
	
	public JsonObject set(String id, double n) {
		return set(id, new JsonNumber(n));
	}
	
	public JsonObject set(String id, short n) {
		return set(id, new JsonNumber(n));
	}
	
	public JsonObject set(String id, byte n) {
		return set(id, new JsonNumber(n));
	}
	
	public JsonObject set(String id, boolean b) {
		return set(id, new JsonBoolean(b));
	}
	
	// Lists
	
	public List<JsonElement> elementList() {
		List<JsonElement> elements = new ArrayList<>();
		for(JsonEntry entry : entries) {
			elements.add(entry.value);
		}
		return elements;
	}
	
	public List<String> idList() {
		List<String> elements = new ArrayList<>();
		for(JsonEntry entry : entries) {
			elements.add(entry.name);
		}
		return elements;
	}
	
	public List<JsonEntry> entryList() {
		return entries;
	}

	public boolean has(String id) {
		return getEntry(id) != null;
	}

	public boolean has(String name, JsonElement element) {
		JsonEntry entry = getEntry(name);
		if(entry != null) {
			return entry.value.equals(element);
		}else {
			return false;
		}
	}

	public boolean has(String name, String string) {
		JsonEntry entry = getEntry(name);
		if(entry != null) {
			if(entry.value.isString()) {
				return entry.value.getAsString().value.equals(string);
			}
		}
		return false;
	}
	
	// Misc.

	public Iterator<JsonEntry> iterator() {
		return entries.iterator();
	}

	public JsonObject setCompact(boolean b) {
		compact = b;
		return this;
	}
	
	public boolean isCompact() {
		return compact;
	}
	
}
