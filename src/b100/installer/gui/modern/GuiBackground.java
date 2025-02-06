package b100.installer.gui.modern;

public class GuiBackground extends GuiElement {

	public GuiElement parent;
	
	public GuiBackground(GuiElement screen) {
		this.parent = screen;
	}
	
	@Override
	public void draw() {
		this.posX = parent.posX;
		this.posY = parent.posY;
		this.width = parent.width;
		this.height = parent.height;
		
		int tileSize = 32;
		
		int tilesX = ceilDiv(width, tileSize);
		int tilesY = ceilDiv(height, tileSize);
		
		renderer.setColor(0x404040);
		
		for(int i=0; i < tilesX; i++) {
			for(int j=0; j < tilesY; j++) {
				renderer.drawImage(Textures.background, i * tileSize, j * tileSize, tileSize, tileSize);
			}
		}
		
		renderer.setColor(0xFFFFFF);
	}
	
	public static int ceilDiv(int a, int b) {
		return (int) Math.ceil(a / (double) b);
	}
	
}
