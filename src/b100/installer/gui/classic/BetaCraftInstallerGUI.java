package b100.installer.gui.classic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JTextField;

import b100.installer.Config;
import b100.installer.ModLoader;
import b100.installer.Utils;
import b100.installer.VersionList;
import b100.installer.installer.BetaCraftInstaller;
import b100.json.element.JsonObject;

@SuppressWarnings("serial")
public class BetaCraftInstallerGUI extends BaseInstallerGUI {

	public static final String INSTALL_TYPE = "betacraft";
	
	public JTextField betacraftDirectoryTextfield;
	public JTextField instanceTextfield;
	
	public BetaCraftInstaller betacraftInstaller = new BetaCraftInstaller();
	
	public BetaCraftInstallerGUI(ClassicInstallerGUI installerGUI) {
		super(installerGUI);
		
		int inset = 4;
		getGridBagConstraints().insets.set(inset, inset, inset, inset);
		
		betacraftDirectoryTextfield = new JTextField();
		betacraftDirectoryTextfield.setText(getBetaCraftDirectory());
		
		instanceTextfield = new JTextField();
		instanceTextfield.setText("Better Than Adventure!");
		
		List<ModLoader> modLoaders = new ArrayList<>();
		modLoaders.add(ModLoader.None);
		versionComponent = new VersionComponent(modLoaders, (version, modLoader) -> modLoader == ModLoader.None && isVersionSupported(version));
		
		installButton = new JButton("Install");
		installButton.addActionListener(this);
		
		add(GuiUtils.createImagePanel("/logo.png"), 0, 0, 1, 1);
		add(GuiUtils.createTitledPanel(betacraftDirectoryTextfield, "BetaCraft Directory"), 0, 1, 1, 0);
		add(GuiUtils.createTitledPanel(instanceTextfield, "Instance"), 0, 2, 1, 0);
		add(versionComponent, 0, 3, 1, 0);
		add(installButton, 0, 4, 1, 0);
	}
	
	public boolean isVersionSupported(String version) {
		JsonObject versionObject = VersionList.getVersion(version);
		JsonObject betaCraftObject = versionObject.getObject("betacraft");
		return betaCraftObject != null;
	}

	@Override
	public boolean install() {
		Map<String, Object> parameters = new HashMap<>();
		
		parameters.put("version", versionComponent.getSelectedVersion());
		parameters.put("loader", versionComponent.getSelectedLoader());
		parameters.put("betacraftdir", betacraftDirectoryTextfield.getText());
		parameters.put("instancename", instanceTextfield.getText());
		
		return betacraftInstaller.install(parameters);
	}
	
	public String getBetaCraftDirectory() {
		String last = Config.getInstance().lastBetaCraftDirectory.value;
		if(last != null) {
			return last;
		}
		return getDefaultBetacraftDirectory().getAbsolutePath();
	}
	
	public static File getDefaultBetacraftDirectory() {
		String folder = null;
		
		String os = System.getProperty("os.name").toLowerCase();
		boolean isWindows = os.indexOf("win") >= 0;
		boolean isLinux = os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") >= 0;
		boolean isMac = os.indexOf("mac") >= 0;
		
		if(isWindows) {
			folder = System.getenv("APPDATA") + "\\.betacraft\\";
		}else if(isLinux) {
			folder = System.getProperty("user.home") + "/.betacraft";
		}else if(isMac) {
			folder = System.getProperty("user.home") + "/Library/Application Support/betacraft/";
		}else {
			return Utils.getAppDirectory("betacraft");
		}
		return new File(folder);
	}

}
