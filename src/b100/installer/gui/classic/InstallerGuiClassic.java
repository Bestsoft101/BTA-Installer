package b100.installer.gui.classic;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import b100.installer.Config;
import b100.installer.Global;

public class InstallerGuiClassic {
	
	public static InstallerGuiClassic instance;
	
	public JFrame mainFrame;
	public GridPanel mainPanel;
	public JTabbedPane tabs;
	
	public VanillaLauncherInstallerGUI vanillaLauncherInstallerGUI;
	public BetaCraftInstallerGUI betaCraftInstallerGUI;
	public MultiMcInstallerGUI multiMCInstallerGUI;
	
	public InstallerGuiClassic() {
		if(instance != null) {
			throw new IllegalStateException("Instance already exists!");
		}
		instance = this;
		
		mainFrame = new JFrame("BTA Installer" + (Global.isOffline() ? " (Offline Mode)" : ""));
		mainFrame.setMinimumSize(new Dimension(400, 320));
		
		mainPanel = new GridPanel();

		vanillaLauncherInstallerGUI = new VanillaLauncherInstallerGUI(this);
		betaCraftInstallerGUI = new BetaCraftInstallerGUI(this);
		multiMCInstallerGUI = new MultiMcInstallerGUI(this);
		
		tabs = new JTabbedPane();
		tabs.addTab("Vanilla Launcher", vanillaLauncherInstallerGUI);
		tabs.addTab("BetaCraft", betaCraftInstallerGUI);
		tabs.addTab("MultiMC / Prism Launcher", multiMCInstallerGUI);
		tabs.addTab("Log", new LogGUI());
		
		Config config = Config.getInstance();
		String lastInstallType = config.lastInstallType.value;
		if(lastInstallType != null) {
			if(lastInstallType.equalsIgnoreCase(VanillaLauncherInstallerGUI.INSTALL_TYPE)) tabs.setSelectedIndex(0);
			if(lastInstallType.equalsIgnoreCase(BetaCraftInstallerGUI.INSTALL_TYPE)) tabs.setSelectedIndex(1);
			if(lastInstallType.equalsIgnoreCase(MultiMcInstallerGUI.INSTALL_TYPE)) tabs.setSelectedIndex(2);
		}
		
		mainPanel.add(tabs, 0, 1, 1, 1);
		
		mainFrame.add(mainPanel);
		mainFrame.pack();
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
	}
	
	public void showLog() {
		tabs.setSelectedIndex(3);
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		if(Global.setup(args)) {
			new InstallerGuiClassic();
		}
	}
}
