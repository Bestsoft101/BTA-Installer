package b100.installer.gui.modern.render;

import java.awt.image.BufferedImage;

public abstract class Renderer {
	
	public static Renderer instance;
	
	public abstract int getWidth();
	
	public abstract int getHeight();
	
	public abstract void drawImage(BufferedImage image, int x, int y);
	
	public abstract void drawImageStretched(BufferedImage image, int x, int y, int w, int h);
	
	public abstract void drawSubImage(BufferedImage image, int x, int y, int w, int h, int sx, int sy);
	
	public abstract void drawRectangle(int x, int y, int w, int h);
	
	public void drawRectangleOutline(int x, int y, int w, int h) {
		drawRectangle(x, y, 1, h);
		drawRectangle(x, y, w, 1);
		drawRectangle(x + w - 1, y, 1, h);
		drawRectangle(x, y + h - 1, w, 1);
	}
	
	public abstract void setColor(int color);
	
	public void resetColor() {
		setColor(0xFFFFFF);
	}
	
	public abstract void enableScissor(int x, int y, int w, int h);
	
	public abstract void disableScissor();
	
	public abstract void enableInvertColor();
	
	public abstract void disableInvertColor();
	
}
