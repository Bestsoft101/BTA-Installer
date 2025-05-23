package b100.installer.installer;

public interface ProgressListener {
	
	public void update(String string);
	
	public void setProgress(float progress);
	
	public static class Dummy implements ProgressListener {
		@Override
		public void update(String string) {
			
		}
		@Override
		public void setProgress(float progress) {
			
		}
	}
}