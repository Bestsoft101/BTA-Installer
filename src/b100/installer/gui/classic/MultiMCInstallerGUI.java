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
import b100.installer.installer.MultiMcInstaller;

@SuppressWarnings("serial")
public class MultiMcInstallerGUI extends BaseInstallerGUI {
	
	public static final String INSTALL_TYPE = "multimc";
	
	public JTextField multimcDirectoryTextfield;
	
	public MultiMcInstaller multiMcInstaller = new MultiMcInstaller();
	
	public MultiMcInstallerGUI(InstallerGuiClassic installerGUI) {
		super(installerGUI);

		int inset = 4;
		getGridBagConstraints().insets.set(inset, inset, inset, inset);
		
		multimcDirectoryTextfield = new JTextField(Config.getInstance().lastMultimcDirectory.value);

		List<ModLoader> modLoaders = new ArrayList<>();
		modLoaders.add(ModLoader.None);
		versionComponent = new VersionComponent(modLoaders, multiMcInstaller);
		
		installButton = new JButton("Install");
		installButton.addActionListener(this);
		
		add(GuiUtils.createImagePanel("/logo.png"), 0, 0, 1, 1);
		add(GuiUtils.createTitledPanel(multimcDirectoryTextfield, "MultiMC / Prism Launcher Directory"), 0, 1, 1, 0);
		add(versionComponent, 0, 2, 1, 0);
		add(installButton, 0, 3, 1, 0);
	}

	@Override
	public boolean install() {
		Map<String, Object> parameters = new HashMap<>();
		
		File multiMcFolder = new File(multimcDirectoryTextfield.getText());
		File instancesFolder = new File(multiMcFolder, "instances");
		
		parameters.put("instancesfolder", instancesFolder.getAbsolutePath());
		parameters.put("version", versionComponent.getSelectedVersion());
		
		return multiMcInstaller.install(parameters);
	}
	
}
