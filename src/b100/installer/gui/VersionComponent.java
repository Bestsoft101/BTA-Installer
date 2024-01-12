package b100.installer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;

import b100.installer.Config;
import b100.installer.gui.utils.GridPanel;

@SuppressWarnings("serial")
public class VersionComponent extends GridPanel implements ActionListener {

	public JLabel selectedVersionLabel;
	public JButton selectVersionButton;
	
	private String selectedVersion;
	
	public VersionComponent() {
		selectedVersionLabel = new JLabel();
		selectVersionButton = new JButton("Choose Version");
		selectVersionButton.addActionListener(this);
		
		getGridBagConstraints().insets.set(0, 0, 0, 16); // at least 16 pixels between text and button
		add(selectedVersionLabel, 0, 0, 1, 0);
		getGridBagConstraints().insets.set(0, 0, 0, 0);
		add(selectVersionButton, 1, 0, 0, 0);
		
		setVersion(Config.getInstance().getLastOrNewestVersion());
	}
	
	public void setVersion(String version) {
		if(version == null) {
			throw new NullPointerException();
		}
		
		this.selectedVersion = version;
		this.selectedVersionLabel.setText("Selected Version: " + selectedVersion);
	}
	
	public String getVersion() {
		return selectedVersion;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == selectVersionButton) {
			new VersionListGUI((version) -> setVersion(version), selectedVersion);
		}
	}
	
	

}
