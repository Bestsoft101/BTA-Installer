package b100.installer;

import java.io.File;

import b100.json.element.JsonObject;

public class DownloadManager {
	
	private static File downloadDirectory = new File(Installer.getInstallerDirectory(), "downloads");
	
	static {
		if(!downloadDirectory.exists()) {
			downloadDirectory.mkdirs();
		}
	}
	
	public static String getDownloadUrl(String filename) {
		JsonObject downloads = VersionList.getVersions().getObject("downloads");
		
		return downloads.getString(filename);
	}
	
	public static File getFile(String filename) {
		File file = new File(downloadDirectory, filename);
		
		if(!file.exists()) {
			String url = getDownloadUrl(filename);
			
			Utils.downloadFile(url, file);
		}
		return file;
	}

}
