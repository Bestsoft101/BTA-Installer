package b100.installer.gui;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import b100.installer.Config;
import b100.installer.Installer;
import b100.installer.VersionList;
import b100.installer.gui.utils.GridPanel;

public class InstallerGUI {
	
	public static InstallerGUI instance;
	
	public JFrame mainFrame;
	public GridPanel mainPanel;
	public JTabbedPane tabs;
	
	public VanillaLauncherInstallerGUI vanillaLauncherInstallerGUI;
	public BetaCraftInstallerGUI betaCraftInstallerGUI;
	
	public InstallerGUI() {
		if(instance != null) {
			throw new IllegalStateException("Instance already exists!");
		}
		instance = this;
		
		mainFrame = new JFrame("BTA Installer" + (Installer.isPortable() ? " (Portable Mode)" : "") + (Installer.isOffline() ? " (Offline Mode)" : ""));
		mainFrame.setMinimumSize(new Dimension(400, 320));
		
		mainPanel = new GridPanel();

		vanillaLauncherInstallerGUI = new VanillaLauncherInstallerGUI(this);
		betaCraftInstallerGUI = new BetaCraftInstallerGUI(this);
		
		tabs = new JTabbedPane();
		tabs.addTab("Vanilla Launcher", vanillaLauncherInstallerGUI);
		tabs.addTab("BetaCraft", betaCraftInstallerGUI);
		tabs.addTab("Log", new LogGUI());
		
		Config config = Config.getInstance();
		String lastInstallType = config.lastInstallType;
		if(lastInstallType != null) {
			if(lastInstallType.equalsIgnoreCase(VanillaLauncherInstallerGUI.INSTALL_TYPE)) tabs.setSelectedIndex(0);
			if(lastInstallType.equalsIgnoreCase(BetaCraftInstallerGUI.INSTALL_TYPE)) tabs.setSelectedIndex(1);	
		}
		
		mainPanel.add(tabs, 0, 1, 1, 1);
		
		mainFrame.add(mainPanel);
		mainFrame.pack();
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
	}
	
	public void showLog() {
		tabs.setSelectedIndex(2);
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch (Exception e) {
			e.printStackTrace();
		}

		if(!VersionList.validateVersion()) {
			JOptionPane.showMessageDialog(null, "Internal version list contains wrong version number! This is a bug!");
			return;
		}
		
		File file = Installer.getInstallerDirectory();
		boolean portable = Installer.isPortable();
		
		if(!file.exists() && !portable) {
			if(!portable) {
				int response = JOptionPane.showConfirmDialog(null, "Create config directory at '" + file.getAbsolutePath() + "'?", "Installer", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(response != 0) {
					JOptionPane.showMessageDialog(null, "Can't proceed without a configuration directory! If you want to run the installer in portable mode, add a file named 'portable' into the installer jar!");
					return;
				}
			}
			file.mkdirs();
		}
		
		Config.getInstance().load();
		
		new InstallerGUI();
	}
	
}
