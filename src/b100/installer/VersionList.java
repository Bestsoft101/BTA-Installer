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
	
	public static boolean useInternalVersionList = false;
	
	public static final int VERSION = 1;
	public static final String URL = "https://raw.githubusercontent.com/Bestsoft101/BTA-Installer/main/src/versions.json";
	public static final long QUERYTIME = 24L * 60L * 60L * 1000L;
	
	private static File versionListFile = new File(Installer.getInstallerDirectory(), "versions.json");
	
	private static boolean loadedVersions = false;
	
	private static JsonObject versions;
	
	public static JsonObject getVersions() {
		if(!loadedVersions) {
			versions = readVersions();
			loadedVersions = true;
		}
		return versions;
	}
	
	public static JsonObject readVersions() {
		if(!useInternalVersionList) {
			long timeSinceLastQuery = System.currentTimeMillis() - Config.getInstance().lastVersionQueryTime;
			if(timeSinceLastQuery > QUERYTIME || !versionListFile.exists()) {
				refreshVersionList();
			}
			
			JsonObject versions = null;
			if(versionListFile.exists()) {
				versions = JsonParser.instance.parseFileContent(versionListFile);
				if(versions.getInt("version") > VERSION) {
					JOptionPane.showMessageDialog(InstallerGUI.instance.mainFrame, "Installer is outdated, version list may be incomplete!");
					versions = null;
				}
			}
		}else {
			System.out.println("Using internal version list!");
		}
		
		if(versions == null) {
			versions = JsonParser.instance.parseStream(VanillaLauncherInstallerGUI.class.getResourceAsStream("/versions.json"));
			if(versions == null) {
				throw new NullPointerException("Could not read internal version list!");
			}
		}
		
		return versions;
	}
	
	public static void refreshVersionList() {
		Config.getInstance().lastVersionQueryTime = System.currentTimeMillis();
		Config.getInstance().save();
		
		System.out.println("Refreshing version list");
		
		if(versionListFile.exists()) {
			if(!versionListFile.delete()) {
				throw new RuntimeException("Could not delete old version list!");
			}
		}
		
		try {
			DownloadManager.downloadFileAndPrintProgress(URL, versionListFile);
			
			loadedVersions = false;
		}catch (Exception e) {
			JOptionPane.showMessageDialog(InstallerGUI.instance.mainFrame, "Could not get the newest version list!");
			e.printStackTrace();
		}
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
