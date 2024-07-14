package b100.installer;

public class Version {
	
	public String name;

	public Version(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
