package b100.installer.gui.modern.multimc;

import java.awt.EventQueue;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import b100.installer.DownloadManager;
import b100.installer.Global;
import b100.installer.Utils;
import b100.installer.gui.modern.ActionListener;
import b100.installer.gui.modern.GuiBackground;
import b100.installer.gui.modern.GuiButton;
import b100.installer.gui.modern.GuiElement;
import b100.installer.gui.modern.GuiScreen;
import b100.installer.gui.modern.GuiSelectVersion;
import b100.installer.gui.modern.Textures;
import b100.installer.installer.MultiMcInstaller;
import b100.json.JsonParser;
import b100.json.element.JsonArray;
import b100.json.element.JsonObject;

public class GuiInstallMultiMc extends GuiScreen implements ActionListener {

	public File instancesFolder;
	public File instanceFolder;
	
	public GuiButton buttonInstall;
	public GuiButton buttonSelectVersion;
	
	public String newestVersion = null;
	public String selectedVersion = null;
	
	public MultiMcInstaller multiMcInstaller = new MultiMcInstaller();
	
	public GuiInstallMultiMc(GuiScreen parentScreen, File instancesFolder) {
		super(parentScreen);
		
		if(instancesFolder == null) {
			if(Utils.multiMcInstanceFolderOverride != null) {
				instancesFolder = Utils.multiMcInstanceFolderOverride;
			}else {
				throw new NullPointerException("Instances folder is null!");	
			}
		}
		
		this.instancesFolder = instancesFolder;

		instanceFolder = new File(instancesFolder, Global.MULTIMC_INSTANCE_FOLDER_NAME);
		File mmcPackFile = new File(instanceFolder, "mmc-pack.json");
		File patchesFolder = new File(instanceFolder, "patches");
		File jarmodsFolder = new File(instanceFolder, "jarmods");

		String currentVersion = null;
		
		if(MultiMcInstaller.isInstance(instanceFolder)) {
			System.out.println("Selected instance exists!");
			
			// Figure out which version of BTA is currently installed
			// First, find the name of the BTA patch in mmc-pack.json
			String existingBtaPatchName = null;
			JsonObject mmcPack = JsonParser.instance.parseFileContent(mmcPackFile);
			JsonArray components = mmcPack.getArray("components");
			for(int i=0; i < components.length(); i++) {
				JsonObject obj = components.get(i).getAsObject();
				String uid = obj.getString("uid");
				
				// Name is different depending on which version of the updater was used
				if(uid.equals("org.multimc.jarmod.bta")) {
					existingBtaPatchName = "org.multimc.jarmod.bta";
				}
				if(uid.equals("custom.jarmod.bta")) {
					existingBtaPatchName = "custom.jarmod.bta";
				}
			}
			
			// Then we read the filename of the BTA jar and the version from the patch file
			File oldBtaJarFile = null;
			if(existingBtaPatchName != null) {
				File btaPatchFile = new File(patchesFolder, existingBtaPatchName + ".json");
				JsonObject btaPatch = JsonParser.instance.parseFileContent(btaPatchFile);
				currentVersion = btaPatch.getString("version");
				JsonArray jarMods = btaPatch.getArray("jarMods");
				JsonObject btaJarMod = jarMods.get(0).getAsObject();
				String filename = btaJarMod.getString("MMC-filename");
				oldBtaJarFile = new File(jarmodsFolder, filename);
			}else {
				System.out.println("Could not find BTA in instance!");
			}
			
			System.out.println("Version: " + currentVersion);
			System.out.println("Old BTA jar: " + oldBtaJarFile);
		}else {
			System.out.println("Selected instance does not exist!");
		}
		
		// Figure out what the newest version of BTA is
		String url = "https://downloads.betterthanadventure.net/bta-client/release/versions.json";
		File btaVersionsFile = new File(Global.getInstallerDirectory(), "bta-versions.json");
		DownloadManager.downloadFileAndPrintProgress(url, btaVersionsFile);
		JsonObject obj = JsonParser.instance.parseFileContent(btaVersionsFile);
		newestVersion = obj.getString("default");
		if(newestVersion.startsWith("v")) {
			newestVersion = newestVersion.substring(1);
		}
		boolean upToDate = newestVersion.equals(currentVersion);
		System.out.println("Newest BTA Version: " + newestVersion);
		System.out.println("Is up to date: " + upToDate);
		
		selectedVersion = newestVersion;
	}

	@Override
	protected void onInit() {
		add(new GuiBackground(this));
		
		buttonInstall = add(new GuiButton(this, "Install").addActionListener(this));
		buttonSelectVersion = add(new GuiButton(this, "Version: " + selectedVersion).addActionListener(this));
	}
	
	@Override
	public void draw() {
		super.draw();

		int x = (renderer.getWidth() - Textures.logo.getWidth()) / 2;
		int y = 30;
		
		renderer.drawImage(Textures.logo, x, y);
		
		fontRenderer.drawString("Install into MultiMC / Prism Launcher", 2, 02, 0x505050, true);
		fontRenderer.drawString("Instance Folder: " + instanceFolder, 2, 12, 0x505050, true);
	}
	
	@Override
	public void onResize() {
		super.onResize();
		
		int x1 = width / 2 - 100;
		int y1 = height / 4 + 24;
		int p = 24;
		
		buttonInstall.setPosition(x1, y1 + p * 1);
		buttonSelectVersion.setPosition(x1, y1 + p * 2);
	}

	@Override
	public void actionPerformed(GuiElement source) {
		if(source == buttonInstall) {
			install();
		}
		if(source == buttonSelectVersion) {
			setScreen(new GuiSelectVersion(this, multiMcInstaller, (version) -> {
				selectedVersion = version;
				buttonSelectVersion.text = "Version: " + selectedVersion;
				setScreen(GuiInstallMultiMc.this);
			}, selectedVersion));
		}
	}
	
	public void install() {
		buttonInstall.setClickable(false);
		
		final Map<String, Object> parameters = new HashMap<>();
		
		parameters.put("instancesfolder", instancesFolder.getAbsolutePath());
		parameters.put("version", selectedVersion);
		parameters.put("instancename", Global.MULTIMC_INSTANCE_FOLDER_NAME);
		
		Runnable runnable = () -> {
			multiMcInstaller.install(parameters);
			
			EventQueue.invokeLater(() -> {
				buttonInstall.setClickable(true);
			});
		};
		
		Thread thread = new Thread(runnable);
		thread.setName("Install-Thread");
		thread.start();
	}
}
