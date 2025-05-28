package b100.installer.updater;

import static b100.installer.util.Utils.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

import b100.installer.Download;
import b100.installer.Global;
import b100.installer.util.MultiMCHelper;
import b100.json.JsonParser;
import b100.json.element.JsonArray;
import b100.json.element.JsonObject;

public class Updater {

	private static final String COMPARE_STRING = "This file is read by the updater to ensure that no incorrect file gets deleted, do not delete or modify it.";

	public static void update(String[] args) {
		System.out.println("Update !!!");
		
		File runInstance = MultiMCHelper.getRunInstanceDirectory();
		System.out.println("Run Instance: " + runInstance);
		
		// When installing the updater as a jarmod in MultiMC, we have to replace the jarmod file,
		// not the one that's currently running, because MultiMC creates a temporary file
		// when the instance is started
		if(runInstance != null) {
			File updaterJar = findUpdaterJarInInstance(runInstance);
			if(updaterJar != null) {
				installUpdate(updaterJar, false);
				return;
			}
		}
		
		installUpdate(getJarFileContainingClass(Updater.class), true);
	}
	
	private static void downloadUpdate(File file) {
		String jarUrl = Global.getDownloadUrl() + "bta-installer/installer.jar";
		
		if(file.exists()) {
			file.delete();
		}else {
			File parent = file.getAbsoluteFile().getParentFile();
			if(!parent.exists()) {
				parent.mkdirs();
			}
		}
		
		System.out.println("Downloading update...");
		new Download(jarUrl).downloadIntoFile(file);
	}
	
	private static File findUpdaterJarInInstance(File instance) {
		System.out.println("Install update in instance: " + instance.getAbsolutePath());
		
		File mmcPackFile = new File(instance, "mmc-pack.json");
		File jarmodsFolder = new File(instance, "jarmods");
		
		JsonObject mmcPack = JsonParser.instance.parseFileContent(mmcPackFile);
		JsonArray components = mmcPack.getArray("components");
		
		for(int i=0; i < components.length(); i++) {
			String uid = components.get(i).getAsObject().getString("uid");
			if(uid.startsWith("org.multimc.jarmod.")) {
				String jarmodId = uid.substring(19);
				
				System.out.println("Found jar mod with id: " + jarmodId);
				
				File jarmodFile = new File(jarmodsFolder, jarmodId + ".jar");
				if(isUpdaterJar(jarmodFile)) {
					return jarmodFile;
				}
			}
		}
		return null;
	}
	
	private static void installUpdate(File currentFile, boolean startProcess) {
		File updateFile = new File("installer.jar.tmp");
		File backupFile = new File("installer.jar.old");
		
		System.out.println("Current jar file: " + currentFile.getAbsolutePath());
		System.out.println("Downloading update: " + updateFile.getAbsolutePath());
		downloadUpdate(updateFile);
		
		if(startProcess) {
			copyFile(currentFile, backupFile);
			
			// Start another process that will delete the currently running jar file and put the updated file in its place.
			// This needs to be done in a different process, because it's not possible to delete or rename a jar file that's currently running.
			
			String javaExe = getEscapedPath(new File(System.getProperty("java.home"), "bin/java"));
			System.out.println("Java Path: " + javaExe);
			
			List<String> runArgs = new ArrayList<String>();
			runArgs.add(javaExe);
			runArgs.add("-cp");
			runArgs.add(getEscapedPath(backupFile));
			runArgs.add(RenameFileAndRun.class.getName());
			runArgs.add("--from");
			runArgs.add(getEscapedPath(updateFile));
			runArgs.add("--to");
			runArgs.add(getEscapedPath(currentFile));
			
			Process process = startProcess(runArgs);
			System.out.println("Started process: " + process);
			System.exit(0);
			return;
		}
		
		boolean success = true;
		if(backupFile.exists() && !backupFile.delete()) {
			System.out.println("Could not delete file: " + backupFile.getAbsolutePath());
			success = false;
		}
		if(!currentFile.renameTo(backupFile)) {
			System.out.println("Could not rename current file: " + currentFile.getAbsolutePath());
			success = false;
		}
		if(!updateFile.renameTo(currentFile)) {
			System.out.println("Could not rename update file: " + updateFile.getAbsolutePath());
			success = false;
		}
		
		if(success) {
			System.out.println("Update complete!");	
		}else {
			System.out.println("Update failed!");
		}
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
