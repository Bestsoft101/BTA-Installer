package b100.installer.gui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import b100.installer.gui.utils.GridPanel;

public class InstallerGUI {
	
	public JFrame mainFrame;
	public GridPanel mainPanel;
	public JTabbedPane tabs;
	
	public VanillaLauncherInstallerGUI vanillaLauncherInstallerGUI;
	
	public InstallerGUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		mainFrame = new JFrame("Installer");
		mainFrame.setMinimumSize(new Dimension(100, 100));
		
		mainPanel = new GridPanel();
		
		tabs = new JTabbedPane();
		tabs.addTab("Vanilla Launcher", new VanillaLauncherInstallerGUI(this));
		tabs.addTab("BetaCraft", new BetaCraftInstallerGUI(this));
		tabs.addTab("Log", new LogGUI());
		
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
		new InstallerGUI();
	}
	
}
