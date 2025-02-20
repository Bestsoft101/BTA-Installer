package b100.installer.gui.modern.element;

import b100.installer.gui.modern.render.Textures;
import b100.installer.gui.modern.screen.GuiScreen;

public class GuiBackground extends GuiElement {

	public GuiElement parent;
	public int color;
	public int offset = 0;
	
	private boolean fillsScreen;
	
	public GuiBackground(GuiElement parent) {
		this(parent, 0x404040);
	}
	
	public GuiBackground(GuiElement parent, int color) {
		this.parent = parent;
		this.color = color;
		this.fillsScreen = parent instanceof GuiScreen;
	}
	
	@Override
	public void draw() {
		if(parent != null) {
			this.posX = parent.posX;
			this.posY = parent.posY;
			this.width = parent.width;
			this.height = parent.height;	
		}
		
		final int tileSize = 32;
		
		int modOffset = offset & 31;
		int y0 = posY + modOffset;
		
		int tileX0 = posX / tileSize;
		int tileX1 = (posX + width - 1) / tileSize;
		int tileY0 = y0 >> 5;
		int tileY1 = (y0 + height - 1) >> 5;
		
//		tileX0++;
//		tileY0++;
//		tileX1--;
//		tileY1--;
		
		renderer.setColor(color);
		if(!fillsScreen) {
			renderer.enableScissor(posX, posY, width, height);
		}
		
		for(int i = tileX0; i <= tileX1; i++) {
			for(int j = tileY0; j <= tileY1; j++) {
				renderer.drawImage(Textures.background, i * tileSize, j * tileSize - modOffset);
			}
		}
		
		renderer.setColor(0xFFFFFF);
		if(!fillsScreen) {
			renderer.disableScissor();	
		}
	}
	
	public static int ceilDiv(int a, int b) {
		return (int) Math.ceil(a / (double) b);
	}
	
}
