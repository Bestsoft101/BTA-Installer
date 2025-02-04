package b100.installer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import b100.installer.gui.InstallerGUI;
import b100.installer.gui.VanillaLauncherInstallerGUI;
import b100.json.JsonParser;
import b100.json.element.JsonObject;

public class VersionList {
	
	public static final int VERSION = 3;
	public static final String URL = "https://raw.githubusercontent.com/Bestsoft101/BTA-Installer/main/src/versions.json";
	public static final long QUERYTIME = 24L * 60L * 60L * 1000L;
	
	private static File versionListFile = new File(Installer.getInstallerDirectory(), "versions.json");
	
	private static JsonObject versions;
	
	public static boolean validateVersion() {
		return getJson().getInt("version") == VERSION;
	}
	
	public static JsonObject getJson() {
		if(versions == null) {
			versions = readVersions();
		}
		return versions;
	}
	
	public static JsonObject readVersions() {
		if(!Installer.isOffline()) {
			long timeSinceLastQuery = System.currentTimeMillis() - Config.getInstance().lastVersionQueryTime.value;
			if(timeSinceLastQuery > QUERYTIME || !versionListFile.exists()) {
				refreshVersionList();
			}
			
			if(versionListFile.exists()) {
				versions = JsonParser.instance.parseFileContent(versionListFile);
				int version = versions.getInt("version");
				if(version > VERSION) {
					JOptionPane.showMessageDialog(null, "Installer is outdated, version list may be incomplete!");
					System.out.println("Installer is outdated!");
					versions = null;
				}else if(version < VERSION) {
					JOptionPane.showMessageDialog(null, "Version list on the repository is outdated, using internal version list!");
					System.out.println("Version list is outdated!");
					versions = null;
				}else {
					System.out.println("Using downloaded version list");
				}
			}
		}else {
			System.out.println("Offline mode enabled!");
		}
		
		if(versions == null) {
			System.out.println("Loading internal version list");
			versions = JsonParser.instance.parseStream(VanillaLauncherInstallerGUI.class.getResourceAsStream("/versions.json"));
			if(versions == null) {
				throw new NullPointerException("Could not read internal version list!");
			}
		}
		
		return versions;
	}
	
	public static void refreshVersionList() {
		Config.getInstance().lastVersionQueryTime.value = System.currentTimeMillis();
		Config.getInstance().save();
		
		System.out.println("Refreshing version list");
		
		if(versionListFile.exists()) {
			if(!versionListFile.delete()) {
				throw new RuntimeException("Could not delete old version list!");
			}
		}
		
		try {
			DownloadManager.downloadFileAndPrintProgress(URL, versionListFile);
			versions = null;
		}catch (Exception e) {
			JOptionPane.showMessageDialog(InstallerGUI.instance.mainFrame, "Could not get the newest version list!");
			e.printStackTrace();
		}
	}
	
	public static List<String> getAllVersions() {
		List<String> versionList = new ArrayList<>();
		
		JsonObject versions = getJson().getObject("versions");
		for(int i=0; i < versions.entryList().size(); i++) {
			versionList.add(versions.entryList().get(i).name);
		}
		
		return versionList;
	}
	
	public static JsonObject getVersion(String name) {
		return getJson().getObject("versions").getObject(name);
	}

}
