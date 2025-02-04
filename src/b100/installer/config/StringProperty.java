package b100.installer.config;

import java.util.Objects;

// TODO Handle line breaks properly
public class StringProperty implements Property {
	
	public final String defaultValue;
	
	public String value;

	public StringProperty() {
		this(null);
	}
	
	public StringProperty(String defaultValue) {
		this.defaultValue = defaultValue;
		this.value = defaultValue;
	}
	
	@Override
	public void parseConfigString(String value) {
		this.value = value;
	}

	@Override
	public String getConfigString() {
		return value;
	}

	@Override
	public void reset() {
		value = defaultValue;
	}

	@Override
	public boolean isDefault() {
		return Objects.equals(value, defaultValue);
	}
	
}
