package b100.installer.installer;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import b100.installer.Config;
import b100.installer.Download;
import b100.installer.Global;
import b100.installer.Versions;
import b100.installer.Versions.Version;
import b100.installer.config.ConfigUtil;
import b100.installer.gui.classic.MultiMCInstallerGUI;
import b100.installer.util.ModLoader;
import b100.installer.util.MultiMCHelper;
import b100.installer.util.Utils;
import b100.json.JsonParser;
import b100.json.element.JsonArray;
import b100.json.element.JsonElement;
import b100.json.element.JsonObject;
import b100.json.element.JsonString;
import b100.utils.StringUtils;

public class MultiMCInstaller implements Installer {

	@Override
	public boolean install(Map<String, Object> parameters, ProgressListener progressListener) {
		return install(new Parameters(parameters), progressListener);
	}
	
	public boolean install(Parameters params, ProgressListener progressListener) {
		System.out.println("Launcher folder: " + params.launcherFolder);
		System.out.println("Selected Version: " + params.version);
		System.out.println("Instance Folder: " + params.instanceFolder.getAbsolutePath());
		
		Config config = Config.getInstance();
		config.lastSelectedVersion.value = params.version.id;
		config.lastInstallType.value = MultiMCInstallerGUI.INSTALL_TYPE;
//		config.lastMultimcDirectory.value = params.instancesFolder.getAbsolutePath();
		config.save();
		
		File jarmodsFolder = new File(params.instanceFolder, "jarmods");
		File patchesFolder = new File(params.instanceFolder, "patches");
		
		JsonObject manifest = params.version.manifest;
		if(manifest == null) {
			JOptionPane.showMessageDialog(null, "Version '" + params.version + "' does not exist!");
			return false;
		}
		JsonObject multimcObject = manifest.getObject("multimc");
		String installType = multimcObject.getString("type");
		
		boolean lwjgl3 = installType.equals("lwjgl3");
		System.out.println("LWJGL 3: " + lwjgl3);
		boolean noawt = installType.equals("noawt") || installType.equals("lwjgl3");
		
		// Icon
		String iconKey = null;
		try {
			File iconsFolder = MultiMCHelper.getIconsDirectory();
			if(iconsFolder != null) {
				progressListener.update("Setting icon...");
				if(manifest.has("icon")) {
					String iconName = manifest.getString("icon");
					System.out.println("Set icon: " + iconName);
					
					String iconUrl = Global.getDownloadUrl() + "bta-installer/icons/" + iconName + ".png";
					
					MultiMCHelper.setIcon(iconName, new Download(iconUrl).setProgressListener(progressListener).getAsImage());
					iconKey = iconName;
				}else {
					System.out.println("No icon in manifest!");
				}	
			}else {
				System.out.println("Icons folder is null, can't set icon!");
			}	
		}catch (Exception e) {
			System.err.println("Icon installation failed!");
			e.printStackTrace();
		}

		// instance.cfg
		{
			progressListener.update("Setting up instance configuration...");
			System.out.println("Setting up instance.cfg");
			
			File instanceCfg = new File(params.instanceFolder, "instance.cfg");
			Map<String, String> instanceProperties = ConfigUtil.loadPropertiesFile(instanceCfg, '=');
			instanceProperties.put("InstanceType", "OneSix");
			instanceProperties.put("notes", "");
			if(!instanceProperties.containsKey("name")) {
				instanceProperties.put("name", "Better than Adventure!");
			}
			
			if(iconKey != null) {
				boolean updateIcon;
				
				if(!instanceProperties.containsKey("iconKey")) {
					// If no icon is set, always set it
					updateIcon = true;
				}else {
					// If the icon is already set, only update it if overrideIcons is enabled
					updateIcon = Config.getInstance().overrideIcons.value;
				}
				
				if(updateIcon) {
					System.out.println("Updating instance icon key");
					instanceProperties.put("iconKey", iconKey);	
				}
			}
			ConfigUtil.saveProperties(instanceCfg, instanceProperties, '=');
		}
		
		String btaPatchUid = "custom.jarmod.bta";
		
		// LWJGL 3 Patch
		File lwjglPatchFile = new File(patchesFolder, "org.lwjgl.json");
		if(lwjgl3) {
			System.out.println("Setting up LWJGL 3 patch");
			
			Utils.extractFile("/resources/multimc/lwjgl3.json", lwjglPatchFile);
		}else {
			lwjglPatchFile.delete();
		}

		progressListener.update("Setting up Minecraft patch...");
		// Minecraft Patch
		File minecraftPatchFile = new File(patchesFolder, "net.minecraft.json");
		if(noawt) {
			System.out.println("Settings up minecraft patch");

			JsonObject minecraftPatch = JsonParser.instance.parseStream(getClass().getResourceAsStream("/resources/multimc/minecraft.json"));
			
			List<JsonElement> traits = new ArrayList<>();
			traits.add(new JsonString("texturepacks"));
			traits.add(new JsonString("noapplet"));
			traits.add(new JsonString("legacyLaunch"));
			traits.add(new JsonString("FirstThreadOnMacOS"));
			minecraftPatch.set("+traits", new JsonArray(traits));
			
			minecraftPatch.set("minecraftArguments", "${auth_player_name} ${auth_session} --username ${auth_player_name} --session ${auth_session} --gameDir ${game_directory} --uuid ${auth_uuid}");
			
			StringUtils.saveStringToFile(minecraftPatchFile, minecraftPatch.toString());
		}else {
			minecraftPatchFile.delete();
		}

		progressListener.update("Setting up BTA patch...");
		// BTA-Patch
		{
			System.out.println("Setting up BTA patch");
			
			String versionFileName = "bta-" + params.version.id + ".jar";
			
			JsonObject patch = createPatch(btaPatchUid, params.version, versionFileName);
			File patchFile = new File(patchesFolder, btaPatchUid + ".json");
			
			StringUtils.saveStringToFile(patchFile, patch.toString());
			
			progressListener.update("Downloading client jar...");
			File file = params.version.getFile("client.jar", progressListener);
			
			progressListener.update("Copying client jar...");
			Utils.copyFile(file, new File(jarmodsFolder, versionFileName), progressListener);
		}

		progressListener.update("Setting up instance package...");
		// mmc-pack.json
		{
			System.out.println("Setting up mmc-pack.json");
			
			JsonObject pack = new JsonObject();

			List<JsonElement> packComponents = new ArrayList<>();
			
			if(lwjgl3) {
				JsonObject lwjglComponent = new JsonObject();
				lwjglComponent.set("uid", "org.lwjgl");
				packComponents.add(lwjglComponent);
			}
			
			JsonObject minecraftComponent = new JsonObject();
			minecraftComponent.set("uid", "net.minecraft");
			minecraftComponent.set("version", "b1.7.3");
			packComponents.add(minecraftComponent);
			
			JsonObject btaComponent = new JsonObject();
			btaComponent.set("uid", btaPatchUid);
			packComponents.add(btaComponent);
			
			pack.set("components", new JsonArray(packComponents));
			pack.set("formatVersion", 1);
			
			File mmcPack = new File(params.instanceFolder, "mmc-pack.json");
			StringUtils.saveStringToFile(mmcPack, pack.toString());
		}
		
		progressListener.update("Done!");
		return true;
	}

