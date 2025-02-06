package b100.installer.gui.classic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;

import b100.installer.Config;
import b100.installer.ModLoader;
import b100.installer.gui.classic.VersionListGUI.VersionFilter;

@SuppressWarnings("serial")
public class VersionComponent extends GridPanel implements ActionListener {

	public JLabel selectedVersionLabel;
	public JButton selectVersionButton;

	private String selectedVersion;
	private ModLoader selectedLoader;
	
	public List<ModLoader> modLoaders;
	public VersionFilter filter;
	
	public VersionComponent(List<ModLoader> modLoaders, VersionFilter filter) {
		this.modLoaders = modLoaders;
		this.filter = filter;
		
		selectedVersionLabel = new JLabel();
		selectVersionButton = new JButton("Choose Version");
		selectVersionButton.addActionListener(this);
		
		getGridBagConstraints().insets.set(0, 0, 0, 16); // at least 16 pixels between text and button
		add(selectedVersionLabel, 0, 0, 1, 0);
		getGridBagConstraints().insets.set(0, 0, 0, 0);
		add(selectVersionButton, 1, 0, 0, 0);
		
		setVersionAndLoader(Config.getInstance().getLastOrNewestVersion(), ModLoader.None);
	}
	
	public void setVersionAndLoader(String version, ModLoader loader) {
		if(version == null) {
			throw new NullPointerException();
		}
		
		this.selectedVersion = version;
		this.selectedLoader = loader;
		
		if(selectedLoader != ModLoader.None) {
			this.selectedVersionLabel.setText(selectedVersion + " " + selectedLoader.getDisplayName());	
		}else {
			this.selectedVersionLabel.setText(selectedVersion);
		}
	}
	
	public String getSelectedVersion() {
		return selectedVersion;
	}
	
	public ModLoader getSelectedLoader() {
		return selectedLoader;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == selectVersionButton) {
			new VersionListGUI((version, loader) -> setVersionAndLoader(version, loader), selectedVersion, selectedLoader, modLoaders, filter);
		}
	}
}
