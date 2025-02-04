package b100.installer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import b100.installer.gui.utils.GridPanel;
import b100.installer.gui.utils.VersionComponent;

@SuppressWarnings("serial")
public abstract class BaseInstallerGUI extends GridPanel implements ActionListener, Runnable {

	public final InstallerGUI installerGUI;

	public VersionComponent versionComponent;
	public JButton installButton;
	
	public BaseInstallerGUI(InstallerGUI installerGUI) {
		this.installerGUI = installerGUI;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == installButton) {
			new Thread(this).start();
		}
	}
	
	@Override
	public void run() {
		installButton.setEnabled(false);
		installerGUI.showLog();
		
		try {
			install();
		}catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Failure!");
			
			e.printStackTrace();
		}
		
		installButton.setEnabled(true);
	}
	
	public abstract boolean install();
	
}
