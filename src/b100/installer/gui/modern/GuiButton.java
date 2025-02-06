package b100.installer.gui.modern;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class GuiButton extends GuiElement implements Focusable {
	
	/**
	 * The screen that is button is in
	 */
	public GuiScreen screen;
	
	/**
	 * The text of this button. Can be null.
	 */
	public String text;
	
	/**
	 * Should the button be clickable or not. When it is not clickable, its still visible but grayed out
	 */
	private boolean clickable = true;
	
	/**
	 * When the button is focused it can be clicked with Space and Enter
	 */
	private boolean focused = false;
	
	public final ListenerList<ActionListener> actionListeners = new ListenerList<>(this);
	public final ListenerList<FocusListener> focusListeners = new ListenerList<>(this);
	
	private int state = 1;
	
	public GuiButton(GuiScreen screen, String text) {
		this.screen = screen;
		this.text = text;
		
		this.width = 200;
		this.height = 20;
	}
	
	@Override
	public void tick() {
		int newState;
		
		if(clickable) {
			if(focused || screen.isMouseOver(this)) {
				newState = 2;
			}else {
				newState = 1;
			}
		}else {
			newState = 0;
		}
		
		if(state != newState) {
			state = newState;
			
			ModernInstallerGUI.getInstance().scheduleRepaint();
		}
	}
	
	@Override
	public void draw() {
		BufferedImage texture;
		
		int fontColor = 0xFFFFFF;
		
		if(state == 2) {
			texture = Textures.button_hover;
			fontColor = 0xFFFF80;
		}else if(state == 1) {
			texture = Textures.button;
		}else {
			texture = Textures.button_disabled;
		}
		
		// TODO
		renderer.drawImage(texture, posX, posY);
		
		renderer.setColor(0xff0000);
		
		if(text != null) {
			int textWidth = fontRenderer.getStringWidth(text);
			int textX = posX + (width - textWidth) / 2;
			int textY = posY + height / 2 - 4;
			fontRenderer.drawString(text, textX, textY, fontColor, true);
		}
	}
	
	@Override
	public boolean keyEvent(int key, boolean pressed) {
		if(pressed && focused && (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_SPACE)) {
			clickButton();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean mouseEvent(int button, boolean pressed, double mouseX, double mouseY) {
		if(clickable && pressed && screen.isMouseOver(this)) {
			clickButton();
			return true;
		}
		
		return super.mouseEvent(button, pressed, mouseX, mouseY);
	}
	
	public void clickButton() {
		actionListeners.forEach((listener) -> listener.actionPerformed(this));
	}
	
	public void setClickable(boolean clickable) {
		this.clickable = clickable;
	}
	
	public boolean isClickable() {
		return clickable;
	}
	
	@Override
	public void setFocused(boolean focused) {
		if(focused != this.focused) {
			this.focused = focused;
			focusListeners.forEach((listener) -> listener.focusChanged(this));
		}
	}

	@Override
	public boolean isFocused() {
		return focused;
	}

	@Override
	public boolean isFocusable() {
		return clickable;
	}

	@Override
	public ListenerList<FocusListener> getFocusListeners() {
		return focusListeners;
	}
	
	public ListenerList<ActionListener> getActionListeners() {
		return actionListeners;
	}
	
	public GuiButton addActionListener(ActionListener actionListener) {
		actionListeners.add(actionListener);
		return this;
	}
	
	public boolean removeActionListener(ActionListener actionListener) {
		return actionListeners.remove(actionListener);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[x=" + posX + ",y=" + posY + ",w=" + width + ",h=" + height + ",text=" + text + "]";
	}

}
