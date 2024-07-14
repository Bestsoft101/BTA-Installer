package b100.installer;

public enum ModLoader {
	None, Babric, Fabric, ASMLoader;
	
	public String getDisplayName() {
		if(this == None) {
			return "No Mod Loader";
		}
		return name();
	}
	
	@Override
	public String toString() {
		return getDisplayName();
	}
}