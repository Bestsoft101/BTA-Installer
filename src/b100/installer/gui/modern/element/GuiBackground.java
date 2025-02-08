package b100.installer.gui.modern.element;

import b100.installer.gui.modern.render.Textures;

public class GuiBackground extends GuiElement {

	public GuiElement parent;
	public int color;
	
	public GuiBackground(GuiElement screen) {
		this(screen, 0x404040);
	}
	
	public GuiBackground(GuiElement screen, int color) {
		this.parent = screen;
		this.color = color;
	}
	
	@Override
	public void draw() {
		if(parent != null) {
			this.posX = parent.posX;
			this.posY = parent.posY;
			this.width = parent.width;
			this.height = parent.height;	
		}
		
		int tileSize = 32;
		
		int tilesX = ceilDiv(width, tileSize);
		int tilesY = ceilDiv(height, tileSize);
		
		renderer.setColor(color);
		
		for(int i=0; i < tilesX; i++) {
			for(int j=0; j < tilesY; j++) {
				renderer.drawImage(Textures.background, posX + i * tileSize, posY + j * tileSize, tileSize, tileSize);
			}
		}
		
		renderer.setColor(0xFFFFFF);
	}
	
	public static int ceilDiv(int a, int b) {
		return (int) Math.ceil(a / (double) b);
	}
	
}
