package b100.installer.gui.modern;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class DefaultRenderer extends Renderer {
	
	private Graphics g;
	private int scale = 1;
	private int scaledWidth;
	private int scaledHeight;
	private int color;
	
	public void update(Component component, Graphics graphics) {
		if(graphics == null) {
			throw new NullPointerException("Graphics is null!");
		}
		g = graphics;
		
		int width = component.getWidth();
		int height = component.getHeight();
		
		scale = Math.max(1, Math.min(width / 320, height / 240));
		scaledWidth = Math.max(1, width / scale);
		scaledHeight = Math.max(1, height / scale);
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
	
	public static int multiplyRGB(int color, double mul) {
		int a = (color >> 24) & 0xFF;
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color >> 0) & 0xFF;

		r = (int) (r * mul);
		g = (int) (g * mul);
		b = (int) (b * mul);

		a = clamp(a, 0, 255);
		r = clamp(r, 0, 255);
		g = clamp(g, 0, 255);
		b = clamp(b, 0, 255);
		
		return (a << 24) | (r << 16) | (g << 8) | b;
	}
	
	public static int clamp(int val, int min, int max) {
		if(val < min) return min;
		if(val > max) return max;
		return val;
	}

	@Override
	public void drawSubImage(BufferedImage image, int x, int y, int w, int h, int sx, int sy) {
		if(image == null) {
			image = Textures.missingtex;
		}
		image = getTintedImage(image, color);
		
		g.drawImage(image, x * scale, y * scale, (x + w) * scale, (y + h) * scale, sx, sy, sx + w, sy + h, null);
	}
	
	private Map<BufferedImage, Map<Integer, BufferedImage>> coloredImageCache = new HashMap<>();
	
	private BufferedImage getTintedImage(BufferedImage image, int colorMultiplier) {
		colorMultiplier |= 0xFF000000;
		if(colorMultiplier == 0xFFFFFFFF) {
			return image;
		}
		
		Map<Integer, BufferedImage> imageColors = coloredImageCache.get(image);
		if(imageColors == null) {
			imageColors = new HashMap<>();
			coloredImageCache.put(image, imageColors);
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
					
					r = clamp((int) (r * rmul), 0, 255);
					g = clamp((int) (g * gmul), 0, 255);
					b = clamp((int) (b * bmul), 0, 255);
					
					coloredImage.setRGB(x, y, a << 24 | r << 16 | g << 8 | b);
				}
			}
			
		}
		
		return coloredImage;
	}

	@Override
	public void setColor(int color) {
		this.color = color;
	}
	
//	private void draw(Raster src, Raster dst, WritableRaster out) {
//		for(int x=0; x < src.getWidth(); x++) {
//			for(int y=0; y < src.getHeight(); y++) {
//				int alpha = src.getSample(x, y, 3);
//				if(alpha > 0) {
//					int r = src.getSample(x, y, 0);
//					int g = src.getSample(x, y, 1);
//					int b = src.getSample(x, y, 2);
//					
//					r *= rmul;
//					g *= gmul;
//					b *= bmul;
//					
//					out.setSample(x, y, 0, r);
//					out.setSample(x, y, 1, g);
//					out.setSample(x, y, 2, b);
//				}
//			}
//		}
//	}
//	
//	public class CustomComposite implements Composite {
//
//		@Override
//		public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
//			return new CustomCompositeContext();
//		}
//	}
//	
//	public class CustomCompositeContext implements CompositeContext {
//
//		@Override
//		public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
//			draw(src, dstIn, dstOut);
//		}
//
//		@Override
//		public void dispose() {
//			
//		}
//	}
	
}
