package b100.installer;

import java.io.File;

import b100.json.JsonParser;
import b100.json.element.JsonObject;
import b100.utils.FileUtils;

public class DownloadHelper {
	
	/**
	 * Get the file from the cache if it exists, or download it
	 */
	public static File getFile(String path) {
		if(Global.isOffline()) {
			File downloadsFolder = new File("downloads");
			File localFile = new File(downloadsFolder, path);
			
			return localFile;
		}
		
		File downloadsFolder = new File(Global.getInstallerDirectory(), "downloads");
		File cachedFile = new File(downloadsFolder, path);
		
		if(!cachedFile.exists()) {
			new Download(getUrl(path)).setPrintProgress(true).downloadIntoFile(cachedFile);
		}
		
		return cachedFile;
	}
	
	/**
	 * Just download the file
	 */
	public static void downloadFile(String path, File target) {
		if(Global.isOffline()) {
			File downloadsFolder = new File("downloads");
			File localFile = new File(downloadsFolder, path);
			
			FileUtils.copy(localFile, target);
			return;
		}
		
		new Download(getUrl(path)).setPrintProgress(false).downloadIntoFile(target);
	}
	
	public static JsonObject getJson(String path) {
		if(Global.isOffline()) {
			File downloadsFolder = new File("downloads");
			File localFile = new File(downloadsFolder, path);
			
			return JsonParser.instance.parseFileContent(localFile);
		}
		
		return new Download(getUrl(path)).setPrintProgress(false).downloadAsJson();
	}
	
	private static String getUrl(String path) {
		return Global.getDownloadUrl() + path.replace(" ", "%20");
	}
	
}
