package b100.installer.gui.classic;

import java.io.PrintStream;

import javax.swing.JTextArea;

public class GuiPrintStream extends PrintStream {
	
	private String prefix;
	private JTextArea textArea;
	private PrintStream prev;
	
	public GuiPrintStream(PrintStream printStream, JTextArea textArea, String prefix) {
		super(printStream);
		
		this.prefix = prefix;
		this.prev = printStream;
		this.textArea = textArea;
	}
	
	public void write(String string) {
		prev.print(string);
		
		textArea.append(prefix + string);
	}
	
	@Override
	public void println() {
		write("\n");
	}
	
	@Override
	public void println(String x) {
		write(x + "\n");
	}
	
	@Override
	public void println(boolean x) {
		write(x + "\n");
	}
	
	@Override
	public void println(char x) {
		write(x + "\n");
	}
	
	@Override
	public void println(double x) {
		write(x + "\n");
	}
	
	@Override
	public void println(float x) {
		write(x + "\n");
	}
	
	@Override
	public void println(int x) {
		write(x + "\n");
	}
	
	@Override
	public void println(long x) {
		write(x + "\n");
	}
	
	@Override
	public void println(Object x) {
		write(x + "\n");
	}
	
	@Override
	public void println(char[] x) {
		write(new StringBuilder().append(x).toString() + "\n");
	}
	
	@Override
	public void print(String s) {
		write(s);
	}
	
	@Override
	public void print(boolean b) {
		write(String.valueOf(b));
	}
	
	@Override
	public void print(char c) {
		write(String.valueOf(c));
	}
	
	@Override
	public void print(double d) {
		write(String.valueOf(d));
	}
	
	@Override
	public void print(float f) {
		write(String.valueOf(f));
	}
	
	@Override
	public void print(int i) {
		write(String.valueOf(i));
	}
	
	@Override
	public void print(long l) {
		write(String.valueOf(l));
	}
	
	@Override
	public void print(Object obj) {
		write(String.valueOf(obj));
	}
	
	@Override
	public void print(char[] s) {
		write(new StringBuilder().append(s).toString());
	}
	
}