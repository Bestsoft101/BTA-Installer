package b100.installer.gui.modern;

import java.awt.image.BufferedImage;

public abstract class Renderer {
	
	public static Renderer instance;
	
	public abstract int getWidth();
	
	public abstract int getHeight();
	
	public abstract void drawImage(BufferedImage image, int x, int y);
	
	public abstract void drawImage(BufferedImage image, int x, int y, int w, int h);
	
	public abstract void drawSubImage(BufferedImage image, int x, int y, int w, int h, int sx, int sy);
	
	public abstract void setColor(int color);
	
	public void resetColor() {
		setColor(0xFFFFFF);
	}
	
}
