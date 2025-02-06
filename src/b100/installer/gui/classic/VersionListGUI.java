package b100.installer.gui.classic;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import b100.installer.Global;
import b100.installer.ModLoader;
import b100.installer.Utils;
import b100.installer.VersionList;

public class VersionListGUI implements ActionListener {
	
	private List<String> allVersions = new ArrayList<>();
	private List<String> filteredVersions = new ArrayList<>();
	private List<ListDataListener> dataListeners = new ArrayList<>();
	
	public JFrame frame;
	public GridPanel mainPanel;

	public JComboBox<ModLoader> modLoaderSelection;
	public JList<String> versionList;
	
	public Listener listener;
	
	public JButton refreshButton;
	public JButton confirmButton;
	public JButton cancelButton;

	private String selectedVersion;
	private ModLoader selectedLoader;

	public ComboBoxData<ModLoader> modLoaderSelectionData;
	public VersionFilter filter;
	
	public VersionListGUI(Listener listener, String selectedVersion, ModLoader selectedLoader, List<ModLoader> modLoaders, VersionFilter filter) {
		this.listener = listener;
		this.selectedVersion = selectedVersion;
		this.selectedLoader = selectedLoader;
		this.filter = filter;
		
		frame = new JFrame("Select Version");
		
		mainPanel = new GridPanel();
		mainPanel.getGridBagConstraints().insets.set(4, 4, 4, 4);

		modLoaderSelectionData = new ComboBoxData<>(modLoaders);
		modLoaderSelection = new JComboBox<>(modLoaderSelectionData);
		modLoaderSelection.setSelectedIndex(Utils.indexOf(modLoaderSelectionData.content, selectedLoader));
		modLoaderSelection.addActionListener(this);
		
		versionList = new JList<>(new ListModelImpl());
		versionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		versionList.setEnabled(false);
		
		JScrollPane versionListScrollPane = new JScrollPane(versionList);
		versionListScrollPane.setPreferredSize(new Dimension(300, 300));
		
		refreshButton = new JButton("Refresh");
		confirmButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
		
		GridPanel buttonPanel = new GridPanel();
		buttonPanel.getGridBagConstraints().insets.set(0, 4, 4, 4);
		if(!Global.isOffline()) {
			buttonPanel.add(refreshButton, 0, 0, 0, 1);
		}
		buttonPanel.add(new JPanel(), 1, 0, 1, 1);
		buttonPanel.add(confirmButton, 2, 0, 0, 1);
		buttonPanel.add(cancelButton, 3, 0, 0, 1);
		
		refreshButton.addActionListener(this);
		confirmButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		mainPanel.add(modLoaderSelection, 0, 0, 1.0, 0.0);
		mainPanel.add(versionListScrollPane, 0, 1, 1.0, 1.0);
		mainPanel.add(buttonPanel, 0, 2, 1.0, 0.0);
		
		frame.add(mainPanel);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		setupVersionList();
	}
	
	public static interface Listener {
		
		public void onVersionSelected(String string, ModLoader loader);
		
	}
	
	public static interface VersionFilter {
		
		public boolean isCompatible(String version, ModLoader loader);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == modLoaderSelection) {
			this.selectedLoader = modLoaderSelectionData.content.get(modLoaderSelection.getSelectedIndex());
			setupVersionList();
		}
		if(e.getSource() == refreshButton) {
			refresh();
		}
		if(e.getSource() == confirmButton) {
			String selection = versionList.getSelectedValue();
			if(selection != null) {
				listener.onVersionSelected(selection, selectedLoader);
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
				JOptionPane.showMessageDialog(ClassicInstallerGUI.instance.mainFrame, "Error!");
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
		allVersions = VersionList.getAllVersions();
		
		filteredVersions.clear();
		for(String version : allVersions) {
			if(filter.isCompatible(version, selectedLoader)) {
				filteredVersions.add(version);
			}
		}
		
		for(int i=0; i < dataListeners.size(); i++) {
			dataListeners.get(i).contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, 0));
		}
		
		versionList.validate();
		
		if(selectedVersion != null) {
			int i = Utils.indexOf(filteredVersions, selectedVersion);
			if(i >= 0) {
				versionList.setSelectedIndex(i);
			}
		}
		
		versionList.setEnabled(true);
		confirmButton.setEnabled(filteredVersions.size() > 0);
	}

	class ListModelImpl implements ListModel<String> {

		@Override
		public int getSize() {
			return filteredVersions.size();
		}

		@Override
		public String getElementAt(int index) {
			return filteredVersions.get(index);
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
