package b100.installer.config;

public class IntProperty implements Property {
	
	public final int defaultValue;
	
	public int value;
	
	public IntProperty() {
		this(0);
	}
	
	public IntProperty(int defaultValue) {
		this.defaultValue = defaultValue;
		this.value = defaultValue;
	}
	
	@Override
	public void parseConfigString(String str) {
		value = Integer.parseInt(str);
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
