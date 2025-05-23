package b100.installer.gui.modern.element;

import java.awt.image.BufferedImage;

public class GuiImageElement extends GuiElement {

	protected BufferedImage image;
	
	public GuiImageElement() {
		
	}
	
	public GuiImageElement(BufferedImage image) {
		setImage(image);
	}
	
	@Override
	public void draw() {
		if(image != null) {
			renderer.drawImage(image, posX, posY);	
		}
	}
	
	public GuiImageElement setImage(BufferedImage image) {
		this.image = image;
		if(image != null) {
			setSize(image.getWidth(), image.getHeight());	
		}
		return this;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
}
