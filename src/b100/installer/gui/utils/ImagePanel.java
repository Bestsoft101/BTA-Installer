package b100.installer.gui.utils;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ImagePanel extends JPanel {
	
	public BufferedImage image;
	
	public ImagePanel(BufferedImage image) {
		setImage(image);
	}
	
	public void setImage(BufferedImage image) {
		this.image = image;
		
		setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		int x = (getWidth() - image.getWidth()) / 2;
		int y = (getHeight() - image.getHeight()) / 2;
		
		g.drawImage(image, x, y, null);
	}
	
}