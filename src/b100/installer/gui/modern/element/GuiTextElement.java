package b100.installer.gui.modern.element;

import java.util.List;

import b100.installer.util.Utils;

public class GuiTextElement extends GuiElement {

	protected List<String> lines;
	
	public double alignX;
	public double alignY;
	
	protected int textWidth;
	protected int textHeight;
	
	public GuiTextElement(String text, double alignX, double alignY) {
		setSize(8, 8);
		setText(text);
		
		this.alignX = alignX;
		this.alignY = alignY;
	}
	
	@Override
	public void draw() {
		int textPosX = (int) (posX + (width - textWidth) * alignX);
		int textPosY = (int) (posY + (width - textWidth) * alignY);
		
		for(int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
			String line = lines.get(lineNumber);
			
			fontRenderer.drawString(line, textPosX, textPosY + lineNumber * 9);
		}
	}
	
	public void setAlignX(double alignX) {
		this.alignX = alignX;
	}
	
	public void setAlignY(double alignY) {
		this.alignY = alignY;
	}
	
	public void setText(String text) {
		this.lines = Utils.splitLines(text);

		int maxLineWidth = 0;
		for(String line : lines) {
			maxLineWidth = Math.max(maxLineWidth, fontRenderer.getStringWidth(line));
		}
		
		textWidth = maxLineWidth;
		textHeight = lines.size() * 8 + lines.size() - 1;
		
		width = Math.max(width, textWidth);
		height = Math.max(height, textHeight);
	}
	
	public double getAlignX() {
		return alignX;
	}
	
	public double getAlignY() {
		return alignY;
	}
}
