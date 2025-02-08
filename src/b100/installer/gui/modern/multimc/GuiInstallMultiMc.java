package b100.installer.gui.modern.multimc;

import java.awt.EventQueue;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import b100.installer.Global;
import b100.installer.Utils;
import b100.installer.VersionList;
import b100.installer.gui.modern.ActionListener;
import b100.installer.gui.modern.GuiBackground;
import b100.installer.gui.modern.GuiButton;
import b100.installer.gui.modern.GuiCheckbox;
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
	public GuiButton buttonSelectInstance;
	
	public GuiCheckbox checkboxAdvancedMode;
	
	public final String latestVersion;
	public String selectedVersion = null;
	public InstanceInfo selectedInstance = null;
	
	public MultiMcInstaller multiMcInstaller = new MultiMcInstaller();
	
	public boolean advancedMode = false;
	
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
		this.instanceFolder = new File(instancesFolder, Global.MULTIMC_INSTANCE_FOLDER_NAME);
		
		this.latestVersion = VersionList.getLatestVersion();
		System.out.println("Latest BTA Version: " + latestVersion);
		
		selectedVersion = latestVersion;
		
		setInstance(Global.MULTIMC_INSTANCE_FOLDER_NAME);
	}

	@Override
	protected void onInit() {
		add(new GuiBackground(this));
		
		buttonInstall = add(new GuiButton(this, "Install").addActionListener(this));
		
		if(advancedMode) {
			buttonSelectVersion = add(new GuiButton(this, "Version").addActionListener(this));
			buttonSelectInstance = add(new GuiButton(this, "Instance").addActionListener(this));	
		}else {
			setInstance(Global.MULTIMC_INSTANCE_FOLDER_NAME);
			selectedVersion = latestVersion;
		}
		
		checkboxAdvancedMode = add(new GuiCheckbox(this, "Advanced Mode", advancedMode).addActionListener(this));

		refresh();
	}
	
	@Override
	public void draw() {
		super.draw();

		int x = (renderer.getWidth() - Textures.logo.getWidth()) / 2;
		int y = 30;
		
		renderer.drawImage(Textures.logo, x, y + 8);
		
		fontRenderer.drawString("Install into MultiMC / Prism Launcher", 2, 02, 0x505050, true);
//		fontRenderer.drawString("Instance Folder: " + instanceFolder, 2, 12, 0x505050, true);
		
		int x1 = posX + width / 2;
		int y1 = buttonInstall.posY - 36;
		
		if(selectedInstance.instanceExists) {
			if(!advancedMode) {
				fontRenderer.drawCenteredString("Installed Version: " + selectedInstance.currentVersion, x1, y1, 0xFFFFFF, true);
				if(selectedInstance.currentVersion.equals(latestVersion)) {
					fontRenderer.drawCenteredString("Up to date!", x1, y1 + 12, 0xFFFF00, true);
				}else {
					fontRenderer.drawCenteredString("Update Available: " + latestVersion, x1, y1 + 12, 0x00FF00, true);	
				}
			}else {
				fontRenderer.drawCenteredString("Installed Version: " + selectedInstance.currentVersion, x1, y1 + 18, 0xFFFFFF, true);
			}
			
		}else {
			fontRenderer.drawCenteredString("Not installed!", x1, y1, 0xFFFFFF, true);
		}
	}
	
	@Override
	public void onResize() {
		super.onResize();
		
		int x1 = width / 2 - 100;
		int y1 = height / 4 + 24;
		int p = 24;
		
		if(advancedMode) {
			buttonInstall.setPosition(x1, y1 + p * 2);
			buttonSelectVersion.setPosition(x1, y1 + p * 3);
			buttonSelectInstance.setPosition(x1, y1 + p * 4);	
		}else {
			buttonInstall.setPosition(x1, y1 + p * 3);
		}
		
		checkboxAdvancedMode.setPosition(8, height - checkboxAdvancedMode.height - 8);
	}

	@Override
	public void actionPerformed(GuiElement source) {
		if(source == buttonInstall) {
			install();
		}
		if(source == buttonSelectVersion) {
			setScreen(new GuiSelectVersion(this, multiMcInstaller, (version) -> {
				selectedVersion = version;
				refresh();
				setScreen(GuiInstallMultiMc.this);
			}, selectedVersion));
		}
		if(source == buttonSelectInstance) {
			setScreen(new GuiSelectInstance(this, instancesFolder, (instance) -> {
				setInstance(instance);
				refresh();
				setScreen(GuiInstallMultiMc.this);
			}, selectedInstance.getInstanceFolderName()));
		}
		if(source == checkboxAdvancedMode) {
			advancedMode = checkboxAdvancedMode.isChecked();
			
			System.out.println("Advanced Mode: " + advancedMode);
			
			boolean wasFocused = checkboxAdvancedMode.isFocused();
			
			init();
			
			if(wasFocused) {
				checkboxAdvancedMode.setFocused(true);	
			}
		}
	}
	
	public void install() {
		buttonInstall.setClickable(false);
		
		final Map<String, Object> parameters = new HashMap<>();
		
		parameters.put("instancesfolder", instancesFolder.getAbsolutePath());
		parameters.put("version", selectedVersion);
		parameters.put("instancename", selectedInstance.getInstanceFolderName());
		
		Runnable runnable = () -> {
			multiMcInstaller.install(parameters);
			
			EventQueue.invokeLater(() -> {
				buttonInstall.setClickable(true);
				
				refresh();
			});
		};
		
		Thread thread = new Thread(runnable);
		thread.setName("Install-Thread");
		thread.start();
	}
	
	public void setInstance(String instanceName) {
		File instanceFolder = new File(instancesFolder, instanceName);
		
		if(selectedInstance == null || !selectedInstance.instanceFolder.equals(instanceFolder)) {
			System.out.println("Set Instance: " + instanceName);
			
			this.selectedInstance = new InstanceInfo(instanceFolder);	
		}
	}
	
	public void refresh() {
		if(selectedInstance != null) {
			selectedInstance = new InstanceInfo(selectedInstance.instanceFolder);
		}
		
		if(advancedMode) {
			buttonSelectVersion.text = "Version: " + selectedVersion;
			buttonSelectInstance.text = "Instance: " + selectedInstance.getName();
			
			if(selectedInstance.currentVersion.equals(selectedVersion)) {
				buttonInstall.text = "Reinstall";	
			}else {
				buttonInstall.text = "Install";
			}
		}else {
			if(selectedInstance.currentVersion.equals(selectedVersion)) {
				buttonInstall.text = "Reinstall";	
			}else {
				buttonInstall.text = "Update";
			}
		}
	}
	
	class InstanceInfo {
		
		/** The folder of the instance. Cannot be null */
		public final File instanceFolder;
		
		/** Does the instance exist? An instance needs both instance.cfg and mmc-pack.json files to be valid */
		public final boolean instanceExists;
		
		/** The version of BTA installed in this instance. Contains null if the instance doesn't exist, or the version couldn't be read. */
		public final String currentVersion;
		
		/** The BTA jar. May be null if the instance doesn't exist or the file couldn't be found */
		public final File oldBtaJarFile;
		
		/** The name set in instance.cfg. May be null if the instance doesn't exist, or the instance doesn't have a custom name set */
		public final String displayName;
		
		public InstanceInfo(File instanceFolder) {
			if(instanceFolder == null) {
				throw new NullPointerException("Instance folder is null!");
			}
			
			this.instanceFolder = instanceFolder;
			this.instanceExists = MultiMcInstaller.isInstance(instanceFolder);
			
			if(MultiMcInstaller.isInstance(instanceFolder)) {
				displayName = MultiMcInstaller.getInstanceName(instanceFolder);
			}else {
				displayName = null;
			}
			
			File mmcPackFile = new File(instanceFolder, "mmc-pack.json");
			File patchesFolder = new File(instanceFolder, "patches");
			File jarmodsFolder = new File(instanceFolder, "jarmods");
			
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
				currentVersion = null;
				oldBtaJarFile = null;
			}
			
			System.out.println("Version: " + currentVersion);
			System.out.println("Old BTA jar: " + oldBtaJarFile);
		}
		
		/**
		 * Returns the display name if it's not null, or the folder name instead
		 */
		public String getName() {
			if(displayName == null) {
				return instanceFolder.getName();
			}
			return displayName;
		}
		
		public String getInstanceFolderName() {
			return instanceFolder.getName();
		}
		
		@Override
		public String toString() {
			return getName();
		}
		
	}
}
