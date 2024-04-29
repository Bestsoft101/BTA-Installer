package b100.installer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
			
			downloadFileAndPrintProgress(url, file);
		}
		return file;
	}
	
	public static void downloadFileAndPrintProgress(String url, File file) {
		HttpURLConnection connection = null;
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			
			System.out.println("Downloading: " + connection.getURL());
			
			long completeFileSize = connection.getContentLengthLong();
			long downloadedFileSize = 0;
			long lastPrint = 0;
			
			in = new BufferedInputStream(connection.getInputStream());
			out = new BufferedOutputStream(new FileOutputStream(file));
			
			byte[] cache = new byte[4096];
			
			while(true) {
				int read = in.read(cache, 0, cache.length);
				if(read == -1) {
					break;
				}
				downloadedFileSize += read;
				out.write(cache, 0, read);
				
				long now = System.currentTimeMillis();
				if(now > lastPrint + 500) {
					lastPrint = now;
					
					int percent = (int) ((downloadedFileSize / (double) completeFileSize) * 100);
					System.out.println("Downloading: " + percent + "%");
				}
			}
			
			System.out.println("Finished Downloading!");
		}catch (Exception e) {
			throw new RuntimeException("Error downloading file from '" + url + "' to '" + file.getAbsolutePath() + "'!", e);
		}finally {
			try {
				connection.disconnect();
			}catch (Exception e) {}
			try {
				in.close();
			}catch (Exception e) {}
			try {
				out.close();
			}catch (Exception e) {}
		}
	}

}
