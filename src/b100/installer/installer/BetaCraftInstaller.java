package b100.installer.installer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import b100.installer.Config;
import b100.installer.DownloadHelper;
import b100.installer.Versions;
import b100.installer.Versions.Version;
import b100.installer.config.ConfigUtil;
import b100.installer.gui.classic.BetacraftInstallerGUI;
import b100.installer.util.ModLoader;
import b100.installer.util.Utils;
import b100.json.element.JsonObject;
import b100.utils.StringUtils;

public class BetacraftInstaller implements Installer {

	@Override
	public boolean install(Map<String, Object> parameters) {
		String selectedVersion = (String) parameters.get("version");
		ModLoader loader = (ModLoader) parameters.get("loader");
		File betacraftDirectory = new File((String) parameters.get("betacraftdir"));
		
		System.out.println("Selected Version: " + selectedVersion);
		System.out.println("Selected Mod Loader: " + loader);
		System.out.println("BetaCraft Directory: " + betacraftDirectory.getAbsolutePath());

		// Update config
		Config config = Config.getInstance();
		config.lastSelectedVersion.value = selectedVersion;
		config.lastInstallType.value = BetacraftInstallerGUI.INSTALL_TYPE;
		config.lastBetaCraftDirectory.value = betacraftDirectory.getAbsolutePath();
		config.save();
		
		// Validate path
		if(!betacraftDirectory.exists() || !betacraftDirectory.isDirectory()) {
			JOptionPane.showMessageDialog(null, "Invalid BetaCraft Directory: '" + betacraftDirectory.getAbsolutePath() + "'!");
			config.lastBetaCraftDirectory = null;
			config.save();
			return false;
		}
		
		Version version = Versions.getInstance().get(selectedVersion);
		JsonObject manifest = version.manifest;
		JsonObject betaCraftObject = manifest.getObject("betacraft");
		if(betaCraftObject == null) {
			JOptionPane.showMessageDialog(null, "Version '" + selectedVersion + "' is not compatible with BetaCraft!");
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
				JOptionPane.showMessageDialog(null, "Please start Beta 1.7.3 once before installing!");
				return false;
			}
			
			File btaJarFile = version.getFile("client.jar");
			Utils.createModdedMinecraftJar(minecraftJar, btaJarFile, outputJar);
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
				
				File cachedFile = DownloadHelper.getFile("misc/betacraft/" + launchMethod + ".jar");
				Utils.copyFile(cachedFile, installedLaunchMethod);
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
		String instanceName = (String) parameters.get("instancename");
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
		
		JOptionPane.showMessageDialog(null, "Done!");
		return true;
	}

	@Override
	public boolean isCompatible(String version, ModLoader loader) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
