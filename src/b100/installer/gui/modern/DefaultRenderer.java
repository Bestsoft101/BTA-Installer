package b100.installer.gui.modern;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import b100.installer.Utils;

public class DefaultRenderer extends Renderer {
	
	private Graphics g;
	
	private int scale = 1;
	
	private int scaledWidth;
	private int scaledHeight;
	
	private int color;
	
	private Map<BufferedImage, Map<Integer, BufferedImage>> tintedImageCache = new HashMap<>();
	
	public void update(Component component, Graphics graphics) {
		if(graphics == null) {
			throw new NullPointerException("Graphics is null!");
		}
		g = graphics;
		
		int width = component.getWidth();
		int height = component.getHeight();
		
		scale = Math.max(1, Math.min(width / 320, height / 240));
		scaledWidth = Math.max(1, Utils.ceilDiv(width, scale));
		scaledHeight = Math.max(1, Utils.ceilDiv(height, scale));
	}

	@Override
	public int getWidth() {
		return scaledWidth;
	}

	@Override
	public int getHeight() {
		return scaledHeight;
	}
	
	public int getScale() {
		return scale;
	}

	@Override
	public void drawImage(BufferedImage image, int x, int y) {
		if(image == null) {
			image = Textures.missingtex;
		}
		
		drawImage(image, x, y, image.getWidth(), image.getHeight());
	}

	@Override
	public void drawImage(BufferedImage image, int x, int y, int w, int h) {
		if(image == null) {
			image = Textures.missingtex;
		}
		image = getTintedImage(image, color);
		
		int x1 = x * scale;
		int y1 = y * scale;
		int w1 = w * scale;
		int h1 = h * scale;
		
		g.drawImage(image, x1, y1, w1, h1, null);
	}

	@Override
	public void drawSubImage(BufferedImage image, int x, int y, int w, int h, int sx, int sy) {
		if(image == null) {
			image = Textures.missingtex;
		}
		image = getTintedImage(image, color);
		
		g.drawImage(image, x * scale, y * scale, (x + w) * scale, (y + h) * scale, sx, sy, sx + w, sy + h, null);
	}

	@Override
	public void drawRectangle(int x, int y, int w, int h) {
		g.fillRect(x * scale, y * scale, w * scale, h * scale);
	}

	@Override
	public void setColor(int color) {
		this.color = color;
		
		int red = (color >> 16) & 0xFF;
		int green = (color >> 8) & 0xFF;
		int blue = color & 0xFF;
		
		g.setColor(new Color(red, green, blue));
	}
	
	///////////////////////////////
	
	public static BufferedImage loadTexture(String path) {
		InputStream in = null;
		try {
			in = DefaultRenderer.class.getResourceAsStream(path);
			return ImageIO.read(in);
		}catch (Exception e) {
			System.out.println("Could not load texture: " + path);
			return Textures.missingtex;
		}finally {
			try {
				in.close();
			}catch (Exception e) {}
		}
	}
	
	private BufferedImage getTintedImage(BufferedImage image, int colorMultiplier) {
		colorMultiplier |= 0xFF000000;
		if(colorMultiplier == 0xFFFFFFFF) {
			return image;
		}
		
		Map<Integer, BufferedImage> imageColors = tintedImageCache.get(image);
		if(imageColors == null) {
			imageColors = new HashMap<>();
			tintedImageCache.put(image, imageColors);
		}
		BufferedImage coloredImage = imageColors.get(colorMultiplier);
		
		if(coloredImage == null) {
			System.out.println("Creating tinted image: " + image.getWidth() + " x " + image.getHeight() + " : " + Integer.toString(colorMultiplier & 0xFFFFFF, 16));
			
			coloredImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
			imageColors.put(colorMultiplier, coloredImage);
			
			float rmul = ((colorMultiplier >> 16) & 0xFF) / 255.0f;
			float gmul = ((colorMultiplier >>  8) & 0xFF) / 255.0f;
			float bmul = ((colorMultiplier >>  0) & 0xFF) / 255.0f;
			
			for(int x=0; x < image.getWidth(); x++) {
				for(int y=0; y < image.getHeight(); y++) {
					int rgb = image.getRGB(x, y);
					
					int a = (rgb >> 24) & 0xFF;
					int r = (rgb >> 16) & 0xFF;
					int g = (rgb >>  8) & 0xFF;
					int b = (rgb >>  0) & 0xFF;
					
					r = Utils.clamp((int) (r * rmul), 0, 255);
					g = Utils.clamp((int) (g * gmul), 0, 255);
					b = Utils.clamp((int) (b * bmul), 0, 255);
					
					coloredImage.setRGB(x, y, a << 24 | r << 16 | g << 8 | b);
				}
			}
			
		}
		
		return coloredImage;
	}
	
}
