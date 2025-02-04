package b100.installer.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import b100.installer.config.ConfigUtil.ConfigParser;

public class ConfigFile implements ConfigParser {
	
	public File file;
	
	private final List<Property> allProperties = new ArrayList<>();
	private final Map<String, Property> propertyMap = new HashMap<>();
	
	private final List<Property> allPropertiesImmutable = Collections.unmodifiableList(allProperties);
	
	public ConfigFile(File file) {
		this.file = file;
	}
	
	@Override
	public void parse(String key, String value) {
		Property property = propertyMap.get(key);
		if(property == null) {
			System.out.println("Unknown Config Property: " + key);
			return;
		}
		property.parseConfigString(value);
	}
	
	public <E extends Property> E register(String name, E property) {
		allProperties.add(property);
		propertyMap.put(name, property);
		return property;
	}
	
	public void load() {
		if(file.exists()) {
			ConfigUtil.loadConfig(file, this, ':');	
		}
	}
	
	public void save() {
		StringBuilder str = new StringBuilder();
		Map<Property, String> propertyKeys = getPropertyKeyMap();

		int written = 0;
		for(int i=0; i < allProperties.size(); i++) {
			Property property = allProperties.get(i);
			
			String key = propertyKeys.get(property);
			String value = property.getConfigString();
			
			if(key != null && value != null) {
				if(written > 0) {
					str.append('\n');
				}
				str.append(key);
				str.append(':');
				str.append(value);
				written++;
			}
		}
		
		ConfigUtil.saveStringToFile(str.toString(), file);
	}
	
	public List<Property> getAllProperties() {
		return allPropertiesImmutable;
	}
	
	public Property getProperty(String name) {
		return propertyMap.get(name);
	}
	
	public List<String> getAllPropertyKeys() {
		Map<Property, String> propertyKeys = getPropertyKeyMap();
		List<String> allKeys = new ArrayList<>();
		for(int i=0; i < allProperties.size(); i++) {
			allKeys.add(propertyKeys.get(allProperties.get(i)));
		}
		return Collections.unmodifiableList(allKeys);
	}
	
	public Map<Property, String> getPropertyKeyMap() {
		Map<Property, String> propertyKeys = new HashMap<>();
		for(String key : propertyMap.keySet()) {
			Property property = propertyMap.get(key);
			propertyKeys.put(property, key);
		}
		return propertyKeys;
	}
}
