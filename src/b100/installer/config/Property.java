package b100.installer.config;

public interface Property {
	
	public void parseConfigString(String value);
	
	public String getConfigString();
	
	public void reset();
	
	public boolean isDefault();
	
}
