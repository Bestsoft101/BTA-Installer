package b100.installer.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;

public class Log {
	
	private static PrintStream previousOut;
	private static PrintStream previousErr;
	
	private static PrintStream customOut;
	private static PrintStream customErr;
	
	private static OutputStream out;
	private static OutputStreamWriter writer;
	
	private static boolean isSetup;
	
	public static void setup(File logFile) {
		if(isSetup) {
			close();
		}
		try {
			previousOut = System.out;
			previousErr = System.err;
			
			logFile = logFile.getAbsoluteFile();
			File parent = logFile.getParentFile();
			if(!parent.exists()) {
				parent.mkdirs();
			}
			
			out = new FileOutputStream(logFile);
			writer = new OutputStreamWriter(out);
			
			customOut = new LogPrintStream(System.out, "[OUT] ");
			customErr = new LogPrintStream(System.err, "[ERR] ");
			
			isSetup = true;
		}catch (Exception e) {
			throw new RuntimeException("Setting up log: " + logFile.getAbsolutePath());
		}
	}
	
	public static void enable() {
		if(!isSetup) {
			throw new RuntimeException("Log is not set up!");
		}
		
		System.setOut(customOut);
		System.setErr(customErr);
	}
	
	public static void disable() {
		if(!isSetup) {
			throw new RuntimeException("Log is not set up!");
		}
		
		System.setOut(previousOut);
		System.setErr(previousErr);
	}
	
	public static void close() {
		if(!isSetup) {
			return;
		}
		
		System.setOut(previousOut);
		System.setErr(previousErr);
		
		try {
			out.close();
		}catch (Exception e) {}
		try {
			writer.close();
		}catch (Exception e) {}
		
		out = null;
		writer = null;
		
		isSetup = false;
	}
	
	static class LogPrintStream extends PrintStream {

		private String name;
		private PrintStream previous;
		private boolean lineBreak = true;
		
		public LogPrintStream(PrintStream stream, String name) {
			super(stream);
			
			this.previous = stream;
			this.name = name;
		}
		
		@Override
		public void println(String str) {
			printlnImpl(str);
		}
		
		@Override
		public void println(boolean x) {
			printlnImpl(String.valueOf(x));
		}
		
		@Override
		public void println(char x) {
			printlnImpl(String.valueOf(x));
		}
		
		@Override
		public void println(char[] x) {
			printlnImpl(new String(x));
		}
		
		@Override
		public void println(double x) {
			printlnImpl(String.valueOf(x));
		}
		
		@Override
		public void println(float x) {
			printlnImpl(String.valueOf(x));
		}
		
		@Override
		public void println(int x) {
			printlnImpl(String.valueOf(x));
		}
		
		@Override
		public void println(long x) {
			printlnImpl(String.valueOf(x));
		}
		
		@Override
		public void println(Object x) {
			printlnImpl(String.valueOf(x));
		}
		
		public void printlnImpl(String str) {
			printImpl(str + "\n");
		}
		
		@Override
		public void println() {
			printlnImpl("");
		}
		
		////////////////////////////////
		
		@Override
		public void print(String str) {
			printImpl(str);
		}

		////////////////////////////////
		
		public void printImpl(String str) {
			StringBuilder strb = new StringBuilder();
			try {
				for(int i=0; i < str.length(); i++) {
					char c = str.charAt(i);
					if(lineBreak) {
						strb.append(name);
						lineBreak = false;
					}
					if(c == '\t') {
						strb.append("    ");
						continue;
					}
					if(c == '\n') {
						lineBreak = true;
					}
					strb.append(c);
				}
				str = strb.toString();
				writer.write(str);
				writer.flush();
				previous.print(str);
			}catch (Exception e) {
				disable();
				e.printStackTrace();
			}
		}
	}
}
