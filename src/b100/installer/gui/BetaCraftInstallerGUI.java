package b100.installer.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import b100.installer.Config;
import b100.installer.DownloadManager;
import b100.installer.Utils;
import b100.installer.VersionList;
import b100.installer.gui.utils.GridPanel;
import b100.json.element.JsonObject;
import b100.utils.StringUtils;

@SuppressWarnings("serial")
public class BetaCraftInstallerGUI extends GridPanel implements ActionListener, Runnable {

	public static final String INSTALL_TYPE = "betacraft";
	
	public final InstallerGUI installerGUI;
	
	public JComboBox<String> versionMenu;
	public JButton startButton;
	public JTextField betacraftDirectoryTextfield;
	public JTextField instanceTextfield;
	
	public BetaCraftInstallerGUI(InstallerGUI installerGUI) {
		this.installerGUI = installerGUI;
		
		int inset = 4;
		getGridBagConstraints().insets.set(inset, inset, inset, inset);
		
		betacraftDirectoryTextfield = new JTextField();
		betacraftDirectoryTextfield.setText(getBetacraftDirectory().getAbsolutePath());
		
		instanceTextfield = new JTextField();
		instanceTextfield.setText("Better Than Adventure!");

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
		add(createTitledPanel(betacraftDirectoryTextfield, "BetaCraft Directory"), 0, 1, 0, 0, 2, 1);
		add(createTitledPanel(instanceTextfield, "Instance"), 0, 2, 0, 0, 2, 1);
		add(versionMenu, 0, 3, 1, 0);
		add(startButton, 1, 3, 0, 0);
	}
	
	public GridPanel createTitledPanel(Component component, String title) {
		GridPanel panel = new GridPanel();
		panel.setBorder(new TitledBorder(title));
		panel.getGridBagConstraints().insets.set(4, 4, 4, 4);
		panel.add(component, 0, 0, 1, 1);
		return panel;
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
		
		File betacraftDirectory = new File(betacraftDirectoryTextfield.getText());
		if(!betacraftDirectory.exists() || !betacraftDirectory.isDirectory()) {
			JOptionPane.showMessageDialog(this, "Invalid BetaCraft Directory: '" + betacraftDirectory.getAbsolutePath() + "'!");
			return false;
		}
		
		JsonObject betaCraftObject = versionObject.getObject("betacraft");
		if(betaCraftObject == null) {
			JOptionPane.showMessageDialog(this, "The selected version is not compatible with BetaCraft!");
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
		
		Utils.saveProperties(instanceFile, properties);
		
		JOptionPane.showMessageDialog(this, "Done!");
		return true;
	}
	
	public static File getBetacraftDirectory() {
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
