package b100.utils;

public class StringWriter {
	
	private StringBuilder string = new StringBuilder();
	private int tabs = 0;
	private String tabString = "\t";
	
	public StringWriter() {
		
	}
	
	public StringWriter write(Writable writable) {
		writable.write(this);
		return this;
	}

	public void write(String string) {
		for(int i=0; i < string.length(); i++) {
			write(string.charAt(i));
		}
	}

	public void writeln(String string) {
		write(string + "\n"); 
	}

	public void writeln() {
		write("\n");
	}
	
	public void write(char c) {
		if(isLastCharLineBreak()) {
			for(int i=0; i < tabs; i++) {
				string.append(tabString);
			}
		}
		string.append(c);
	}
	
	public boolean isLastCharLineBreak() {
		if(string.length() == 0) return false;
		return string.charAt(string.length() - 1) == '\n';
	}
	
	public void addTab() {
		tabs++;
	}
	
	public void removeTab() {
		tabs--;
	}
	
	public String toString() {
		return string.toString();
	}
	
	public String getTabString() {
		return tabString;
	}
	
	public void setTabString(String tabString) {
		this.tabString = tabString;
	}
	
}
