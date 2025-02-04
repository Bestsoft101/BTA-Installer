package b100.installer.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import b100.installer.Config;
import b100.installer.DownloadManager;
import b100.installer.ModLoader;
import b100.installer.Utils;
import b100.installer.VersionList;
import b100.installer.config.ConfigUtil;
import b100.installer.gui.utils.GuiUtils;
import b100.installer.gui.utils.VersionComponent;
import b100.json.element.JsonObject;
import b100.utils.StringUtils;

@SuppressWarnings("serial")
public class BetaCraftInstallerGUI extends BaseInstallerGUI {

	public static final String INSTALL_TYPE = "betacraft";
	
	public JTextField betacraftDirectoryTextfield;
	public JTextField instanceTextfield;
	
	public BetaCraftInstallerGUI(InstallerGUI installerGUI) {
		super(installerGUI);
		
		int inset = 4;
		getGridBagConstraints().insets.set(inset, inset, inset, inset);
		
		betacraftDirectoryTextfield = new JTextField();
		betacraftDirectoryTextfield.setText(getBetaCraftDirectory());
		
		instanceTextfield = new JTextField();
		instanceTextfield.setText("Better Than Adventure!");
		
		List<ModLoader> modLoaders = new ArrayList<>();
		modLoaders.add(ModLoader.None);
		versionComponent = new VersionComponent(modLoaders, (version, modLoader) -> modLoader == ModLoader.None && isVersionSupported(version));
		
		installButton = new JButton("Install");
		installButton.addActionListener(this);
		
		add(GuiUtils.createImagePanel("/logo.png"), 0, 0, 1, 1);
		add(GuiUtils.createTitledPanel(betacraftDirectoryTextfield, "BetaCraft Directory"), 0, 1, 1, 0);
		add(GuiUtils.createTitledPanel(instanceTextfield, "Instance"), 0, 2, 1, 0);
		add(versionComponent, 0, 3, 1, 0);
		add(installButton, 0, 4, 1, 0);
	}
	
	public boolean isVersionSupported(String version) {
		JsonObject versionObject = VersionList.getVersion(version);
		JsonObject betaCraftObject = versionObject.getObject("betacraft");
		return betaCraftObject != null;
	}

	@Override
	public boolean install() {
		String selectedVersion = versionComponent.getSelectedVersion();
		ModLoader loader = versionComponent.getSelectedLoader();
		File betacraftDirectory = new File(betacraftDirectoryTextfield.getText());
		
		System.out.println("Selected Version: " + selectedVersion);
		System.out.println("Selected Mod Loader: " + loader);
		System.out.println("BetaCraft Directory: " + betacraftDirectory.getAbsolutePath());

		// Update config
		Config config = Config.getInstance();
		config.lastSelectedVersion.value = selectedVersion;
		config.lastInstallType.value = INSTALL_TYPE;
		config.lastBetaCraftDirectory.value = betacraftDirectory.getAbsolutePath();
		config.save();
		
		// Validate path
		if(!betacraftDirectory.exists() || !betacraftDirectory.isDirectory()) {
			JOptionPane.showMessageDialog(this, "Invalid BetaCraft Directory: '" + betacraftDirectory.getAbsolutePath() + "'!");
			config.lastBetaCraftDirectory = null;
			config.save();
			return false;
		}

		JsonObject versionObject = VersionList.getVersion(selectedVersion);
		JsonObject betaCraftObject = versionObject.getObject("betacraft");
		if(betaCraftObject == null) {
			JOptionPane.showMessageDialog(this, "Version '" + selectedVersion + "' is not compatible with BetaCraft!");
			return false;
		}
		
		String versionName = "BTA " + selectedVersion;
		
		// Combine minecraft and mod jar and put into versions folder
		File versionsFolder = new File(betacraftDirectory, "versions");
		File outputJar = new File(versionsFolder, versionName + ".jar");
		if(!outputJar.exists()) {
			System.out.println("Installing version");
			File minecraftJar = new File(versionsFolder, "b1.7.3.jar");
			if(!minecraftJar.exists()) {
				JOptionPane.showMessageDialog(this, "Please start Beta 1.7.3 once before installing!");
				return false;
			}

			File modJarFile = DownloadManager.getFile(versionObject.getString("jar"));
			Utils.createModdedMinecraftJar(minecraftJar, modJarFile, outputJar);
		}else {
			System.out.println("Version is installed");
		}

		String launchMethod = betaCraftObject.getString("launch-method");
		if(launchMethod.equals("default")) {
			System.out.println("Using default launch method");
		}else {
			File installedLaunchMethod = new File(betacraftDirectory, "launcher/launch-methods/" + launchMethod + ".jar");

			// Put CustomLaunch jar into launch-methods folder
			if(!installedLaunchMethod.exists()) {
				System.out.println("Installing launch method");
				File launchMethodJar = DownloadManager.getFile(launchMethod + ".jar");
				
				installedLaunchMethod.getParentFile().mkdirs();
				Utils.copyFile(launchMethodJar, installedLaunchMethod);
			}else {
				System.out.println("Launch method is installed");
			}
			
			// Create version info file
			File versionInfoFile = new File(versionsFolder, "jsons/" + versionName + ".info");
			if(!versionInfoFile.exists()) {
				System.out.println("Creating version info file");
				
				String string = "launch-method:" + launchMethod;
				StringUtils.saveStringToFile(versionInfoFile, string);
			}else {
				System.out.println("Version info file exists!");
			}
		}
		
		// Create or update BTA instance
		String instanceName = instanceTextfield.getText();
		File instanceFile = new File(betacraftDirectory, "/launcher/instances/" + instanceName + ".txt");
		boolean useProxy = betaCraftObject.getBoolean("proxy");
		
		Map<String, String> properties;
		if(instanceFile.exists()) {
			properties = Utils.loadProperties(instanceFile);
		}else {
			properties = new HashMap<>();
		}
		
		// If these don't exist the instance will be reset
		if(!properties.containsKey("width")) properties.put("width", "854");
		if(!properties.containsKey("height")) properties.put("height", "480");
		if(!properties.containsKey("addons")) properties.put("addons", "");
		if(!properties.containsKey("gameDir")) properties.put("gameDir", new File(betacraftDirectory, instanceName).getAbsolutePath());
		
		properties.put("proxy", String.valueOf(useProxy));
		properties.put("version", versionName);
		
		ConfigUtil.saveProperties(instanceFile, properties, ':');
		
		JOptionPane.showMessageDialog(this, "Done!");
		return true;
	}
	
	public String getBetaCraftDirectory() {
		String last = Config.getInstance().lastBetaCraftDirectory.value;
		if(last != null) {
			return last;
		}
		return getDefaultBetacraftDirectory().getAbsolutePath();
	}
	
	public static File getDefaultBetacraftDirectory() {
		String folder = null;
		
		String os = System.getProperty("os.name").toLowerCase();
		boolean isWindows = os.indexOf("win") >= 0;
		boolean isLinux = os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") >= 0;
		boolean isMac = os.indexOf("mac") >= 0;
		
		if(isWindows) {
			folder = System.getenv("APPDATA") + "\\.betacraft\\";
		}else if(isLinux) {
			folder = System.getProperty("user.home") + "/.betacraft";
		}else if(isMac) {
			folder = System.getProperty("user.home") + "/Library/Application Support/betacraft/";
		}else {
			return Utils.getAppDirectory("betacraft");
		}
		return new File(folder);
	}

}
