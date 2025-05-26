package b100.installer.util;

import static b100.installer.util.Utils.*;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import javax.imageio.ImageIO;

import b100.installer.Download;
import b100.installer.Global;
import b100.installer.config.ConfigUtil;
import b100.utils.StringUtils;

/**
 * Also compatible with Prism Launcher, but I don't feel like writing both names everywhere
 */
public class MultiMCHelper {
	
	/**
	 * The "assets" folder inside the MultiMC folder. Only used to find the actual MultiMC folder
	 */
	private static File assetsDirectory;
	
	/**
	 * The actual MultiMC folder, determined by launch parameters and where the installer is started from
	 */
	private static File launcherDirectory;
	
	/**
	 * If the installer is started through MultiMC, this is the folder of the instance it's started from
	 */
	private static File runInstanceDirectory;
	
	private static File instancesDirectory;
	private static File iconsDirectory;
	
	private static boolean initialized = false;
	
	public static void init(String[] args) {
		if(initialized) {
			return;
		}
		initialized = true;
		
		if(args != null) {
			processArgs(args);
		}
		
		if(runInstanceDirectory == null) {
			tryGetInstanceDirectoryFromRunDirectory();	
		}
		if(launcherDirectory == null && assetsDirectory != null) {
			System.out.println("No launcher directory provided, using directory above assets");
			launcherDirectory = assetsDirectory.getParentFile();
		}
		if(launcherDirectory == null && runInstanceDirectory != null) {
			File file = getLauncherDirectoryFromInstanceDirectory(runInstanceDirectory);
			if(file != null) {
				launcherDirectory = file;
			}
		}
		if(launcherDirectory != null) {
			System.out.println("Launcher Directory: " + launcherDirectory);
		}else {
			System.out.println("Could not automatically determine a launcher directory!");
		}

		setIconsAndInstanceDirectories(launcherDirectory);
		
		updateInstallerInstanceIcon();
	}
	
	private static void processArgs(String[] args) {
		for(int i=0; i < args.length; i++) {
			String arg = args[i];
			
			// MultiMC currently does not have a way of telling the game the launcher directory,
			// but it is possible for the assets directory, which is always inside the launcher directory
			if(arg.equals("--assetsDir")) {
				getDirFromArgs("Assets Directory", args, ++i, file -> assetsDirectory = file);
			}
			if(arg.equals("--launcherDir")) {
				getDirFromArgs("Launcher Directory", args, ++i, file -> launcherDirectory = file);
			}
			if(arg.equals("--instanceDir")) {
				getDirFromArgs("Instance Directory", args, ++i, file -> runInstanceDirectory = file);
			}
		}
	}
	
	private static void tryGetInstanceDirectoryFromRunDirectory() {
		File runDirectory = new File("").getAbsoluteFile();
		System.out.println("Run Directory: " + runDirectory);
		
		File maybeInstanceDirectory = runDirectory.getParentFile();
		if(isInstance(maybeInstanceDirectory)) {
			System.out.println("Found instance in run directory: " + maybeInstanceDirectory.getAbsolutePath());
			runInstanceDirectory = maybeInstanceDirectory;
		}else {
			if(maybeInstanceDirectory != null) {
				System.out.println("Not an instance: " + maybeInstanceDirectory.getAbsolutePath());	
			}
		}
	}
	
	private static void setIconsAndInstanceDirectories(File launcherDirectory) {
		instancesDirectory = null;
		iconsDirectory = null;
		
		if(launcherDirectory != null) {
			File configFile = getConfigFile(launcherDirectory);
			if(configFile != null) {
				Map<String, String> config = ConfigUtil.loadPropertiesFile(configFile, '=');
				
				String instanceDirName = config.get("InstanceDir");
				if(instanceDirName != null) {
					File file = new File(launcherDirectory, instanceDirName);
					if(file.isDirectory()) {
						instancesDirectory = file;
					}else {
						System.out.println("Not a directory: " + file.getAbsolutePath());
					}
				}else {
					System.out.println("No instance directory in config file!");
				}
				
				String iconsDirName = config.get("IconsDir");
				if(iconsDirName != null) {
					File file = new File(launcherDirectory, iconsDirName);
					if(file.isDirectory()) {
						iconsDirectory = file;
					}else {
						System.out.println("Not a directory: " + file.getAbsolutePath());
					}
				}else {
					System.out.println("No icons directory in config file!");
				}
			}else {
				System.out.println("No config file in launcher folder: " + launcherDirectory.getAbsolutePath());
			}	
		}
		
		if(instancesDirectory == null) {
			File file = runInstanceDirectory;
			while(file != null) {
				if(isInstancesFolder(file)) {
					instancesDirectory = file;
					break;
				}
				file = file.getParentFile();
			}
		}
	}
	
