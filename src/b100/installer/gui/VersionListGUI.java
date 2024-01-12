package b100.installer.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import b100.installer.Utils;
import b100.installer.VersionList;
import b100.installer.gui.utils.GridPanel;

public class VersionListGUI implements ActionListener {
	
	public JFrame frame;
	public GridPanel mainPanel;
	
	public JList<String> versionList;
	
	public Listener listener;
	
	public JButton refreshButton;
	public JButton confirmButton;
	public JButton cancelButton;
	
	public VersionListGUI(Listener listener, String selectedVersion) {
		this.listener = listener;

		String[] versions = Utils.toArray(VersionList.getAllVersions());
		
		frame = new JFrame("Select Version");
		
		mainPanel = new GridPanel();
		mainPanel.getGridBagConstraints().insets.set(4, 4, 4, 4);
		
		versionList = new JList<>(versions);
		versionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		if(selectedVersion != null) {
			int i = Utils.indexOf(versions, selectedVersion);
			if(i >= 0) {
				versionList.setSelectedIndex(i);
			}
		}
		
		JScrollPane versionListScrollPane = new JScrollPane(versionList);
		versionListScrollPane.setPreferredSize(new Dimension(300, 300));
		
		refreshButton = new JButton("Refresh");
		refreshButton.setEnabled(false);
		confirmButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
		
		GridPanel buttonPanel = new GridPanel();
		buttonPanel.getGridBagConstraints().insets.set(0, 4, 4, 4);
		buttonPanel.add(refreshButton, 0, 0, 0, 1);
		buttonPanel.add(new JPanel(), 1, 0, 1, 1);
		buttonPanel.add(confirmButton, 2, 0, 0, 1);
		buttonPanel.add(cancelButton, 3, 0, 0, 1);
		
		refreshButton.addActionListener(this);
		confirmButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		mainPanel.add(versionListScrollPane, 0, 0, 1, 1);
		mainPanel.add(buttonPanel, 0, 1, 1, 0);
		
		frame.add(mainPanel);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public static interface Listener {
		
		public void onVersionSelected(String string);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == refreshButton) {
			refresh();
		}
		if(e.getSource() == confirmButton) {
			String selection = versionList.getSelectedValue();
			if(selection != null) {
				listener.onVersionSelected(selection);
			}
			frame.dispose();
		}
		if(e.getSource() == cancelButton) {
			frame.dispose();
		}
	}
	
	public void refresh() {
		
	}
	
}
