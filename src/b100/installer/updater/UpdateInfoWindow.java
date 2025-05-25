package b100.installer.updater;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import b100.installer.Download;
import b100.installer.Global;
import b100.installer.gui.classic.GridPanel;
import b100.installer.util.Utils;

public class UpdateInfoWindow implements ActionListener {

	public JFrame frame;
	public GridPanel panel;
	public JTextArea textArea;
	public JButton updateButton;
	public JButton dontUpdateButton;

	public Runnable updateAction;
	public Runnable dontUpdateAction;
	
	public UpdateInfoWindow(List<String> availableVersions, Runnable updateAction, Runnable dontUpdateAction) {
		this.updateAction = updateAction;
		this.dontUpdateAction = dontUpdateAction;
		
		frame = new JFrame();
		frame.setTitle("Better than Adventure! Installer");
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setFocusable(false);
		textArea.setText("Loading changelog...");
		textArea.setFont(new Font("Monospaced", 1, 12));
		
		JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setPreferredSize(new Dimension(400, 240));
		
		JPanel buttonPanel = new JPanel();
		
		Dimension buttonSize = new Dimension(128, 24);
		
		dontUpdateButton = new JButton("Don't Update");
		dontUpdateButton.addActionListener(this);
		dontUpdateButton.setPreferredSize(buttonSize);
		buttonPanel.add(dontUpdateButton);
		
		updateButton = new JButton("Update");
		updateButton.addActionListener(this);
		updateButton.setPreferredSize(buttonSize);
		buttonPanel.add(updateButton);

		panel = new GridPanel();
		GridBagConstraints c = panel.getGridBagConstraints();
		int padding = 4;
		c.insets = new Insets(padding, padding, padding, padding);
		
		JLabel label1 = new JLabel("Update Available!");
		label1.setPreferredSize(new Dimension(24, 24));
		label1.setHorizontalAlignment(JLabel.CENTER);
		
		panel.add(label1, 0, 0, 1.0f, 0.0f);
		
		c.insets.top = 0;
		panel.add(scrollPane, 0, 1, 1.0f, 1.0f);
		panel.add(buttonPanel, 0, 2, 1.0f, 0.0f);
		
		frame.add(panel);
		frame.pack();
		frame.setMinimumSize(frame.getSize());
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
		
		Utils.createAndRunThread("Get-Changelog", true, new DownloadChangelogThread(availableVersions));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == updateButton) {
			Utils.createAndRunThread("Update-Installer", false, updateAction);
			frame.dispose();
		}
		if(e.getSource() == dontUpdateButton) {
			Utils.createAndRunThread("Run-Installer", false, dontUpdateAction);
			frame.dispose();
		}
	}
	
	class DownloadChangelogThread implements Runnable {
		
		List<String> versions;
		
		public DownloadChangelogThread(List<String> versions) {
			this.versions = versions;
		}

		@Override
		public void run() {
			StringBuilder str = new StringBuilder();
			str.append("Changelog:\n\n");
			
			for(int i=0; i < versions.size(); i++) {
				String version = versions.get(i);
				
				str.append(version).append(":\n");
				
				String url = Global.getDownloadUrl() + "bta-installer/changelogs/" + version + ".txt";
				String changelog = new Download(url).downloadAsString();
				String[] lines = changelog.split("\n");
				
				for(String line : lines) {
					line = line.trim();
					if(line.length() == 0) {
						continue;
					}
					str.append(" - ").append(line).append('\n');
				}
				
				str.append('\n');
			}
			
			SwingUtilities.invokeLater(() -> {
				textArea.setText(str.toString());
				textArea.setCaretPosition(0);
			});
		}
	}
	
}
