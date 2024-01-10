package b100.json.element;

public class JsonEntry{
	
	public final String name;
	public JsonElement value;
	
	public JsonEntry(String name, JsonElement value) {
		this.name = name;
		this.value = value;
	}
	
	public boolean equalsId(String string) {
		return string.equals(name);
	}
	
}
