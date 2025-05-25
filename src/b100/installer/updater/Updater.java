package b100.installer.updater;

import static b100.installer.util.Utils.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

import b100.installer.Download;
import b100.installer.Global;
import b100.json.element.JsonArray;
import b100.json.element.JsonObject;

public class Updater {

	private static final String COMPARE_STRING = "This file is read by the updater to ensure that no incorrect file gets deleted, do not delete or modify it.";

	public static void update(String[] args) {
		System.out.println("Update !!!");
		
		String javaExe = getEscapedPath(new File(System.getProperty("java.home"), "bin/java"));
		System.out.println("Java Path: " + javaExe);
		
		String jarUrl = Global.getDownloadUrl() + "bta-installer/installer.jar";
		File tempFile = new File("installer.jar.tmp");
		
		if(tempFile.exists()) {
			tempFile.delete();
		}else {
			File parent = tempFile.getAbsoluteFile().getParentFile();
			if(!parent.exists()) {
				parent.mkdirs();
			}
		}
		
		System.out.println("Downloading update...");
		new Download(jarUrl).downloadIntoFile(tempFile);
		
		File currentFile = getJarFileContainingClass(Updater.class);
		System.out.println("Current jar file: " + currentFile.getAbsolutePath());
		
		File oldFile = new File("installer.jar.old");
		copyFile(currentFile, oldFile);
		
		// Start another process that will delete the currently running jar file and put the updated file in its place.
		// This needs to be done in a different process, because it's not possible to delete or rename a jar file that's currently running.
		List<String> runArgs = new ArrayList<String>();
		runArgs.add(javaExe);
		runArgs.add("-cp");
		runArgs.add(getEscapedPath(oldFile));
		runArgs.add(RenameFileAndRun.class.getName());
		runArgs.add("--from");
		runArgs.add(getEscapedPath(tempFile));
		runArgs.add("--to");
		runArgs.add(getEscapedPath(currentFile));
		Process process = startProcess(runArgs);
		System.out.println("Started process: " + process);
		System.exit(0);
	}
	
	public static List<String> searchAvailableUpdates() {
		List<String> availableUpdates = new ArrayList<>();

		String currentVersion = readVersion();
		String versionsUrl = Global.getDownloadUrl() + "bta-installer/versions.json";
		JsonObject versions = new Download(versionsUrl).downloadAsJson();
		JsonArray versionsArray = versions.getArray("versions");
		
		for(int i=0; i < versionsArray.length(); i++) {
			String version = versionsArray.get(i).getAsString().value;
			if(currentVersion.equals(version)) {
				break;
			}
			availableUpdates.add(version);
		}
		
		return availableUpdates;
	}
	
	public static boolean isUpdaterJar(File file) {
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(file);
			
			return readLine(zipFile.getInputStream(zipFile.getEntry("updater.txt"))).equals(COMPARE_STRING);
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally {
			try {
				zipFile.close();
			}catch (Exception e) {}
		}
	}
	
	public static File getJarFileContainingClass(Class<?> clazz) {
		ClassLoader classLoader = clazz.getClassLoader();
		URL url = null;
		String classFileName = clazz.getName().replace('.', '/') + ".class";
		try {
			url = classLoader.getResource(classFileName);
		}catch (Exception e) {
			throw new RuntimeException("Finding class: \"" + classFileName + "\"", e);
		}
		if(url == null) {
			throw new NullPointerException("URL is null!");
		}
		
		File file = null;
		String urlString = url.toString();
		
		if(urlString.startsWith("jar:file:")) {
			int i = urlString.indexOf('!');
			if(i == -1) {
				throw new RuntimeException("Malformed URL: \"" + urlString + "\"!");
			}
			urlString = urlString.substring(4, i);
		}
		
		file = getFileFromURL(urlString);
		if(!isUpdaterJar(file)) {
			throw new RuntimeException("Wrong file: \"" + file.getAbsolutePath() + "\"!");
		}
		
		return file;
	}
	
}
