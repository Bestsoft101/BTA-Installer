package b100.installer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import b100.installer.Config;
import b100.installer.DownloadManager;
import b100.installer.ModLoader;
import b100.installer.Utils;
import b100.installer.VersionList;
import b100.installer.gui.VersionListGUI.VersionFilter;
import b100.installer.gui.utils.GridPanel;
import b100.installer.gui.utils.GuiUtils;
import b100.installer.gui.utils.VersionComponent;
import b100.json.JsonParser;
import b100.json.element.JsonElement;
import b100.json.element.JsonObject;
import b100.utils.StringUtils;

@SuppressWarnings("serial")
public class VanillaLauncherInstallerGUI extends GridPanel implements ActionListener, Runnable, VersionFilter {
	
	public static final String INSTALL_TYPE = "vanilla";
	
	public final InstallerGUI installerGUI;
	
	public VersionComponent versionComponent;
	public JButton installButton;
	public JTextField minecraftDirectoryTextfield;
	
	public VanillaLauncherInstallerGUI(InstallerGUI installerGUI) {
		this.installerGUI = installerGUI;
		
		int inset = 4;
		getGridBagConstraints().insets.set(inset, inset, inset, inset);
		
		minecraftDirectoryTextfield = new JTextField();
		minecraftDirectoryTextfield.setText(getMinecraftDirectory());

		List<ModLoader> modLoaders = new ArrayList<>();
		modLoaders.add(ModLoader.None);
		modLoaders.add(ModLoader.ASMLoader);
		modLoaders.add(ModLoader.Babric);
		versionComponent = new VersionComponent(modLoaders, this);
		
		installButton = new JButton("Install");
		installButton.addActionListener(this);
		
		add(GuiUtils.createImagePanel("/logo.png"), 0, 0, 1, 1);
		add(GuiUtils.createTitledPanel(minecraftDirectoryTextfield, "Minecraft Directory"), 0, 1, 1, 0);
		add(versionComponent, 0, 2, 1, 0);
		add(installButton, 0, 3, 1, 0);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == installButton) {
			new Thread(this).start();
		}
	}
	
	@Override
	public void run() {
		installButton.setEnabled(false);
		installerGUI.showLog();
		
		try {
			install();
		}catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Failure!");
			
			e.printStackTrace();
		}
		
		installButton.setEnabled(true);
	}
	
	public boolean install() {
		String selectedVersion = versionComponent.getSelectedVersion();
		ModLoader loader = versionComponent.getSelectedLoader();
		File minecraftDirectory = new File(minecraftDirectoryTextfield.getText());
		
		System.out.println("Selected Version: " + selectedVersion);
		System.out.println("Selected Mod Loader: " + loader);
		System.out.println("Minecraft Directory: " + minecraftDirectory.getAbsolutePath());
		
		// Update config
		Config config = Config.getInstance();
		config.lastSelectedVersion = selectedVersion;
		config.lastInstallType = INSTALL_TYPE;
		config.lastMinecraftDirectory = minecraftDirectory.getAbsolutePath();
		config.save();

		// Validate path
		if(!minecraftDirectory.exists() || !minecraftDirectory.isDirectory()) {
			JOptionPane.showMessageDialog(this, "Invalid Minecraft Directory: '" + minecraftDirectory.getAbsolutePath() + "'!");
			config.lastMinecraftDirectory = null;
			config.save();
			return false;
		}
		
		JsonObject versionObject = VersionList.getVersion(selectedVersion);
		JsonObject vanillaObject = versionObject.getObject("vanilla");
		if(vanillaObject == null) {
			throw new NullPointerException("No vanilla object!");
		}
		
		String versionName = "BTA " + selectedVersion;
		String profileName = "Better Than Adventure!";

		if(loader == ModLoader.Fabric || loader == ModLoader.Babric) {
			versionName = versionName + " " + loader.name();
			profileName = profileName + " " + selectedVersion + " " + loader.name();
		}else if(loader != ModLoader.None) {
			profileName = profileName + " " + loader.name();
		}
		
		String fabricVersionOverride = null;
		if(loader == ModLoader.Fabric || loader == ModLoader.Babric) {
			JsonObject fabricObject = versionObject.getObject("fabric");
			
			if(fabricObject != null && fabricObject.has("version-name-override")) {
				fabricVersionOverride = fabricObject.getString("version-name-override");	
			}else {
				fabricVersionOverride = selectedVersion;
			}
			
			if(fabricVersionOverride == null) {
				JOptionPane.showMessageDialog(this, "The selected version does not support Fabric!");
				return false;
			}
		}
		
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
		
		String jsonPath;
		if(loader == ModLoader.Fabric || loader == ModLoader.Babric) {
			JsonElement jsonFabric = vanillaObject.get("json-" + loader.name().substring(0, 1).toLowerCase() + "abric");
			if(jsonFabric == null) {
				JOptionPane.showMessageDialog(this, "The selected version does not support " + loader.name() + "!");
				return false;
			}
			jsonPath = "/" + jsonFabric.getAsString().value;
		}else {
			jsonPath = "/" + vanillaObject.getString("json");
		}
		
		// Copy json file into instance folder and set id
		JsonObject json = JsonParser.instance.parseStream(VanillaLauncherInstallerGUI.class.getResourceAsStream(jsonPath));
		json.set("id", versionName);
		StringUtils.saveStringToFile(new File(versionFolder, versionName + ".json"),  json.toString());
		
		// Setup Launcher Profile
		updateLauncherProfile(minecraftDirectory, profileName, versionName, loader, fabricVersionOverride);
		
		JOptionPane.showMessageDialog(this, "Done!");
		return true;
	}
	
	public void updateLauncherProfile(File minecraftDirectory, String profileName, String versionId, ModLoader loader, String fabricVersionOverride) {
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
		profile.set("lastUsed", "2070-01-01T00:00:00.000Z"); // All the way up
		profile.set("type", "custom");
		
		List<String> javaArgs = new ArrayList<>();
		if((loader == ModLoader.Fabric || loader == ModLoader.Babric) && fabricVersionOverride != null) {
			javaArgs.add("-Dfabric.gameVersion=" + fabricVersionOverride);
		}
		
		if(loader == ModLoader.ASMLoader) {
			JsonObject asmloaderObject = VersionList.getJson().getObject("asmloader");
			
			String filename = asmloaderObject.getString("filename");
			
			File fileInDownloadDirectory = new File(DownloadManager.getDownloadDirectory(), filename);
			File fileInMinecraftDirectory = new File(minecraftDirectory, filename);
			
			if(!fileInDownloadDirectory.exists()) {
				DownloadManager.downloadFileAndPrintProgress(asmloaderObject.getString("url"), fileInDownloadDirectory);	
			}
			
			Utils.copyFile(fileInDownloadDirectory, fileInMinecraftDirectory);
			
			javaArgs.add("-javaagent:" + filename);
		}
		profile.set("javaArgs", Utils.combineStringsSeperatedWithSpaces(javaArgs));
		
		StringUtils.saveStringToFile(launcherProfilesFile, root.toString());
	}
	
	public String getMinecraftDirectory() {
		String last = Config.getInstance().lastMinecraftDirectory;
		if(last != null) {
			return last;
		}
		return Utils.getMinecraftDirectory().getAbsolutePath();
	}

	@Override
	public boolean isCompatible(String version, ModLoader loader) {
		if(loader == ModLoader.Fabric || loader == ModLoader.Babric) {
			JsonObject versionObject = VersionList.getJson().getObject("versions").getObject(version);
			if(loader == ModLoader.Babric) {
				return versionObject.getObject("vanilla").has("json-babric");
			}else {
				return versionObject.getObject("vanilla").has("json-fabric");	
			}
		}
		if(loader == ModLoader.ASMLoader) {
			JsonObject asmLoaderObject = VersionList.getJson().getObject("asmloader");
			if(asmLoaderObject == null) {
				return false;
			}
			return asmLoaderObject.has("filename") && asmLoaderObject.has("url");
		}
		if(loader == ModLoader.None) {
			return true;
		}
		return false;
	}

}
