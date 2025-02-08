package b100.installer.gui.modern.element;

import java.awt.image.BufferedImage;

public class GuiElementShadow extends GuiElement {
	
	public GuiElement parent;
	public BufferedImage image;
	public Position position;
	
	public GuiElementShadow(GuiElement parent, BufferedImage image, Position position) {
		this.parent = parent;
		this.image = image;
		this.position = position;
	}
	
	@Override
	public void draw() {
		if(parent != null) {
			this.posX = parent.posX;
			this.width = parent.width;
			if(position == Position.ABOVE_ELEMENT) {
				this.posY = parent.posY - image.getHeight();
				this.height = image.getHeight();
			}else if(position == Position.BELOW_ELEMENT) {
				this.posY = parent.posY + parent.height;
				this.height = image.getHeight();	
			}
		}
		
		renderer.drawImage(image, posX, posY, width, height);
	}
	
	public static enum Position {
		
		ABOVE_ELEMENT, BELOW_ELEMENT;
		
	}
	
}