	/**
	 * Broken because MultiMC overwrites the file when the installer is closed
	 */
	private static void updateInstallerInstanceIcon() {
		if(runInstanceDirectory == null) {
			return;
		}
		File instanceCfg = new File(runInstanceDirectory, "instance.cfg");
		if(!instanceCfg.exists() || instanceCfg.exists()) { // <- intentionally disabled
			return;
		}
		
		File instanceCfgCopy = new File(runInstanceDirectory, "instance.cfg.bak");
		Utils.copyFile(instanceCfg, instanceCfgCopy, null);
		
		// Do not risk breaking the config file by only modifying the line containing iconKey
		StringBuilder str = new StringBuilder();
		InputStream in = null;
		BufferedReader br = null;
		try {
			in = new FileInputStream(instanceCfg);
			br = new BufferedReader(new InputStreamReader(in));
			
			while(true) {
				String line = br.readLine();
				if(line == null) {
					break;
				}
				int i = line.indexOf('=');
				if(i != -1) {
					String key = line.substring(0, i);
					if(key.equals("iconKey")) {
						String iconKey = line.substring(i + 1);
						if(iconKey != null && iconKey.equalsIgnoreCase("default")) {
							System.out.println("Instance is using default icon, let's change that!");
							
							String iconName = "bta64";
							setIcon(iconName, new Download(Global.getDownloadUrl() + "bta-installer/icons/" + iconName + ".png").getAsImage());
							line = "iconKey=" + iconName;
						}
					}
				}
				
				str.append(line).append('\n');
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			return;
		}finally {
			try {
				in.close();
			}catch (Exception e) {}
			try {
				br.close();
			}catch (Exception e) {}
		}
		
		StringUtils.saveStringToFile(instanceCfg, str.toString());
	}
	
	////////////////////////////////
	
	// This only works if the instance directory is not changed in MultiMC
	private static File getLauncherDirectoryFromInstanceDirectory(File directory) {
		directory = directory.getAbsoluteFile();
		while(directory != null) {
			if(isLauncherDirectory(directory)) {
				return directory;
			}
			directory = directory.getParentFile();
		}
		return null;
	}
	
	public static String getInstanceName(File instanceFolder) {
		return ConfigUtil.loadPropertiesFile(new File(instanceFolder, "instance.cfg"), '=').get("name");
	}
	
	private static File getConfigFile(File launcherFolder) {
		String[] launcherNames = new String[] {"multimc", "prismlauncher"};
		
		for(String launcherName : launcherNames) {
			File file = new File(launcherFolder, launcherName + ".cfg");
			System.out.println("Check config file: " + file.getAbsolutePath());
			if(file.isFile()) {
				return file;
			}
		}
		
		return null;
	}
	
	////////////////////////////////
	
	public static void setLauncherDirectory(File file) {
		launcherDirectory = file;
		
		setIconsAndInstanceDirectories(launcherDirectory);
	}
	
	public static boolean setIcon(String name, BufferedImage image) {
		if(name == null) {
			throw new NullPointerException("Name is null!");
		}
		if(image == null) {
			throw new NullPointerException("Icon is null!");
		}
		if(iconsDirectory != null) {
			File iconFile = new File(iconsDirectory, name + ".png");
			try {
				ImageIO.write(image, "png", iconFile);
				return true;
			}catch (Exception e) {
				System.err.println("Could not write icon: " + iconFile.getAbsolutePath());
				e.printStackTrace();
			}
		}
		return false;
	}
	
	////////////////////////////////
	
	public static boolean isInstance(File file) {
		if(file.isDirectory()) {
			File instanceCfg = new File(file, "instance.cfg");
			File mmcPack = new File(file, "mmc-pack.json");
			return instanceCfg.isFile() && mmcPack.isFile();
		}
		return false;
	}
	
	public static boolean isLauncherDirectory(File file) {
		if(file != null && file.isDirectory()) {
			if(new File(file, "multimc.cfg").isFile()) return true;
			if(new File(file, "multimc.exe").isFile()) return true;
			
			if(new File(file, "prismlauncher.cfg").isFile()) return true;
			if(new File(file, "prismlauncher.exe").isFile()) return true;
		}
		return false;
	}
	
	public static boolean isInstancesFolder(File file) {
		return new File(file, "instgroups.json").isFile();
	}
	
	////////////////////////////////
	
	public static File getLauncherDirectory() {
		return launcherDirectory;
	}
	
	public static File getInstancesDirectory() {
		return instancesDirectory;
	}
	
	public static File getIconsDirectory() {
		return iconsDirectory;
	}
	
	public static File getRunInstanceDirectory() {
		return runInstanceDirectory;
	}
}
