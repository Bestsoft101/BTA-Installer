package b100.installer.gui.modern;

public class GuiListButton extends GuiElement implements Focusable {
	
	public GuiScreen screen;
	
	private final ListenerList<FocusListener> focusListeners = new ListenerList<>(this);
	
	public int outlineColorFocused = 0xFFFFFFFF;
	public int outlineColor = 0xFF808080;
	public int fillColor = 0xFF000000;
	
	public String text;
	
	private boolean clickable = true;
	private boolean focused = false;
	
	public GuiListButton(GuiScreen screen) {
		this.screen = screen;
		
		this.width = 200;
		this.height = 20;
	}
	
	@Override
	public void draw() {
		int highlight = getHighlightColor();
		if(highlight != 0) {
			renderer.setColor(highlight);
			renderer.drawRectangle(posX, posY, width, height);
			renderer.setColor(fillColor);
			renderer.drawRectangle(posX + 1, posY + 1, width - 2, height - 2);
		}
		
		if(text != null) {
			int textWidth = fontRenderer.getStringWidth(text);
			int textX = posX + (width - textWidth) / 2;
			int textY = posY + height / 2 - 4;
			fontRenderer.drawString(text, textX, textY, 0xFFFFFF, true);
		}
	}
	
	@Override
	public boolean mouseEvent(int button, boolean pressed, double mouseX, double mouseY) {
		if(pressed && screen.isMouseOver(this) && clickable) {
			setFocused(true);
			return true;
		}
		
		return super.mouseEvent(button, pressed, mouseX, mouseY);
	}
	
	@Override
	public void setFocused(boolean focused) {
		if(focused != this.focused) {
			this.focused = focused;
			onFocusChanged();
		}
	}
	
	public int getHighlightColor() {
		if(isFocused()) {
			return outlineColorFocused;
		}else if(getContainer().getLastFocusedElement() == this) {
			return outlineColor;
		}
		return 0;
	}
	
	public void onFocusChanged() {
		InstallerGuiModern.getInstance().scheduleRepaint();
		
		focusListeners.forEach((e) -> e.focusChanged(this));
	}
	
	@Override
	public boolean isFocused() {
		return focused;
	}
	
	@Override
	public boolean isFocusable() {
		return clickable;
	}
	
	public void setClickable(boolean clickable) {
		this.clickable = clickable;
	}
	
	public boolean isClickable() {
		return clickable;
	}
	
	@Override
	public ListenerList<FocusListener> getFocusListeners() {
		return focusListeners;
	}
	
	public GuiListButton addFocusListener(FocusListener focusListener) {
		focusListeners.add(focusListener);
		return this;
	}
	
	public boolean removeFocusListener(FocusListener focusListener) {
		return focusListeners.remove(focusListener);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[x=" + posX + ",y=" + posY + ",w=" + width + ",h=" + height + ",text=" + text + "]";
	}

}
