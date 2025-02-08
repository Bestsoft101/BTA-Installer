package b100.installer.gui.modern;

import java.awt.event.KeyEvent;

import b100.installer.Utils;

public class GuiCheckbox extends GuiElement implements Focusable {
	
	/**
	 * The screen that this checkbox is in
	 */
	public GuiScreen screen;

	/**
	 * The text of this checkbox. Can be null.
	 */
	public String text;

	/**
	 * Should the checkbox be clickable or not. When it is not clickable, its still visible but grayed out
	 */
	private boolean clickable = true;

	/**
	 * When the checkbox is focused it can be clicked with Space and Enter
	 */
	private boolean focused = false;

	private boolean checked = false;

	public final ListenerList<ActionListener> actionListeners = new ListenerList<>(this);
	public final ListenerList<FocusListener> focusListeners = new ListenerList<>(this);

	private int previousState = 1;
	
	public GuiCheckbox(GuiScreen screen, String text) {
		this(screen, text, false);
	}
	
	public GuiCheckbox(GuiScreen screen, String text, boolean checked) {
		this.screen = screen;
		this.text = text;
		this.checked = checked;
		
		this.width = 100;
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
		
		if(previousState != newState) {
			previousState = newState;
			
			InstallerGuiModern.getInstance().scheduleRepaint();
		}
	}
	
	@Override
	public void draw() {
		int buttonSize = 20;
		int border = (height - buttonSize) / 2;
		
		// Top left corner of the button
		int x1 = posX + border;
		int y1 = posY + border;
		
		renderer.drawSubImage(Textures.button_disabled, x1, y1, 10, height, 0, 0);
		renderer.drawSubImage(Textures.button_disabled, x1 + 10, y1, 10, height, Textures.button_disabled.getWidth() - 10, 0);	
		
		// Center of the button
		int x2 = x1 + buttonSize / 2;
		int y2 = y1 + buttonSize / 2;
		
		if(checked) {
			int x3 = x2 - Textures.checkmark.getWidth() / 2;
			int y3 = y2 - Textures.checkmark.getHeight() / 2;
			
			renderer.drawImage(Textures.checkmark, x3, y3);
		}
		
		if(text != null) {
			int fontColor;
			
			if(previousState == 2) {
				fontColor = 0xFFFF80;
			}else if(previousState == 1) {
				fontColor = 0xFFFFFF;
			}else {
				fontColor = 0x808080;
			}
			
			int x4 = posX + border + buttonSize + 4;
			int y4 = posY + height / 2 - 4;
			
			fontRenderer.drawString(text, x4, y4, fontColor, true);
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
		checked = !checked;
		
		Utils.click();
		
		actionListeners.forEach((listener) -> listener.actionPerformed(this));
		
		InstallerGuiModern.getInstance().scheduleRepaint();
	}
	
	public boolean isChecked() {
		return checked;
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
	
	public GuiCheckbox addActionListener(ActionListener actionListener) {
		actionListeners.add(actionListener);
		return this;
	}
	
	public boolean removeActionListener(ActionListener actionListener) {
		return actionListeners.remove(actionListener);
	}
	
}
