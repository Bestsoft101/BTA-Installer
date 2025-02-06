package b100.installer;

import java.io.File;
import java.io.InputStream;

public class Global {
	
	private static File installerDirectory;
	private static boolean portable;
	private static boolean offline;
	
	static {
		portable = checkFileExists("portable");
		offline = checkFileExists("offline");
		
		if(portable) {
			installerDirectory = new File("").getAbsoluteFile();
		}else {
			installerDirectory = Utils.getAppDirectory("bta-installer");
		}
		
		System.out.println("Installer Directory: '" + installerDirectory.getAbsolutePath() + "'");
		System.out.println("Portable Mode: " + portable);
		System.out.println("Offline Mode: " + offline);
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
	
	public static boolean isPortable() {
		return portable;
	}
	
	public static boolean isOffline() {
		return offline;
	}
	
	public static File getInstallerDirectory() {
		return installerDirectory;
	}

}
