package b100.json;

import static b100.utils.FileUtils.*;
import static b100.utils.StringUtils.*;
import static b100.utils.Utils.*;

import java.io.File;

import b100.json.element.JsonObject;

public class JsonFile {
	
	private File file;
	private JsonObject rootObject;
	private JsonParser jsonParser = JsonParser.instance;
	
	public JsonFile(File file) {
		this.file = requireNonNull(file);
	}
	
	public JsonFile(String path) {
		this(new File(requireNonNull(path)));
	}
	
	public JsonObject getRootObject() {
		if(rootObject == null) {
			if(fileExists()) {
				load();
			}
			if(rootObject == null) {
				rootObject = new JsonObject();
			}
		}
		return rootObject;
	}
	
	public void load() {
		try{
			rootObject = jsonParser.parse(file);
		}catch (Exception e) {
			throw new RuntimeException("Error loading file "+file.getAbsolutePath(), e);
		}
	}
	
	public JsonFile setRootObject(JsonObject object) {
		this.rootObject = object;
		return this;
	}
	
	public void save() {
		saveStringToFile(createNewFile(file), getRootObject().toString());
	}
	
	public boolean fileExists() {
		return file.exists() && file.isFile();
	}
	
	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		this.file = requireNonNull(file);
	}
	
	public void setJsonParser(JsonParser jsonParser) {
		this.jsonParser = requireNonNull(jsonParser);
	}
	
	public JsonParser getJsonParser() {
		return jsonParser;
	}
	
}
