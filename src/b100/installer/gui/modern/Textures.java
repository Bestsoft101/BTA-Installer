package b100.installer.gui.modern;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Textures {

	public static BufferedImage missingtex = createMissingTexture();
	public static BufferedImage background = loadTexture("background");
	public static BufferedImage button = loadTexture("button");
	public static BufferedImage button_disabled = loadTexture("button_disabled");
	public static BufferedImage button_hover = loadTexture("button_hover");
	public static BufferedImage font = loadTexture("font");
	public static BufferedImage logo = loadTexture("logo");
	
	static {
		System.out.println("Loaded Textures!");
	}
	
	private static BufferedImage loadTexture(String name) {
		BufferedImage tex = DefaultRenderer.loadTexture("/" + name + ".png");
		
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
	
}
