package b100.utils;

import static b100.utils.Utils.*;

import java.util.ArrayList;
import java.util.List;

public class StringReader {
	
	private String string;
	private int i;
	
	public StringReader(String string) {
		this.string = requireNonNull(string);
	}
	
	public char get() {
		return string.charAt(i);
	}
	
	public String get(int count) {
		return string.substring(i, i + count);
	}
	
	public void skipWhitespace() {
		while(i < string.length() && isWhitespace(get())) i++;
	}
	
	public char getAndSkip() {
		char c = get();
		next();
		return c;
	}
	
	public String getAndSkip(int count) {
		String str = get(count);
		i += count;
		return str;
	}
	
	public void expectAndSkip(char c) {
		expect(c);
		next();
	}
	
	public void expect(char c) {
		expectOne("" + c);
	}
	
	public void expectOne(String chars) {
		for(int i=0; i < chars.length(); i++) if(chars.charAt(i) == get()) return;
		throw new InvalidCharacterException(this);
	}
	
	public boolean isWhitespace(char c) {
		return c == ' ' || c == '\t' || c == '\n';
	}
	
	public void next() {
		i++;
	}
	
	public boolean isNext(String string) {
		return this.string.substring(i, i + string.length()).equals(string);
	}
	
	public void skip(int i) {
		this.i += i;
	}
	
	public void expectAndSkip(String string) {
		if(!isNext(string)) throw new InvalidCharacterException(this);
		skip(string.length());
	}
	
	public int remainingCharacters() {
		return string.length() - i;
	}
	
	public String readUntilCharacter(char endChar) {
		StringBuilder builder = new StringBuilder();
		while(i < string.length()) {
			char c = get();
			if(c == endChar) {
				break;
			}else {
				builder.append(c);
				next();
			}
		}
		return builder.toString();
	}
	
	// ------------------------ //
	
	public String string() {
		return string;
	}
	
	public int position() {
		return i;
	}
	
	public List<String> lines() {
		List<String> lines = new ArrayList<>();
		String line = "";
		
		for(int i=0; i < string.length(); i++) {
			char c = string.charAt(i);
			if(c == '\n') {
				lines.add(line);
				line = "";
			}else {
				line += c;
			}
		}
		
		lines.add(line);
		
		return lines;
	}
	
}
