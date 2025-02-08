package b100.installer.gui.modern;

import java.awt.image.BufferedImage;

import b100.installer.Utils;

public class FontRenderer {
	
	public static FontRenderer instance;
	
	public Renderer renderer;
	
	private byte[] charWidths;
	
	public FontRenderer(Renderer renderer) {
		this.renderer = renderer;
		
		initializeCharacterSizes();
	}
	
	private void initializeCharacterSizes() {
		charWidths = new byte[256];
		
		BufferedImage font = Textures.font;

		final int charSize = 8;
		
		for(int charIndex = 0; charIndex < 256; charIndex++) {
			final int charX = charIndex & 0xF;
			final int charY = charIndex >> 4;
			
			int width = 0;
			
			if(charIndex == ' ') {
				charWidths[charIndex] = 5;
			}else if(charIndex < 32 || (charIndex >= 176 && charIndex < 255)) {
				charWidths[charIndex] = 8;
			}else {
				for(int i=0; i < charSize; i++) {
					for(int j=0; j < charSize; j++) {
						int px = charX * charSize + i;
						int py = charY * charSize + j;
						
						int color = font.getRGB(px, py);
						int alpha = (color >> 24) & 0xFF;
						
						if(alpha > 0) {
							width = Math.max(width, i);
						}
					}
				}
				charWidths[charIndex] = (byte) (width + 2);
			}
		}
	}
	
	public void drawString(String string, int x, int y) {
		drawString(string, x, y, 0xFFFFFF, true);
	}
	
	public void drawCenteredString(String string, int x, int y, int color, boolean shadow) {
		int width = getStringWidth(string);
		
		drawString(string, x - width / 2, y, color, shadow);
	}
	
	public void drawString(String string, int x, int y, int color, boolean shadow) {
		if(shadow) {
			drawString(string, x + 1, y + 1, Utils.multiplyRGB(color, 0.25));
		}
		drawString(string, x, y, color);
	}
	
	public void drawString(String string, int x, int y, int color) {
		renderer.setColor(color);
		
		final int initialX = x;
		
		for(int i=0; i < string.length(); i++) {
			char c = string.charAt(i);
			if(c == '\n') {
				x = initialX;
				y += 8;
				continue;
			}
			if(c == ' ') {
				x += getCharacterWidth(c);
				continue;
			}
			
			drawCharacter(c, x, y);
			x += getCharacterWidth(c);
		}
		
		renderer.setColor(0xFFFFFF);
	}
	
	public int getStringWidth(String string) {
		int width = 0;
		
		for(int i=0; i < string.length(); i++) {
			char c = string.charAt(i);
			
			if(c == '\n') {
				width = 0;
				continue;
			}
			
			width += getCharacterWidth(c);
		}
		
		return width;
	}
	
	public void drawCharacter(char c, int x, int y) {
		int ic = c;
		
		ic = Math.min(ic, 255);
		
		int cx = ic & 0xF;
		int cy = ic >> 4;
		
		renderer.drawSubImage(Textures.font, x, y, 8, 8, cx * 8, cy * 8);
	}
	
	public int getCharacterWidth(char c) {
		return charWidths[Math.min((int) c, charWidths.length - 1)];
	}
	
}
