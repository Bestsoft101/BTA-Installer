package b100.installer;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import b100.installer.util.Log;
import b100.installer.util.MultiMCHelper;
import b100.installer.util.Utils;

public class Global {
	
	public static final String VERSION = Utils.readVersion();
	public static final String MULTIMC_INSTANCE_FOLDER_NAME = "BTA_MANAGED_INSTANCE";
	
	private static File installerDirectory;
	private static boolean offline;
	private static String downloadUrl = "https://downloads.betterthanadventure.net/";
	private static File logFile;
	
	private static boolean initialized = false;
	
	public static void setup(String[] args) {
		if(initialized) {
			return;
		}
		initialized = true;

		MultiMCHelper.init(args);
		
		if(args != null) {
			for(int i=0; i < args.length; i++) {
				String arg = args[i];
				
				if(arg.equals("--run-directory")) {
					installerDirectory = new File(args[++i]);
				}else if(arg.equals("--offline")) {
					offline = true;
				}else if(arg.equals("--download-url")) {
					downloadUrl = args[++i];
				}
			}
		}
		
		if(!offline) {
			offline = checkFileExists("offline");	
		}
		File runInstance = MultiMCHelper.getRunInstanceDirectory();
		if(runInstance != null && installerDirectory == null) {
			installerDirectory = new File(runInstance, ".bta-installer");
		}
		if(installerDirectory == null) {
			installerDirectory = Utils.getAppDirectory("bta-installer");
		}
		
		logFile = new File(installerDirectory, "installer.log");
		Log.setup(logFile);
		Log.enable();
		
		List<String> infos = Utils.getInstallerAndSystemInfo();
		for(String info : infos) {
			System.out.println(info);
		}
		
		Config.getInstance().load();
	}
	
	private static boolean checkFileExists(String name) {
		InputStream stream = null;
		try {
			stream = Global.class.getResourceAsStream("/" + name);
			if(stream != null) {
				return true;
			}
		}catch (Exception e) {
		}finally {
			try {
				stream.close();
			}catch (Exception e) {}
		}
		
		try {
			stream = Global.class.getResourceAsStream("/" + name + ".txt");
			if(stream != null) {
				return true;
			}
		}catch (Exception e) {
		}finally {
			try {
				stream.close();
			}catch (Exception e) {}
		}
		
		return false;
	}
	
	public static boolean isOffline() {
		return offline;
	}
	
	public static File getInstallerDirectory() {
		return installerDirectory;
	}
	
	public static String getDownloadUrl() {
		return downloadUrl;
	}
	
	public static File getLogFile() {
		return logFile;
	}

}
