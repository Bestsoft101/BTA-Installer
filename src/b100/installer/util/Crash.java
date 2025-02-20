package b100.installer.util;

public class Crash {
	public Throwable cause;
	public Thread thread;
	
	public Crash(Throwable cause, Thread thread) {
		this.cause = cause;
		this.thread = thread;
	}
}