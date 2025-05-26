package b100.installer.gui.modern.screen.multimc;

import java.awt.EventQueue;
import java.io.File;
import java.util.Objects;

import b100.installer.Global;
import b100.installer.Versions;
import b100.installer.Versions.Version;
import b100.installer.gui.modern.InstallerGuiModern;
import b100.installer.gui.modern.element.GuiBackground;
import b100.installer.gui.modern.element.GuiButton;
import b100.installer.gui.modern.element.GuiCheckbox;
import b100.installer.gui.modern.element.GuiDialog;
import b100.installer.gui.modern.element.GuiElement;
import b100.installer.gui.modern.element.GuiImageElement;
import b100.installer.gui.modern.element.GuiProgressBar;
import b100.installer.gui.modern.element.GuiTextElement;
import b100.installer.gui.modern.render.Textures;
import b100.installer.gui.modern.screen.GuiScreen;
import b100.installer.gui.modern.screen.GuiSelectVersion;
import b100.installer.gui.modern.util.ActionListener;
import b100.installer.installer.MultiMCInstaller;
import b100.installer.installer.MultiMCInstaller.Parameters;
import b100.installer.installer.ProgressListener;
import b100.installer.util.MultiMCHelper;
import b100.json.JsonParser;
import b100.json.element.JsonArray;
import b100.json.element.JsonObject;

public class GuiInstallMultiMC extends GuiScreen implements ActionListener, ProgressListener {

	public File instancesFolder;
	
	public GuiButton buttonInstall;
	public GuiButton buttonSelectVersion;
	public GuiButton buttonSelectInstance;
	
	public GuiCheckbox checkboxAdvancedMode;
	
	public final Version latestVersion;
	public Version selectedVersion = null;
	public InstanceInfo selectedInstance = null;
	
	public MultiMCInstaller multiMcInstaller = new MultiMCInstaller();
	
	public boolean advancedMode = false;
	
	public boolean installing = false;
	public String installerStatus = null;
	
	public GuiTextElement line0;
	public GuiTextElement line1;
	
	public GuiImageElement logo;
	
	public GuiProgressBar progressBar;
	
	public GuiInstallMultiMC(GuiScreen parentScreen, File launcherFolder) {
		super(parentScreen);
		if(launcherFolder == null) {
			throw new NullPointerException("Launcher folder is null!");
		}
		
		instancesFolder = MultiMCHelper.getInstancesDirectory();
		if(instancesFolder == null) {
			throw new NullPointerException("Instances folder is null!");
		}
		
		this.latestVersion = Versions.getInstance().getLatestVersion();
		System.out.println("Latest BTA Version: " + latestVersion);
		
		selectedVersion = latestVersion;
		
		setInstance(Global.MULTIMC_INSTANCE_FOLDER_NAME);
	}

	@Override
	protected void onInit() {
		add(new GuiBackground(this));
		
		logo = add(new GuiImageElement(Textures.logo));
		
		add(new GuiTextElement().setText("Install into MultiMC / Prism Launcher").setTextColor(0x505050).setPosition(2, 2));
		
		buttonInstall = add(new GuiButton(this, "Install").addActionListener(this));
		
		if(advancedMode) {
			buttonSelectVersion = add(new GuiButton(this, "Version").addActionListener(this));
			buttonSelectInstance = add(new GuiButton(this, "Instance").addActionListener(this));	
		}else {
			setInstance(Global.MULTIMC_INSTANCE_FOLDER_NAME);
			selectedVersion = latestVersion;
		}
		
		checkboxAdvancedMode = add(new GuiCheckbox(this, "Advanced Mode", advancedMode).addActionListener(this));
		
		line0 = add(new GuiTextElement().setAlign(0.5, 0.0));
		line1 = add(new GuiTextElement().setAlign(0.5, 0.0));
		
		progressBar = add(new GuiProgressBar().setEnabled(false).setProgress(0.5f));
		
		refresh();
	}
	
	@Override
	public void draw() {
		updateStrings();
		
		super.draw();
	}
	
	public void updateStrings() {
		line0.setText(null).setTextColor(0xFFFFFF);
		line1.setText(null).setTextColor(0xFFFFFF);
		
		if(installing) {
			line1.setText(installerStatus);
			return;
		}
		
		if(!selectedInstance.instanceExists) {
			line1.setText("Not installed!");
			return;
		}
		
		if(advancedMode) {
			line1.setText("Installed Version: " + Version.getDisplayName(selectedInstance.currentVersion));
			return;
		}

		line0.setText("Installed Version: " + Version.getDisplayName(selectedInstance.currentVersion));
		if(Objects.equals(selectedInstance.currentVersion, latestVersion)) {
			line1.setText("Up to date!").setTextColor(0x00FF00);
		}else {
			line1.setText("Update Available: " + latestVersion.getDisplayName()).setTextColor(0x00FF00);
		}
	}
	
