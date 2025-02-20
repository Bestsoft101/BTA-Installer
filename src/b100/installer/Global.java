package b100.installer;

import java.io.File;
import java.io.InputStream;

public class Global {
	
	public static final String MULTIMC_INSTANCE_FOLDER_NAME = "BTA_MANAGED_INSTANCE";
	
	private static File installerDirectory;
	private static boolean offline;
	private static String downloadUrl = "https://downloads.betterthanadventure.net/";
	
	public static boolean setup(String[] args) {
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
		if(installerDirectory == null) {
			installerDirectory = Utils.getAppDirectory("bta-installer");
		}
		
		System.out.println("Installer Directory: '" + installerDirectory.getAbsolutePath() + "'");
		System.out.println("Offline Mode: " + offline);
		
		Config.getInstance().load();
		return true;
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

}
