package b100.installer;

import java.io.File;

import b100.installer.installer.ProgressListener;
import b100.json.JsonParser;
import b100.json.element.JsonObject;
import b100.utils.FileUtils;

public class DownloadHelper {
	
	private static File localResourcesFolder;
	
	private static File getLocalResourcesFolder() {
		if(localResourcesFolder == null) {
			localResourcesFolder = new File(Global.getInstallerDirectory(), "resources");
		}
		return localResourcesFolder;
	}
	
	/**
	 * Get the file from the cache if it exists, or download it
	 */
	public static File getFile(String path, ProgressListener progressListener) {
		if(Global.isOffline()) {
			File localFile = new File(getLocalResourcesFolder(), path);
			
			return localFile;
		}
		
		File downloadsFolder = new File(Global.getInstallerDirectory(), "downloads");
		File cachedFile = new File(downloadsFolder, path);
		
		if(!cachedFile.exists()) {
			new Download(getUrl(path)).setPrintProgress(true).setProgressListener(progressListener).downloadIntoFile(cachedFile);
		}
		
		return cachedFile;
	}
	
	/**
	 * Just download the file
	 */
	public static void downloadFile(String path, File target, ProgressListener progressListener) {
		if(Global.isOffline()) {
			File localFile = new File(getLocalResourcesFolder(), path);
			
			FileUtils.copy(localFile, target);
			return;
		}
		
		new Download(getUrl(path)).setPrintProgress(false).setProgressListener(progressListener).downloadIntoFile(target);
	}
	
	public static JsonObject getJson(String path) {
		if(Global.isOffline()) {
			File localFile = new File(getLocalResourcesFolder(), path);
			
			return JsonParser.instance.parseFileContent(localFile);
		}
		
		return new Download(getUrl(path)).setPrintProgress(false).downloadAsJson();
	}
	
	private static String getUrl(String path) {
		return Global.getDownloadUrl() + path.replace(" ", "%20");
	}
	
}
