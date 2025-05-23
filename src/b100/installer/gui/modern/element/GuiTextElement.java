package b100.installer.gui.modern.element;

import java.util.List;

import b100.installer.util.Utils;

public class GuiTextElement extends GuiElement {

	protected List<String> lines;
	
	protected double alignX = 0.0;
	protected double alignY = 0.0;
	
	protected int textWidth;
	protected int textHeight;
	
	protected int textColor = 0xFFFFFF;
	
	protected boolean autoSize = false;
	
	public GuiTextElement() {
		setSize(8, 8);
	}
	
	public GuiTextElement(String text, double alignX, double alignY) {
		this();
		setAlign(alignX, alignY);
		setText(text);
	}
	
	@Override
	public void draw() {
		if(lines == null) {
			return;
		}

		if(autoSize) {
			width = textWidth;
			height = textHeight;	
		}
		
		int textPosX = (int) (posX + (width - textWidth) * alignX);
		int textPosY = (int) (posY + (width - textWidth) * alignY);
		
		for(int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
			String line = lines.get(lineNumber);
			
			fontRenderer.drawString(line, textPosX, textPosY + lineNumber * 9, textColor);
		}
	}
	
	public void setAlignX(double alignX) {
		this.alignX = alignX;
	}
	
	public void setAlignY(double alignY) {
		this.alignY = alignY;
	}
	
	public GuiTextElement setAlign(double alignX, double alignY) {
		this.alignX = alignX;
		this.alignY = alignY;
		return this;
	}
	
	public GuiTextElement setText(String text) {
		if(text == null) {
			this.lines = null;
			return this;
		}
		
		this.lines = Utils.splitLines(text);

		int maxLineWidth = 0;
		for(String line : lines) {
			maxLineWidth = Math.max(maxLineWidth, fontRenderer.getStringWidth(line));
		}
		
		textWidth = maxLineWidth;
		textHeight = lines.size() * 8 + lines.size() - 1;
		
		if(autoSize) {
			width = textWidth;
			height = textHeight;
		}
		
		return this;
	}
	
	public GuiTextElement setTextColor(int textColor) {
		this.textColor = textColor;
		return this;
	}
	
	public GuiTextElement setAutoSize(boolean autoSize) {
		this.autoSize = autoSize;

		if(autoSize) {
			width = textWidth;
			height = textHeight;	
		}
		
		return this;
	}
	
	public double getAlignX() {
		return alignX;
	}
	
	public double getAlignY() {
		return alignY;
	}
	
	public int getTextColor() {
		return textColor;
	}
	
	public boolean isAutoSizeEnabled() {
		return autoSize;
	}
}
