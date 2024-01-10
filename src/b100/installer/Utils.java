package b100.installer;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import b100.installer.gui.utils.ImagePanel;
import b100.utils.FileUtils;
import b100.utils.StreamUtils;
import b100.utils.StringUtils;

public class Utils {
	
	public static final int OS_WINDOWS = 0;
	public static final int OS_MAC = 1;
	public static final int OS_LINUX = 2;
	public static final int OS_UNKNOWN = 3;
	
	public static File getMinecraftDirectory() {
		return getAppDirectory("minecraft");
	}
	
	public static File getAppDirectory(String appName) {
		int operatingSystem = OS_UNKNOWN;
		
		String osName = System.getProperty("os.name").toLowerCase();
		if(osName.contains("win")) operatingSystem = OS_WINDOWS;
		if(osName.contains("mac")) operatingSystem = OS_MAC;
		if(osName.contains("linux") || osName.contains("unix") || osName.contains("sunos") || osName.contains("solaris")) operatingSystem = OS_LINUX;
		
		File appDir;
		String userHome = System.getProperty("user.home", ".");
		
		if(operatingSystem == OS_LINUX) {
			appDir = new File(userHome, "." + appName + "/");
		}else if(operatingSystem == OS_WINDOWS) {
			String appdata = System.getenv("APPDATA");
			if(appdata != null) {
				appDir = new File(appdata, "." + appName + "/");
			}else {
				appDir = new File(userHome, "." + appName + "/");
			}
		}else if(operatingSystem == OS_MAC) {
			appDir = new File(userHome, "Library/Application Support/" + appName + "/");
		}else {
			appDir = new File(userHome, appName + "/");
		}
		
		return appDir;
	}
	
	public static void createNewFile(File file) {
		if(file.exists()) {
			file.delete();
		}else {
			File parent = file.getAbsoluteFile().getParentFile();
			if(!parent.exists()) {
				parent.mkdirs();
			}	
		}
		try{
			file.createNewFile();
		}catch (Exception e) {
			throw new RuntimeException("Could not create file: '" + file.getAbsolutePath() + "'!", e);
		}
	}
	
	public static void createModdedMinecraftJar(File minecraftJar, File modJar, File output) {
		createNewFile(output);
		
		List<Closeable> closeables = new ArrayList<>();
		
		ZipOutputStream out = null;
		
		List<File> files = new ArrayList<>();
		files.add(minecraftJar);
		files.add(modJar);
		
		try {
			out = new ZipOutputStream(new FileOutputStream(output));
			closeables.add(out);
			
			Set<String> addedEntries = new HashSet<>();
			
			for(int i = files.size() - 1; i >= 0; i--) {
				File file = files.get(i);
				ZipFile zip = new ZipFile(file);
				closeables.add(zip);
				
				Enumeration<? extends ZipEntry> entries = zip.entries();
				
				while(entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();
					String entryName = entry.getName();
					
					if(i == 0 && entryName.startsWith("META-INF")) {
						continue;
					}
					
					if(addedEntries.contains(entryName)) {
						continue;
					}
					
//					System.out.println("Copy: " + entryName);
					
					byte[] allBytes = readAll(zip.getInputStream(entry));
					out.putNextEntry(new ZipEntry(entryName));
					out.write(allBytes);
					addedEntries.add(entryName);
				}
			}
		}catch (Exception e) {
			throw new RuntimeException(e);
		}finally {
			for(int i=0; i < closeables.size(); i++) {
				try {
					closeables.get(i).close();
				}catch (Exception e) {}
			}
		}
	}
	
	public static void downloadFile(String url, File file) {
		System.out.println("Downloading: '" + url + "'");
		try {
			FileUtils.downloadFile(url, file);
		}catch (IOException e) {
			throw new RuntimeException(e);
		}
		System.out.println("Done!");
	}
	
	public static byte[] readAll(InputStream inputStream) throws IOException {
		final int cacheSize = 4096;
		
		ByteCache byteCache = new ByteCache();
		while(true) {
			byte[] cache = new byte[cacheSize];
			int read = inputStream.read(cache, 0, cache.length);
			if(read == -1) {
				break;
			}
			byteCache.put(cache, 0, read);
		}
		
		try {
			inputStream.close();
		}catch (Exception e) {}
		
		return byteCache.getAll();
	}
	
	static class ByteCache {
		
		private List<CacheEntry> allBuffers = new ArrayList<>();
		
		public void put(byte[] bytes, int offset, int length) {
			CacheEntry cacheEntry = new CacheEntry();
			cacheEntry.bytes = bytes;
			cacheEntry.length = length;
			cacheEntry.offset = offset;
			allBuffers.add(cacheEntry);
		}
		
		public byte[] getAll() {
			int totalSize = 0;
			int buffers = allBuffers.size();
			
			for(int i=0; i < buffers; i++) {
				totalSize += allBuffers.get(i).length;
			}
			
			byte[] allBytes = new byte[totalSize];
			int offset = 0;
			
			for(int i=0; i < buffers; i++) {
				CacheEntry cacheEntry = allBuffers.get(i);
				
				byte[] bytes = cacheEntry.bytes;
				for(int j=0; j < cacheEntry.length; j++) {
					allBytes[offset + j] = bytes[cacheEntry.offset + j];
				}
				offset += cacheEntry.length;
			}
			
			return allBytes;
		}
		
		static class CacheEntry {
			
			public byte[] bytes;
			public int offset;
			public int length;
			
		}

	}
	
	public static String[] toArray(List<String> list) {
		String[] array = new String[list.size()];
		for(int i=0; i < array.length; i++) {
			array[i] = list.get(i);
		}
		return array;
	}
	
	public static BufferedImage readImage(String path) {
		InputStream stream = null;
		try {
			return ImageIO.read(stream = Utils.class.getResourceAsStream(path));
		}catch (Exception e) {
			throw new RuntimeException("Reading image: '" + path + "'!");
		}finally {
			try {
				stream.close();
			}catch (Exception e) {}
		}
	}
	
	public static ImagePanel createImagePanel(String path) {
		BufferedImage image = readImage(path);
		if(image == null) {
			throw new NullPointerException("Image is null!");
		}
		return new ImagePanel(image);
	}
	
	public static void copyFile(File from, File to) {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(from);
			out = new FileOutputStream(to);
			
			StreamUtils.transferData(in, out);
		}catch (Exception e) {
			throw new RuntimeException("Copying file from '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "'!", e);
		}finally {
			try {
				in.close();
			}catch (Exception e) {}
			try {
				out.close();
			}catch (Exception e) {}
		}
	}
	
	public static Map<String, String> loadProperties(File file) {
		Map<String, String> properties = new HashMap<>();
		String[] lines = StringUtils.getFileContentAsString(file).split("\n");
		
		for(int i=0; i < lines.length; i++) {
			String line = lines[i];
			int j = line.indexOf(':');
			if(j == -1) {
				continue;
			}
			
			properties.put(line.substring(0, j), line.substring(j + 1));
		}
		return properties;
	}
	
	public static void saveProperties(File file, Map<String, String> properties) {
		List<String> keys = new ArrayList<>(properties.keySet());
		keys.sort(String.CASE_INSENSITIVE_ORDER);
		
		StringBuilder string = new StringBuilder();
		
		for(int i=0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = properties.get(key);
			
			if(i > 0) {
				string.append('\n');
			}
			string.append(key).append(':').append(value);
		}
		
		StringUtils.saveStringToFile(file, string.toString());
	}

}
