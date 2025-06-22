package b100.installer.util;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import b100.installer.Download;
import b100.installer.Global;
import b100.installer.Main;
import b100.installer.installer.ProgressListener;
import b100.utils.FileUtils;
import b100.utils.StreamUtils;
import b100.utils.StringUtils;

public abstract class Utils {
	
	public static final int OS_WINDOWS = 0;
	public static final int OS_MAC = 1;
	public static final int OS_LINUX = 2;
	public static final int OS_UNKNOWN = 3;
	public static final int OPERATING_SYSTEM;
	
	static {
		int os = OS_UNKNOWN;
		
		String osName = System.getProperty("os.name").toLowerCase();
		if(osName.contains("win")) os = OS_WINDOWS;
		if(osName.contains("mac")) os = OS_MAC;
		if(osName.contains("linux") || osName.contains("unix") || osName.contains("sunos") || osName.contains("solaris")) os = OS_LINUX;
		
		OPERATING_SYSTEM = os;
	}
	
	public static File getMinecraftDirectory() {
		return getAppDirectory("minecraft");
	}
	
	public static File getAppDirectory(String appName) {
		File appDir;
		String userHome = System.getProperty("user.home", ".");
		
		if(OPERATING_SYSTEM == OS_LINUX) {
			appDir = new File(userHome, "." + appName + "/");
		}else if(OPERATING_SYSTEM == OS_WINDOWS) {
			String appdata = System.getenv("APPDATA");
			if(appdata != null) {
				appDir = new File(appdata, "." + appName + "/");
			}else {
				appDir = new File(userHome, "." + appName + "/");
			}
		}else if(OPERATING_SYSTEM == OS_MAC) {
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
	
	public static void downloadFileAndPrintProgress(String url, File file) {
		new Download(url).setPrintProgress(true).downloadIntoFile(file);
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
	
	public static void copyFile(File from, File to, ProgressListener progressListener) {
		FileUtils.createFolderForFile(to);
		
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(from);
			out = new FileOutputStream(to);

			byte[] temp = new byte[1024];
			
			long copiedAmount = 0;
			long totalAmount = from.length();
			
			while(true) {
				int read = in.read(temp);
				if(read == -1) break;
				out.write(temp, 0, read);
				
				copiedAmount += read;
				if(progressListener != null) {
					float progress = (float) (copiedAmount / (double) totalAmount);
					progressListener.setProgress(progress);
				}
			}
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
	
	public static String combineStringsSeperatedWithSpaces(List<String> list) {
		if(list == null || list.size() == 0) {
			return "";
		}
		StringBuilder argsStr = new StringBuilder();
		for(int i=0; i < list.size(); i++) {
			if(i > 0) {
				argsStr.append(' ');
			}
			argsStr.append(list.get(i));
		}
		return argsStr.toString();
	}
	
	public static <E> int indexOf(E[] array, E obj) {
		for(int i=0; i < array.length; i++) {
			if(obj.equals(array[i])) {
				return i;
			}
		}
		return -1;
	}
	
	public static <E> int indexOf(List<E> list, E obj) {
		for(int i=0; i < list.size(); i++) {
			if(obj.equals(list.get(i))) {
				return i;
			}
		}
		return -1;
	}

	public static String getClipboardString() {
		try {
			Transferable trans = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
			if(trans != null && trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String s = (String) trans.getTransferData(DataFlavor.stringFlavor);
				return s;
			}
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void copyString(String text) {
		try {
			StringSelection selection = new StringSelection(text);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void extractFile(String internalPath, File output) {
		File parent = output.getAbsoluteFile().getParentFile();
		if(!parent.exists()) {
			parent.mkdirs();
		}
		InputStream in = null;
		OutputStream out = null;
		try {
			in = Utils.class.getResourceAsStream(internalPath);
			if(in == null) {
				throw new NullPointerException("Resource does not exist: \"" + internalPath + "\"!");
			}
			out = new FileOutputStream(output);
			StreamUtils.transferData(in, out);
		}catch (Exception e) {
			throw new RuntimeException("Could not extract file from \"" + internalPath + "\" to " + output.getAbsolutePath(), e);
		}finally {
			try {
				in.close();
			}catch (Exception e) {}
			try {
				out.close();
			}catch (Exception e) {}
		}
	}
	
	public static List<String> splitLines(String string) {
		List<String> lines = new ArrayList<>();
		
		int start = 0;
		int i = string.indexOf('\n', start);
		while(true) {
			if(i == -1) {
				lines.add(string.substring(start, string.length()));
				break;
			}
			
			lines.add(string.substring(start, i));
			start = i + 1;
			i = string.indexOf('\n', start);
		}
		
		return lines;
	}
	
	public static void createAndRunThread(String name, boolean daemon, Runnable runnable) {
		Thread thread = new Thread(runnable);
		if(name != null) {
			thread.setName(name);	
		}
		thread.setDaemon(daemon);
		thread.start();
	}
	
	public static String getEscapedPath(File file) {
		return "\"" + file.getAbsolutePath() + "\"";
	}
	
	public static void copyFile(File from, File to) {
		File parent = to.getAbsoluteFile().getParentFile();
		if(!parent.exists()) {
			parent.mkdirs();
		}
		
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(from);
			out = new FileOutputStream(to);
			
			copy(in, out);
		}catch (Exception e) {
			throw new RuntimeException("Copying file from \"" + from.getAbsolutePath() + "\" to \"" + to + "\"!", e);
		}finally {
			try {
				in.close();
			}catch (Exception e) {}
			try {
				out.close();
			}catch (Exception e) {}
		}
	}
	
	public static void copy(InputStream in, OutputStream out) throws IOException {
		byte[] cache = new byte[1024];
		while(true) {
			int read = in.read(cache);
			if(read < 0) {
				break;
			}
			out.write(cache, 0, read);
		}
	}
	
	public static Process startProcess(List<String> cmd) {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(cmd);
			processBuilder.inheritIO();
			return processBuilder.start();
		}catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	public static File getFileFromURL(String urlString) {
		try {
			URL url = new URL(urlString);
			URI uri = url.toURI();
			return new File(uri);
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String readVersion() {
		return readLine(Main.class.getResourceAsStream("/version.txt"));
	}
	
	public static List<String> getInstallerAndSystemInfo() {
		List<String> infos = new ArrayList<>();
		
		try {
			infos.add("Installer Version: " + Global.VERSION);
			infos.add("Installer Directory: '" + Global.getInstallerDirectory().getAbsolutePath() + "'");
			infos.add("Offline Mode: " + Global.isOffline());
			infos.add("Operating System: " + System.getProperty("os.name"));
			infos.add("Java Version: " + System.getProperty("java.version"));	
		}catch (Exception e) {
			String msg = "Could not get debug information!";
			infos.add(msg);
			System.err.println(msg);
			e.printStackTrace();
		}
		
		return infos;
	}
	
	public static String readLine(InputStream in) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(in));
			return br.readLine();
		}catch (Exception e) {
			throw new RuntimeException(e);
		}finally {
			try {
				in.close();
			}catch (Exception e) {}
			try {
				br.close();
			}catch (Exception e) {}
		}
	}
	
	public static void getDirFromArgs(String name, String[] args, int i, Consumer<File> consumer) {
		if(args.length <= i) {
			System.out.println("Didn't receive path after \"" + args[i - 1] + "\"!");
			return;
		}
		String arg = args[i];
		File file = new File(arg);
		if(file.isDirectory()) {
			System.out.println(name + " in args: " + file.getAbsolutePath());
			consumer.accept(file);
		}else {
			System.out.println("Received invalid " + name.toLowerCase() + " in args: \"" + arg + "\"");	
		}
	}
	
	////////////////////////////////
	// Math
	
	public static int floor(double d) {
		return (int) Math.floor(d);
	}
	
	public static int ceil(double d) {
		return (int) Math.ceil(d);
	}
	
	public static int ceilDiv(int a, int b) {
		return (int) Math.ceil(a / (double) b);
	}
	
	public static int clampi(int val, int min, int max) {
		if(val < min) return min;
		if(val > max) return max;
		return val;
	}
	
	public static double clampd(double val, double min, double max) {
		if(val < min) return min;
		if(val > max) return max;
		return val;
	}
	
	public static long clampl(long val, long min, long max) {
		if(val < min) return min;
		if(val > max) return max;
		return val;
	}
	
	public static int multiplyRGB(int color, double mul) {
		int a = (color >> 24) & 0xFF;
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color >> 0) & 0xFF;

		r = (int) (r * mul);
		g = (int) (g * mul);
		b = (int) (b * mul);

		a = clampi(a, 0, 255);
		r = clampi(r, 0, 255);
		g = clampi(g, 0, 255);
		b = clampi(b, 0, 255);
		
		return (a << 24) | (r << 16) | (g << 8) | b;
	}
	
	public static int mixARGB(int color1, int color2, float factor) {
		int a1 = (color1 >> 24) & 0xFF;
		int r1 = (color1 >> 16) & 0xFF;
		int g1 = (color1 >>  8) & 0xFF;
		int b1 = (color1 >>  0) & 0xFF;

		int a2 = (color2 >> 24) & 0xFF;
		int r2 = (color2 >> 16) & 0xFF;
		int g2 = (color2 >>  8) & 0xFF;
		int b2 = (color2 >>  0) & 0xFF;
		
		int a = mix(a1, a2, factor);
		int r = mix(r1, r2, factor);
		int g = mix(g1, g2, factor);
		int b = mix(b1, b2, factor);
		
		return a << 24 | r << 16 | g << 8 | b;
	}
	
	public static int mix(int a, int b, float factor) {
		return (int) (a * (1.0f - factor) + b * factor);
	}

	////////////////////////////////

}
