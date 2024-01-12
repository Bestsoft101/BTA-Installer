package b100.installer;

import java.io.File;
import java.io.InputStream;

public class Installer {
	
	private static File installerDirectory;
	private static boolean portable;
	
	static {
		InputStream stream = null;
		try {
			stream = Installer.class.getResourceAsStream("/portable");
			
			portable = stream != null;
		}catch (Exception e) {
			portable = false;
		}finally {
			try {
				stream.close();
			}catch (Exception e) {}
		}
		
		if(portable) {
			installerDirectory = new File(".").getAbsoluteFile();
		}else {
			installerDirectory = Utils.getAppDirectory("bta-installer");
		}
		
		System.out.println("Installer Directory: '" + installerDirectory.getAbsolutePath() + "'");
		System.out.println("Portable Mode: " + portable);
	}
	
	public static boolean isPortable() {
		return portable;
	}
	
	public static File getInstallerDirectory() {
		return installerDirectory;
	}

}
