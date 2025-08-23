package b100.installer.gui.modern.render;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import b100.installer.util.Utils;

public class Textures {

	public static BufferedImage missingtex = createMissingTexture();
	public static BufferedImage background = scale(loadTexture("background"), 2);
	public static BufferedImage button = loadTexture("button");
	public static BufferedImage button_disabled = loadTexture("button_disabled");
	public static BufferedImage button_hover = loadTexture("button_hover");
	public static BufferedImage font = loadTexture("font");
	public static BufferedImage logo = loadTexture("logo1");
	public static BufferedImage checkmark = loadTexture("checkmark");
	public static BufferedImage icons = loadTexture("icons");
	public static BufferedImage shadow_1 = createGradientImage(1, 6, 0x00000000, 0x80000000, false);
	public static BufferedImage shadow_2 = createGradientImage(1, 6, 0x80000000, 0x00000000, false);
	public static BufferedImage dialogBackground = createColorImage(0xC8101010);
	
	static {
		System.out.println("Loaded Textures!");
	}
	
	private static BufferedImage loadTexture(String name) {
		BufferedImage tex = DefaultRenderer.loadTexture("/resources/images/" + name + ".png");
		
		BufferedImage tex1 = new BufferedImage(tex.getWidth(), tex.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = tex1.getGraphics();
		g.drawImage(tex, 0, 0, null);
		g.dispose();
		
		return tex1;
	}
	
	private static BufferedImage createMissingTexture() {
		BufferedImage missingTex = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
		Graphics g = missingTex.getGraphics();
		
		g.setColor(Color.white);
		g.fillRect(0, 0, missingTex.getWidth(), missingTex.getHeight());
		
		g.setColor(Color.black);
		g.drawString("missingtex", 0, 12);
		
		g.dispose();
		return missingTex;
	}
	
	private static BufferedImage createColorImage(int color) {
		return createColorImage(1, 1, color);
	}
	
	private static BufferedImage createColorImage(int width, int height, int color) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		for(int i=0; i < image.getWidth(); i++) {
			for(int j=0; j < image.getHeight(); j++) {
				image.setRGB(i, j, color);
			}
		}
		
		return image;
	}
	
	private static BufferedImage createGradientImage(int width, int height, int color1, int color2, boolean horizontal) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		for(int i=0; i < image.getWidth(); i++) {
			for(int j=0; j < image.getHeight(); j++) {
				float factor;
				if(horizontal) {
					factor = i / ((float) (image.getWidth() - 1));
				}else {
					factor = j / ((float) (image.getHeight() - 1));	
				}
				
				image.setRGB(i, j, Utils.mixARGB(color1, color2, factor));
			}
		}
		
		return image;
	}
	
	private static BufferedImage scale(BufferedImage image, int scale) {
		BufferedImage scaledImage = new BufferedImage(image.getWidth() * scale, image.getHeight() * scale, image.getType());
		
		Graphics g = scaledImage.getGraphics();
		g.drawImage(image, 0, 0, image.getWidth() * scale, image.getHeight() * scale, null);
		g.dispose();
		
		return scaledImage;
	}
	
}
