package b100.installer.config;

public class LongProperty implements Property {
	
	public final long defaultValue;
	
	public long value;

	public LongProperty() {
		this(0L);
	}
	
	public LongProperty(long defaultValue) {
		this.defaultValue = defaultValue;
		this.value = defaultValue;
	}
	
	@Override
	public void parseConfigString(String str) {
		value = Long.parseLong(str);
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