	@Override
	public boolean isCompatible(String versionId, ModLoader loader) {
		if(loader == ModLoader.None) {
			return Versions.getInstance().get(versionId).manifest.has("multimc");
		}
		return false;
	}
	
	public JsonObject createPatch(String uid, Version version, String versionFile) {
		JsonObject root = new JsonObject();
		
		List<JsonElement> jarmods = new ArrayList<>();
		
		JsonObject btaJarmod = new JsonObject();
		btaJarmod.set("MMC-displayname", "Better Than Adventure!");
		btaJarmod.set("MMC-filename", versionFile);
		btaJarmod.set("MMC-hint", "local");
		btaJarmod.set("name", "0:0:1");
		jarmods.add(btaJarmod);

		root.set("jarMods", new JsonArray(jarmods));
		root.set("name", "Better Than Adventure!");
		root.set("uid", uid);
		root.set("version", version.id);
		
		return root;
	}
	
	public static class Parameters {
		
		public final File launcherFolder;
		public final File instanceFolder;
		public final Version version;
		
		public Parameters(File launcherFolder, File instanceFolder, Version version) {
			this.launcherFolder = launcherFolder;
			this.instanceFolder = instanceFolder;
			this.version = version;
		}
		
		public Parameters(Map<String, Object> parameters) {
			launcherFolder = new File((String) parameters.get("launcherfolder"));
			if(!launcherFolder.isDirectory()) {
				throw new RuntimeException("Invalid MultiMC / Prism Launcher folder: '" + launcherFolder.getAbsolutePath() + "'!");
			}

			File instancesFolder = new File((String) parameters.get("instancesfolder"));
			if(!instancesFolder.isDirectory()) {
				throw new RuntimeException("Invalid instances folder: '" + instancesFolder.getAbsolutePath() + "'!");
			}

			String versionId = (String) parameters.get("version");
			version = Versions.getInstance().get(versionId);
			if(version == null) {
				throw new NullPointerException("Version is null!");
			}
			
			String instanceFolderName;
			if(parameters.containsKey("instancename")) {
				instanceFolderName = (String) parameters.get("instancename");
			}else {
				instanceFolderName = Global.MULTIMC_INSTANCE_FOLDER_NAME;
			}

			instanceFolder = new File(instancesFolder, instanceFolderName);
		}
	}
	
}
