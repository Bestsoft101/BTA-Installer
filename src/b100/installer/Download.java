package b100.installer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import b100.installer.installer.ProgressListener;
import b100.json.JsonParser;
import b100.json.element.JsonObject;
import b100.utils.FileUtils;

/**
 * Helper class for downloading files
 */
public class Download {
	
	public final String url;

	/** Should the download progress be printed into the log? */
	private boolean printProgress = true;
	
	private ProgressListener progressListener;
	
	public Download(String url) {
		this.url = url;
	}

	//////////////////////////////////////////////
	
	public Download setPrintProgress(boolean printProgress) {
		this.printProgress = printProgress;
		return this;
	}
	
	public Download setProgressListener(ProgressListener progressListener) {
		this.progressListener = progressListener;
		return this;
	}
	
	//////////////////////////////////////////////
	
	/** Return the page content as String */
	public String downloadAsString() {
		StringBuilder str = new StringBuilder();
		
		OutputStream out = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				str.append((char) b);
			}
		};
		
		download(out);
		
		return str.toString();
	}
	
	/** Return the page content as json object */
	public JsonObject downloadAsJson() {
		return JsonParser.instance.parseString(downloadAsString());
	}
	
	/** Download the page content into the given file */
	public void downloadIntoFile(File file) {
		FileUtils.createFolderForFile(file);
		
		try {
			download(new FileOutputStream(file));
		}catch (Exception e) {
			throw new RuntimeException("Downloading file: '" + file.getAbsolutePath() + "' from '" + url + "'!", e);
		}
	}

	//////////////////////////////////////////////
	
	private void download(OutputStream out) {
		HttpURLConnection connection = null;
		BufferedInputStream bin = null;
		BufferedOutputStream bout = null;
		
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();

			System.out.println("Downloading: " + connection.getURL());

			long completeFileSize = connection.getContentLengthLong();
			long downloadedFileSize = 0;
			long lastPrint = 0;

			bin = new BufferedInputStream(connection.getInputStream());
			bout = new BufferedOutputStream(out);
			
			byte[] cache = new byte[4096];
			
			while(true) {
				int read = bin.read(cache, 0, cache.length);
				if(read == -1) {
					break;
				}
				downloadedFileSize += read;
				bout.write(cache, 0, read);
				
				float progress = (float) (downloadedFileSize / (double) completeFileSize);
				if(progressListener != null) {
					progressListener.setProgress(progress);
				}
				
				if(printProgress) {
					long now = System.currentTimeMillis();
					if(now > lastPrint + 500) {
						lastPrint = now;
						
						int percent = (int) (progress * 100);
						System.out.println("Downloading: " + percent + "%");
					}
				}
			}
			
			if(printProgress) {
				System.out.println("Finished Downloading!");	
			}
		}catch (Exception e) {
			throw new RuntimeException("Downloading '" + url + "'!", e);
		}finally {
			try {
				connection.disconnect();
			}catch (Exception e) {}
			try {
				bin.close();
			}catch (Exception e) {}
			try {
				bout.close();
			}catch (Exception e) {}
			try {
				out.close();
			}catch (Exception e) {}
		}
	}
	
}