	@Override
	public void onResize() {
		super.onResize();

		int center = posX + width / 2;
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
		
		int y2 = buttonInstall.posY - 24;
		if(advancedMode) {
			y2 += 6;
		}
		
		line0.setPosition(center, y2 - 12).setSize(0, 0);
		line1.setPosition(center, y2 + 0).setSize(0, 0);
		
		progressBar.setPosition(center - progressBar.width / 2, line1.posY + 12);
		
		checkboxAdvancedMode.setPosition(8, height - checkboxAdvancedMode.height - 8);

		logo.setPosition((width - logo.width) / 2, 38);
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
				setScreen(GuiInstallMultiMC.this);
			}, selectedVersion));
		}
		if(source == buttonSelectInstance) {
			setScreen(new GuiSelectInstance(this, instancesFolder, (instance) -> {
				setInstance(instance);
				refresh();
				setScreen(GuiInstallMultiMC.this);
			}, selectedInstance.getInstanceFolderName()));
		}
		if(source == checkboxAdvancedMode) {
			advancedMode = checkboxAdvancedMode.isChecked();
			
			boolean wasFocused = checkboxAdvancedMode.isFocused();
			
			init();
			
			if(wasFocused) {
				checkboxAdvancedMode.setFocused(true);	
			}
		}
	}
	
	public void install() {
		buttonInstall.setClickable(false);
		
		File launcherDirectory = MultiMCHelper.getLauncherDirectory();
		File instanceFolder = new File(instancesFolder, selectedInstance.getInstanceFolderName());
		
		Parameters params = new Parameters(launcherDirectory, instanceFolder, selectedVersion);
		
		Runnable runnable = () -> {
			try {
				installing = true;
				update("Installing...");
				
				multiMcInstaller.install(params, this);

				installing = false;
				update("Done!");
				
				EventQueue.invokeLater(() -> {
					buttonInstall.setClickable(true);
					
					GuiDialog doneDialog = new GuiDialog(this);
					doneDialog.add(new GuiTextElement("Done!", 0.5, 0.0));
					
					GuiButton button = new GuiButton(this, "Close");
					button.addActionListener(e -> doneDialog.close());
					button.width = 150;
					doneDialog.add(button);
					
					add(doneDialog);
					
					refresh();
				});
			}catch (Exception e) {
				InstallerGuiModern.getInstance().onCrash(e);
			}
		};
		
		Thread thread = new Thread(runnable);
		thread.setName("Install-Thread");
		thread.start();
	}

	@Override
	public void update(String string) {
		installerStatus = string;
		
		InstallerGuiModern.getInstance().scheduleRepaint();
		progressBar.setEnabled(false);
	}

	@Override
	public void setProgress(float progress) {
		progressBar.setEnabled(true);
		progressBar.setProgress(progress);
		
		InstallerGuiModern.getInstance().scheduleRepaint();
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
			buttonSelectVersion.text = "Version: " + selectedVersion.getDisplayName();
			buttonSelectInstance.text = "Instance: " + selectedInstance.getName();
			
			if(Objects.equals(selectedInstance.currentVersion, selectedVersion)) {
				buttonInstall.text = "Reinstall";	
			}else {
				buttonInstall.text = "Install";
			}
		}else {
			if(Objects.equals(selectedInstance.currentVersion, selectedVersion)) {
				buttonInstall.text = "Reinstall";	
			}else if(selectedInstance.instanceExists) {
				buttonInstall.text = "Update";
			}else {
				buttonInstall.text = "Install";
			}
		}
	}
	
	public static class InstanceInfo {
		
		/** The folder of the instance. Cannot be null */
		public final File instanceFolder;
		
		/** Does the instance exist? An instance needs both instance.cfg and mmc-pack.json files to be valid */
		public final boolean instanceExists;
		
		/** The version of BTA installed in this instance. Contains null if the instance doesn't exist, or the version couldn't be read. */
		public final Version currentVersion;
		
		/** The BTA jar. May be null if the instance doesn't exist or the file couldn't be found */
		public final File oldBtaJarFile;
		
		/** The name set in instance.cfg. May be null if the instance doesn't exist, or the instance doesn't have a custom name set */
		public final String displayName;
		
		public InstanceInfo(File instanceFolder) {
			if(instanceFolder == null) {
				throw new NullPointerException("Instance folder is null!");
			}
			
			this.instanceFolder = instanceFolder;
			this.instanceExists = MultiMCHelper.isInstance(instanceFolder);
			
			if(!MultiMCHelper.isInstance(instanceFolder)) {
				displayName = null;
				oldBtaJarFile = null;
				currentVersion = null;
				return;
			}
			
			displayName = MultiMCHelper.getInstanceName(instanceFolder);
			
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
				String versionName = btaPatch.getString("version");
				currentVersion = Versions.getInstance().get(versionName);
				JsonArray jarMods = btaPatch.getArray("jarMods");
				JsonObject btaJarMod = jarMods.get(0).getAsObject();
				String filename = btaJarMod.getString("MMC-filename");
				oldBtaJarFile = new File(jarmodsFolder, filename);
			}else {
				System.out.println("Could not find BTA in instance!");
				currentVersion = null;
				oldBtaJarFile = null;
			}
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
