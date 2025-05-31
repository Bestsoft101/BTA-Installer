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
			posX = parent.posX;
			width = parent.width;
			if(position == Position.ABOVE_ELEMENT) {
				posY = parent.posY - image.getHeight();
			}else if(position == Position.BELOW_ELEMENT) {
				posY = parent.posY + parent.height;
			}else if(position == Position.IN_ELEMENT_TOP) {
				posY = parent.posY;
			}else if(position == Position.IN_ELEMENT_BOTTOM) {
				posY = parent.posY + parent.height - image.getHeight();
			}
			height = image.getHeight();
		}
		
		renderer.drawImageStretched(image, posX, posY, width, height);
	}
	
	@Override
	public boolean isSolid() {
		return false;
	}
	
	public static enum Position {
		ABOVE_ELEMENT, BELOW_ELEMENT, IN_ELEMENT_TOP, IN_ELEMENT_BOTTOM;
	}
}
