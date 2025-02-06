package b100.installer.gui.classic;

import java.io.File;
import java.util.ArrayList;
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
import b100.installer.gui.classic.VersionListGUI.VersionFilter;
import b100.json.JsonParser;
import b100.json.element.JsonArray;
import b100.json.element.JsonElement;
import b100.json.element.JsonObject;
import b100.json.element.JsonString;
import b100.utils.StringUtils;

@SuppressWarnings("serial")
public class MultiMCInstallerGUI extends BaseInstallerGUI implements VersionFilter {
	
	public static final String INSTALL_TYPE = "multimc";
	
	public JTextField multimcDirectoryTextfield;
	
	public MultiMCInstallerGUI(ClassicInstallerGUI installerGUI) {
		super(installerGUI);

		int inset = 4;
		getGridBagConstraints().insets.set(inset, inset, inset, inset);
		
		multimcDirectoryTextfield = new JTextField(Config.getInstance().lastMultimcDirectory.value);

		List<ModLoader> modLoaders = new ArrayList<>();
		modLoaders.add(ModLoader.None);
		versionComponent = new VersionComponent(modLoaders, this);
		
		installButton = new JButton("Install");
		installButton.addActionListener(this);
		
		add(GuiUtils.createImagePanel("/logo.png"), 0, 0, 1, 1);
		add(GuiUtils.createTitledPanel(multimcDirectoryTextfield, "MultiMC / Prism Launcher Directory"), 0, 1, 1, 0);
		add(versionComponent, 0, 2, 1, 0);
		add(installButton, 0, 3, 1, 0);
	}

	@Override
	public boolean install() {
		File multimcDirectory = new File(multimcDirectoryTextfield.getText());
		if(!multimcDirectory.exists() || !multimcDirectory.isDirectory()) {
			JOptionPane.showMessageDialog(this, "Invalid MultiMC / Prism Launcher Directory: '" + multimcDirectory.getAbsolutePath() + "'!");
			return false;
		}
		
		System.out.println("MultiMC Directory: " + multimcDirectory);
		
		String version = versionComponent.getSelectedVersion();
		System.out.println("Selected Version: " + version);
		
		Config config = Config.getInstance();
		config.lastSelectedVersion.value = version;
		config.lastInstallType.value = INSTALL_TYPE;
		config.lastMultimcDirectory.value = multimcDirectory.getAbsolutePath();
		config.save();
		
		String instanceFolderName = "BTA-Installer-Instance";
		
		File instanceFolder = new File(multimcDirectory, "instances/" + instanceFolderName);
		if(!instanceFolder.exists()) {
			instanceFolder.mkdirs();
		}
		File jarmodsFolder = new File(instanceFolder, "jarmods");
		File patchesFolder = new File(instanceFolder, "patches");
		
		JsonObject versionObject = VersionList.getVersion(version);
		JsonObject multimcObject = versionObject.getObject("multimc");
		String installType = multimcObject.getString("type");
		String versionFileName = versionObject.getString("jar");
		
		boolean lwjgl3 = installType.equals("lwjgl3");
		System.out.println("LWJGL 3: " + lwjgl3);
		boolean noawt = installType.equals("noawt") || installType.equals("lwjgl3");
		
		// instance.cfg
		{
			System.out.println("Setting up instance.cfg");
			
			File instanceCfg = new File(instanceFolder, "instance.cfg");
			Map<String, String> instanceProperties = ConfigUtil.loadPropertiesFile(instanceCfg, '=');
			instanceProperties.put("InstanceType", "OneSix");
			instanceProperties.put("notes", "");
			if(!instanceProperties.containsKey("name")) {
				instanceProperties.put("name", "BTA");
			}
			if(!instanceProperties.containsKey("iconKey")) {
				instanceProperties.put("iconKey", "planks");
			}
			ConfigUtil.saveProperties(instanceCfg, instanceProperties, '=');
		}
		
		String btaPatchUid = "custom.jarmod.bta";
		
		// LWJGL 3 Patch
		File lwjglPatchFile = new File(patchesFolder, "org.lwjgl.json");
		if(lwjgl3) {
			System.out.println("Setting up LWJGL 3 patch");
			
			File file = DownloadManager.getFile("multimc/patches/lwjgl3.json");
			Utils.copyFile(file, lwjglPatchFile);
		}else {
			lwjglPatchFile.delete();
		}
		
		// Minecraft Patch
		File minecraftPatchFile = new File(patchesFolder, "net.minecraft.json");
		if(noawt) {
			System.out.println("Settings up minecraft patch");

			File file = DownloadManager.getFile("multimc/patches/minecraft.json");
			JsonObject minecraftPatch = JsonParser.instance.parseFileContent(file);
			
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
		
		// BTA-Patch
		{
			System.out.println("Setting up BTA patch");
			
			JsonObject patch = createPatch(btaPatchUid, version, versionFileName);
			File patchFile = new File(patchesFolder, btaPatchUid + ".json");
			
			StringUtils.saveStringToFile(patchFile, patch.toString());
			
			File jarFile = new File(jarmodsFolder, versionFileName);
			Utils.copyFile(DownloadManager.getFile(versionFileName), jarFile);
		}
		
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
			
			File mmcPack = new File(instanceFolder, "mmc-pack.json");
			StringUtils.saveStringToFile(mmcPack, pack.toString());
		}
		
		JOptionPane.showMessageDialog(this, "Done!");
		return true;
	}
	
	public JsonObject createPatch(String uid, String version, String versionFile) {
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
		root.set("version", version);
		
		return root;
	}

	@Override
	public boolean isCompatible(String version, ModLoader loader) {
		if(loader == ModLoader.None) {
			return VersionList.getVersion(version).has("multimc");
		}
		return false;
	}
	
}
