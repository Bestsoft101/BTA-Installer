package b100.installer.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import b100.installer.Config;
import b100.installer.DownloadManager;
import b100.installer.Utils;
import b100.installer.VersionList;
import b100.installer.gui.utils.GridPanel;
import b100.json.JsonParser;
import b100.json.element.JsonObject;
import b100.utils.StringUtils;

@SuppressWarnings("serial")
public class VanillaLauncherInstallerGUI extends GridPanel implements ActionListener, Runnable {
	
	public static final String INSTALL_TYPE = "vanilla";
	
	public final InstallerGUI installerGUI;
	
	public JComboBox<String> versionMenu;
	public JButton startButton;
	
	public VanillaLauncherInstallerGUI(InstallerGUI installerGUI) {
		this.installerGUI = installerGUI;
		
		int inset = 4;
		getGridBagConstraints().insets.set(inset, inset, inset, inset);
		
		String[] allVersions = Utils.toArray(VersionList.getAllVersions());
		versionMenu = new JComboBox<>(allVersions);
		versionMenu.setPreferredSize(new Dimension(256, 24));
		int selectedVersionIndex = Utils.indexOf(allVersions, Config.getInstance().lastSelectedVersion);
		if(selectedVersionIndex > 0) {
			versionMenu.setSelectedIndex(selectedVersionIndex);
		}
		
		startButton = new JButton("Install");
		startButton.addActionListener(this);
		
		add(Utils.createImagePanel("/logo.png"), 0, 0, 1, 1, 2, 1);
		add(versionMenu, 0, 1, 1, 0);
		add(startButton, 1, 1, 0, 0);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		new Thread(this).start();
	}

	@Override
	public void run() {
		startButton.setEnabled(false);
		installerGUI.showLog();
		
		try {
			install();
		}catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Failure!");
			
			e.printStackTrace();
		}
		
		startButton.setEnabled(true);
	}
	
	public boolean install() {
		String selectedVersion = (String) versionMenu.getSelectedItem();
		System.out.println("Selected Version: " + selectedVersion);
		
		Config config = Config.getInstance();
		config.lastSelectedVersion = selectedVersion;
		config.lastInstallType = INSTALL_TYPE;
		config.save();
		
		JsonObject versionObject = VersionList.getVersion(selectedVersion);
		
		JsonObject vanillaObject = versionObject.getObject("vanilla");
		if(vanillaObject == null) {
			throw new NullPointerException("No vanilla object!");
		}
		
		String versionName = "BTA " + selectedVersion;
		String profileName = "Better Than Adventure!";

		File minecraftDirectory = Utils.getMinecraftDirectory();
		System.out.println("Minecraft Directory: " + minecraftDirectory.getAbsolutePath());

		File versionFolder = new File(minecraftDirectory, "versions/" + versionName);
		if(!versionFolder.exists()) {
			versionFolder.mkdirs();
		}
		
		// Combine minecraft and mod jar and put into instance folder
		File outputJar = new File(versionFolder, versionName + ".jar");
		if(!outputJar.exists()) {
			System.out.println("Version is not installed");
			File minecraftJar = new File(minecraftDirectory, "versions/b1.7.3/b1.7.3.jar");
			if(!minecraftJar.exists()) {
				JOptionPane.showMessageDialog(this, "Please start Beta 1.7.3 once before installing!");
				return false;
			}

			File modJarFile = DownloadManager.getFile(versionObject.getString("jar"));
			Utils.createModdedMinecraftJar(minecraftJar, modJarFile, outputJar);
		}else {
			System.out.println("Version is installed");
		}
		
		// Copy json file into instance folder and set id
		JsonObject json = JsonParser.instance.parseStream(VanillaLauncherInstallerGUI.class.getResourceAsStream("/" + vanillaObject.getString("json")));
		json.set("id", versionName);
		StringUtils.saveStringToFile(new File(versionFolder, versionName + ".json"),  json.toString());
		
		// Setup Launcher Profile
		updateLauncherProfile(minecraftDirectory, profileName, versionName);
		
		JOptionPane.showMessageDialog(this, "Done!");
		return true;
	}
	
	public void updateLauncherProfile(File minecraftDirectory, String profileName, String versionId) {
		File launcherProfilesFile = new File(minecraftDirectory, "launcher_profiles.json");
		
		JsonObject root = JsonParser.instance.parseFileContent(launcherProfilesFile);
		JsonObject profiles = root.getObject("profiles");
		JsonObject profile = null;
		
		// Find existing profile with same name
		for(int i=0; i < profiles.entryList().size(); i++) {
			JsonObject profile1 = profiles.entryList().get(i).value.getAsObject();
			String profileName1 = profile1.getString("name");
			if(profileName1.equals(profileName)) {
				System.out.println("Installing into launcher profile '" + profileName1 + "'!");
				profile = profile1;
				break;
			}
		}
		
		// Create new profile if it does not exist
		if(profile == null) {
			System.out.println("Creating new launcher profile: '" + profileName + "'!");
			String randomUUID = UUID.randomUUID().toString().toLowerCase().replace("-", "");
			System.out.println("UUID: " + randomUUID);
			profile = new JsonObject();
			profiles.set(randomUUID, profile);
		}

		profile.set("lastVersionId", versionId);
		profile.set("name", profileName);
		profile.set("icon", "Grass"); // TODO
		
		// I assume it doesn't work if these arent't here
		if(!profile.has("created")) {
			profile.set("created", "1970-01-01T00:00:00.000Z"); // I don't care
		}
		if(!profile.has("lastUsed")) {
			profile.set("lastUsed", "2070-01-01T00:00:00.000Z"); // All the way up
		}
		profile.set("type", "custom");
		
		// TODO java args?
		
		StringUtils.saveStringToFile(launcherProfilesFile, root.toString());
	}

}
