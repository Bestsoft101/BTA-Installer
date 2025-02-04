package b100.installer.config;

public class DoubleProperty implements Property {
	
	public final double defaultValue;
	
	public double value;
	
	public DoubleProperty(double defaultValue) {
		this.defaultValue = defaultValue;
		this.value = defaultValue;
	}
	
	@Override
	public void parseConfigString(String str) {
		value = Double.parseDouble(str);
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
