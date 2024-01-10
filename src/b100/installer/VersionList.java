package b100.installer;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import b100.installer.gui.VanillaLauncherInstallerGUI;
import b100.json.JsonParser;
import b100.json.element.JsonObject;

public class VersionList {
	
	private static JsonObject versions = readVersions();
	
	private static JsonObject readVersions() {
		try {
			return JsonParser.instance.parseStream(VanillaLauncherInstallerGUI.class.getResourceAsStream("/versions.json"));
		}catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Could not load versions.json!");
			e.printStackTrace();
			System.exit(0);
			throw new RuntimeException();
		}
	}
	
	public static JsonObject getVersions() {
		return versions;
	}
	
	public static List<String> getAllVersions() {
		List<String> versionList = new ArrayList<>();
		
		JsonObject versions = getVersions().getObject("versions");
		for(int i=0; i < versions.entryList().size(); i++) {
			versionList.add(versions.entryList().get(i).name);
		}
		
		return versionList;
	}
	
	public static JsonObject getVersion(String name) {
		return getVersions().getObject("versions").getObject(name);
	}

}
