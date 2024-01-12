package b100.installer;

import java.io.File;

import b100.utils.StringUtils;

public class Config {
	
	private static final Config instance = new Config();
	
	public static Config getInstance() {
		return instance;
	}

	public File configFolder = Installer.getInstallerDirectory();
	public File configFile = new File(configFolder, "installer.txt");

	public String lastSelectedVersion;
	public String lastInstallType;
	
	private Config() {
		
	}
	
	public void load() {
		if(!configFile.exists()) {
			return;
		}
		String string = StringUtils.getFileContentAsString(configFile);
		String[] lines = string.split("\n");
		
		for(int i=0; i < lines.length; i++) {
			String line = lines[i];
			int j = line.indexOf(':');
			if(j == -1 || j >= line.length() - 1) {
				continue;
			}
			
			String key = line.substring(0, j);
			String value = line.substring(j + 1);
			
			try {
				parseConfig(key, value);
			}catch (Exception e) {
				System.err.println("Error parsing config line: '" + line + "'");
				e.printStackTrace();
			}
		}
	}
	
	public void parseConfig(String key, String value) {
		if(key.equals("lastSelectedVersion")) lastSelectedVersion = value;
		if(key.equals("lastInstallType")) lastInstallType = value;
	}
	
	public void save() {
		System.out.println("Saving config");
		StringBuilder str = new StringBuilder();
		
		if(lastSelectedVersion != null) str.append("lastSelectedVersion:" + lastSelectedVersion + "\n");
		if(lastInstallType != null) str.append("lastInstallType:" + lastInstallType + "\n");
		
		StringUtils.saveStringToFile(configFile, str.toString());
		System.out.println("Saved!");
	}

}
