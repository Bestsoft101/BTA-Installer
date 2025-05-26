package b100.installer;

import java.util.List;

import javax.swing.UIManager;

import b100.installer.gui.modern.InstallerGuiModern;
import b100.installer.updater.UpdateInfoWindow;
import b100.installer.updater.Updater;

public class Main {
	
	public static void main(String[] args) {
		boolean noUpdate = false;
		for(String arg : args) {
			if(arg.equals("--noUpdate")) {
				noUpdate = true;
			}
		}
		
		Global.setup(args);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		if(noUpdate) {
			System.out.println("Update check disabled through launch parameters!");
			launchInstaller(args);
			return;
		}
		
		List<String> availableUpdates = Updater.searchAvailableUpdates();
		if(availableUpdates == null) {
			System.out.println("Update check failed!");
			launchInstaller(args);
			return;
		}
		if(availableUpdates.size() == 0) {
			System.out.println("No updates available!");
			launchInstaller(args);
			return;
		}
		
		System.out.println("Available updates: ");
		for(String version : availableUpdates) {
			System.out.println("  " + version);
		}
		
		Runnable onDontUpdate = () -> launchInstaller(args);
		Runnable onUpdate = () -> Updater.update(args);
		
		new UpdateInfoWindow(availableUpdates, onUpdate, onDontUpdate);
	}
	
	public static void launchInstaller(String[] args) {
		InstallerGuiModern.main(args);
	}
}
