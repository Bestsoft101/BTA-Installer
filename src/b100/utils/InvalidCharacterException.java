package b100.utils;

import java.io.PrintStream;
import java.util.List;

public class InvalidCharacterException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	private StringReader reader;
	
	private String message;
	
	private int line = 1;
	private int column = 1;
	
	public InvalidCharacterException(StringReader stringReader) {
		this(stringReader, null);
	}
	
	public InvalidCharacterException(StringReader stringReader, String message) {
		this.reader = stringReader;
		this.message = message;
		
		String string = reader.string();
		for(int i=0; i < reader.position(); i++) {
			char c = string.charAt(i);
			if(c == '\n') {
				line++;
				column = 1;
			}else {
				column++;
			}
		}
	}
	
	public String getLinePreview() {
		StringWriter stringWriter = new StringWriter();
		
		stringWriter.writeln(getMessage());
		stringWriter.writeln();
		
		List<String> lines = reader.lines();
		
		int startLine = Math.max(0, line - 4);
		int endLine = Math.min(lines.size() - 1, line);
		
		for(int i = startLine; i < endLine; i++) {
			String line = lines.get(i);
			
			stringWriter.writeln(line);
			if(i + 1 == this.line) {
				for(int j=0; j < line.length(); j++) {
					int l = getPrintChar(line.charAt(j), true).length();
					boolean thisChar = j + 1 == column;
					
					for(int k=0; k < l; k++) {
						stringWriter.write(thisChar ? '^' : ' ');
					}
				}
				
				stringWriter.writeln();
			}
		}
		
		return stringWriter.toString();
	}
	
	public String getMessage() {
		if(message != null) {
			return message + " at line " + line + " column " + column + " (index " + reader.position() + ")";
		}else {
			return "Invalid character \"" + getPrintChar(reader.get(), false) + "\" at line " + line + " column " + column + " (index " + reader.position() + ")";	
		}
	}
	
	public static String getPrintChar(char c, boolean a) {
		if(c == '\\') return a ? "\\" : "\\\\";
		if(c == '\n') return a ? " " : "\\n";
		if(c == '\t') return a ? " " : "\\t";
		return "" + c;
	}
	
	public void printStackTrace(PrintStream s) {
		try{
			s.println(getLinePreview());
		}catch (Exception e) {
			s.println("Could not create line preview: "+e.getClass().getName()+": "+e.getMessage());
		}
		
		super.printStackTrace(s);
	}
	
}
