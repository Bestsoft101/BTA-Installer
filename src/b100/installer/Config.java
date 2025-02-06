package b100.installer;

import java.io.File;

import b100.installer.config.ConfigFile;
import b100.installer.config.LongProperty;
import b100.installer.config.StringProperty;

public class Config extends ConfigFile {

	public static final File CONFIG_FOLDER = Global.getInstallerDirectory();
	public static final File CONFIG_FILE = new File(CONFIG_FOLDER, "installer.txt");
	private static final Config INSTANCE = new Config(CONFIG_FILE);
	
	public static Config getInstance() {
		return INSTANCE;
	}

	public StringProperty lastSelectedVersion = register("lastSelectedVersion", new StringProperty());
	public StringProperty lastInstallType = register("lastInstallType", new StringProperty());
	public StringProperty lastMinecraftDirectory = register("lastMinecraftDirectory", new StringProperty());
	public StringProperty lastBetaCraftDirectory = register("lastBetaCraftDirectory", new StringProperty());
	public StringProperty lastMultimcDirectory = register("lastMultimcDirectory", new StringProperty());
	public LongProperty lastVersionQueryTime = register("lastVersionQueryTime", new LongProperty(0));
	
	private Config(File file) {
		super(file);
	}
	
	public String getLastOrNewestVersion() {
		if(lastSelectedVersion.value != null) {
			return lastSelectedVersion.value;
		}
		return VersionList.getAllVersions().get(0);
	}

}
