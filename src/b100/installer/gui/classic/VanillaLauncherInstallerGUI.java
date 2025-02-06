package b100.installer.gui.classic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JTextField;

import b100.installer.Config;
import b100.installer.ModLoader;
import b100.installer.Utils;
import b100.installer.installer.VanillaLauncherInstaller;

@SuppressWarnings("serial")
public class VanillaLauncherInstallerGUI extends BaseInstallerGUI {
	
	public static final String INSTALL_TYPE = "vanilla";
	
	public JTextField minecraftDirectoryTextfield;
	
	public VanillaLauncherInstaller vanillaLauncherInstaller = new VanillaLauncherInstaller();
	
	public VanillaLauncherInstallerGUI(ClassicInstallerGUI installerGUI) {
		super(installerGUI);
		
		int inset = 4;
		getGridBagConstraints().insets.set(inset, inset, inset, inset);
		
		minecraftDirectoryTextfield = new JTextField();
		minecraftDirectoryTextfield.setText(getMinecraftDirectory());

		List<ModLoader> modLoaders = new ArrayList<>();
		modLoaders.add(ModLoader.None);
		modLoaders.add(ModLoader.ASMLoader);
		modLoaders.add(ModLoader.Babric);
		versionComponent = new VersionComponent(modLoaders, vanillaLauncherInstaller);
		
		installButton = new JButton("Install");
		installButton.addActionListener(this);
		
		add(GuiUtils.createImagePanel("/logo.png"), 0, 0, 1, 1);
		add(GuiUtils.createTitledPanel(minecraftDirectoryTextfield, "Minecraft Directory"), 0, 1, 1, 0);
		add(versionComponent, 0, 2, 1, 0);
		add(installButton, 0, 3, 1, 0);
	}
	
	@Override
	public boolean install() {
		Map<String, Object> parameters = new HashMap<>();
		
		parameters.put("version", versionComponent.getSelectedVersion());
		parameters.put("loader", versionComponent.getSelectedLoader());
		parameters.put("mcdir", minecraftDirectoryTextfield.getText());
		
		return vanillaLauncherInstaller.install(parameters);
	}
	
	public String getMinecraftDirectory() {
		String last = Config.getInstance().lastMinecraftDirectory.value;
		if(last != null) {
			return last;
		}
		return Utils.getMinecraftDirectory().getAbsolutePath();
	}

}
