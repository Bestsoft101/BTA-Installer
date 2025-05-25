package b100.installer;

import static b100.installer.util.Utils.*;

import java.util.List;

import javax.swing.UIManager;

import b100.installer.updater.UpdateInfoWindow;
import b100.installer.updater.Updater;

public class Main {
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch (Exception e) {
			e.printStackTrace();
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
		String installerMainClassName = getManifestAttribute("Installer-Main-Class").trim();
		if(installerMainClassName == null) {
			throw new RuntimeException("Missing installer main class in manifest!");
		}
		invokeMain(Main.class.getClassLoader(), installerMainClassName, args);
	}
}
