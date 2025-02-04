package b100.installer.config;

public class BooleanProperty implements Property {

	public final boolean defaultValue;
	
	public boolean value;
	
	public BooleanProperty(boolean defaultValue) {
		this.defaultValue = defaultValue;
		this.value = defaultValue;
	}
	
	@Override
	public void parseConfigString(String str) {
		value = str.equalsIgnoreCase("true");
	}

	@Override
	public String getConfigString() {
		return String.valueOf(value);
	}

	@Override
	public void reset() {
		value = defaultValue;
	}

	@Override
	public boolean isDefault() {
		return value == defaultValue;
	}
	
}
