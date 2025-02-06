package b100.installer.installer;

import java.util.Map;

import b100.installer.gui.classic.VersionListGUI.VersionFilter;

public interface Installer extends VersionFilter {
	
	public boolean install(Map<String, Object> parameters);
	
}
