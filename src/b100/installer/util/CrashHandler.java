package b100.installer.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class CrashHandler {
	
	public CrashHandler(String message) {
		this(message, null);
	}
	
	public CrashHandler(String message, Component parent) {
		JFrame frame = new JFrame();
		frame.setTitle("Crash Handler");
		
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setText(message);
		
		JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(640, 320));
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		int padding = 4;
		c.insets = new Insets(padding, padding, padding, padding);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		
		panel.add(scrollPane, c);
		
		frame.add(panel);
		frame.pack();
		frame.setMinimumSize(new Dimension(frame.getWidth(), frame.getHeight()));
		frame.setLocationRelativeTo(parent);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public static void createErrorLog(StringBuilder str, Throwable throwable) {
		str.append(throwable.getClass().getName());
		
		String message = throwable.getMessage();
		if(message != null) {
			str.append(": ").append(message);
		}
		
		StackTraceElement[] stackTrace = throwable.getStackTrace();
		
		for(int i=0; i < stackTrace.length; i++) {
			StackTraceElement element = stackTrace[i];
			
			str.append('\n').append("    at ").append(element);
		}
		
		Throwable cause = throwable.getCause();
		if(cause != null) {
			str.append("\nCaused by: ");
			
			createErrorLog(str, cause);
		}
	}
	
}
