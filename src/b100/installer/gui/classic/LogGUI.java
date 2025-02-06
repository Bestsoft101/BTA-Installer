package b100.installer.gui.classic;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

@SuppressWarnings("serial")
public class LogGUI extends GridPanel implements ActionListener {
	
	public JTextArea log;
	public JScrollPane scrollPane;
	public JButton clearButton;
	
	public LogGUI() {
		log = new JTextArea();
		log.setFont(new Font("monospaced", 1, 12));
		log.setEditable(false);
		log.setAutoscrolls(true);
		((DefaultCaret) log.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		scrollPane = new JScrollPane(log);
		
		clearButton = new JButton("Clear");
		clearButton.addActionListener(this);
		
		add(scrollPane, 0, 0, 1, 1);
		add(clearButton, 0, 1, 1, 0);
		
		System.setOut(new GuiPrintStream(System.out, log, "[INFO] "));
		System.setErr(new GuiPrintStream(System.err, log, "[ERROR] "));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == clearButton) {
			clear();
		}
	}
	
	public void clear() {
		log.setText("");
	}
	
}
