package b100.installer.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import b100.installer.Utils;
import b100.installer.VersionList;
import b100.installer.gui.utils.GridPanel;

public class VersionListGUI implements ActionListener {
	
	private List<String> versions = new ArrayList<>();
	private List<ListDataListener> dataListeners = new ArrayList<>();
	
	public JFrame frame;
	public GridPanel mainPanel;
	
	public JList<String> versionList;
	
	public Listener listener;
	
	public JButton refreshButton;
	public JButton confirmButton;
	public JButton cancelButton;
	
	private String selectedVersion;
	
	public VersionListGUI(Listener listener, String selectedVersion) {
		this.listener = listener;
		this.selectedVersion = selectedVersion;
		
		frame = new JFrame("Select Version");
		
		mainPanel = new GridPanel();
		mainPanel.getGridBagConstraints().insets.set(4, 4, 4, 4);
		
		versionList = new JList<>(new ListModelImpl());
		versionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		versionList.setEnabled(false);
		
		setupVersionList();
		
		JScrollPane versionListScrollPane = new JScrollPane(versionList);
		versionListScrollPane.setPreferredSize(new Dimension(300, 300));
		
		refreshButton = new JButton("Refresh");
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
		versionList.setEnabled(false);
		
		new Thread(() -> {
			long startTime = System.currentTimeMillis();
			
			try {
				VersionList.refreshVersionList();
			}catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(InstallerGUI.instance.mainFrame, "Error!");
				versionList.setEnabled(true);
			}
			
			long updateTime = System.currentTimeMillis() - startTime;
			long sleepTime = Math.max(0, 1500 - updateTime);
			if(sleepTime > 0) {
				try {
					Thread.sleep(sleepTime);
				}catch (Exception e) {}
			}
			
			setupVersionList();
		}).start();
	}
	
	public void setupVersionList() {
		versions = VersionList.getAllVersions();
		
		for(int i=0; i < dataListeners.size(); i++) {
			dataListeners.get(i).contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, 0));
		}
		
		versionList.validate();
		
		if(selectedVersion != null) {
			int i = Utils.indexOf(versions, selectedVersion);
			if(i >= 0) {
				versionList.setSelectedIndex(i);
			}
		}
		
		versionList.setEnabled(true);
	}

	class ListModelImpl implements ListModel<String> {

		@Override
		public int getSize() {
			return versions.size();
		}

		@Override
		public String getElementAt(int index) {
			return versions.get(index);
		}

		@Override
		public void addListDataListener(ListDataListener l) {
			dataListeners.add(l);
		}

		@Override
		public void removeListDataListener(ListDataListener l) {
			dataListeners.remove(l);
		}
		
	}
	
}
