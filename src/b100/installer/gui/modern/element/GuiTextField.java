package b100.installer.gui.modern.element;

import java.awt.event.KeyEvent;

import b100.installer.gui.modern.InstallerGuiModern;
import b100.installer.gui.modern.screen.GuiScreen;
import b100.installer.gui.modern.util.FocusListener;
import b100.installer.gui.modern.util.Focusable;
import b100.installer.gui.modern.util.ListenerList;
import b100.installer.util.Utils;

public class GuiTextField extends GuiElement implements Focusable {
	
	public GuiScreen screen;
	
	protected String text = "";
	public int textColor = 0xFFFFFF;
	
	public int cursorPosition;
	public int selection = -1;
	public long clickTime;
	public boolean focused;
	
	public final ListenerList<FocusListener> focusListeners = new ListenerList<FocusListener>(this);
	
	protected boolean cursorVisible;
	
	public GuiTextField(GuiScreen screen) {
		this.screen = screen;
		
		setSize(200, 18);
	}
	
	@Override
	public void tick() {
		int blinkTime = 600;
		boolean blink = (System.currentTimeMillis() - clickTime) % blinkTime < (blinkTime / 2);
		if(blink != cursorVisible) {
			cursorVisible = blink;
			InstallerGuiModern.getInstance().scheduleRepaint();
		}
		super.tick();
	}
	
	@Override
	public void draw() {
		renderer.setColor(focused ? 0xFFFFFF : 0x808080);
		renderer.drawRectangle(posX - 1, posY - 1, width + 2, height + 2);
		renderer.setColor(0x000000);
		renderer.drawRectangle(posX, posY, width, height);
		renderer.resetColor();
		
		int x = posX + 4;
		int y = posY + height / 2 - 4;
		
		cursorPosition = Utils.clampi(cursorPosition, 0, text.length());
		
		if(isTextSelected()) {
			int selectionStart = getSelectionStart();
			int selectionEnd = getSelectionEnd();
			
			String str1 = text.substring(0, selectionStart);
			String str2 = text.substring(selectionStart, selectionEnd);
			String str3 = text.substring(selectionEnd, text.length());
			
			int w1 = fontRenderer.getStringWidth(str1);
			int w2 = fontRenderer.getStringWidth(str2);

			int x1 = x + w1;
			int w = fontRenderer.getStringWidth(text.substring(selectionStart, selectionEnd));
			
			fontRenderer.drawString(str1, x, y, textColor);
			fontRenderer.drawString(str2, x + w1, y, 0xFFFF00);
			fontRenderer.drawString(str3, x + w1 + w2, y, textColor);
			
			renderer.setInvertColorBlendMode();
			renderer.setColor(0xFFFFFF);
			renderer.drawRectangle(x1, y - 1, w, 10);
			renderer.resetBlendMode();
		}else {
			fontRenderer.drawString(text, x, y, textColor);
		}
		
		//Draw Cursor
		if(cursorPosition == text.length()) {
			if(focused && cursorVisible) {
				int w = fontRenderer.getStringWidth(text);
				
				fontRenderer.drawString("_", x + w, y, textColor);
			}
		}else {
			if(focused && cursorVisible) {
				int w = fontRenderer.getStringWidth(text.substring(0, cursorPosition));
				
				renderer.drawRectangle(x + w - 1, y - 1, 1, 10);
			}
		}
	}
	
	@Override
	public boolean mouseEvent(int button, boolean pressed, double mouseX, double mouseY) {
		if(pressed) {
			if(screen.isMouseOver(this)) {
				if(button == 3) {
					setText("");
					onTextOrCursorChanged();
				}
				if(!isFocused()) {
					setFocused(true);
				}
				return true;
			}else {
				if(isFocused()) {
					setFocused(false);	
				}
			}
		}
		
		return super.mouseEvent(button, pressed, mouseX, mouseY);
	}
	
	@Override
	public boolean keyEvent(int key, boolean pressed) {
		if(!focused) {
			return false;
		}
		if(pressed) {
			if(editKeyEvent(key)) {
				return true;
			}
		}
		return super.keyEvent(key, pressed);
	}
	
	@Override
	public boolean charEvent(char c) {
		if(!focused) {
			return false;
		}
		if(editCharEvent(c)) {
			return true;
		}
		return super.charEvent(c);
	}
	
	protected boolean editKeyEvent(int key) {
		boolean ctrl = InstallerGuiModern.getInstance().isCtrlPressed();
		boolean shift = InstallerGuiModern.getInstance().isShiftPressed();
		
		if(key == KeyEvent.VK_BACK_SPACE) {
			if(isTextSelected()) {
				int selStart = getSelectionStart();
				int selEnd = getSelectionEnd();
				
				text = text.substring(0, selStart) + text.substring(selEnd);
				cursorPosition = selStart;
			}else if(cursorPosition > 0) {
				text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
				cursorPosition--;	
			}
			onTextOrCursorChanged();
			return true;
		}
		
		// Navigation
		if(key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_HOME || key == KeyEvent.VK_END) {
			if(shift && selection == -1) {
				selection = cursorPosition;
			}
			if(!shift) {
				selection = -1;
			}
			
			if(ctrl) {
				if(key == KeyEvent.VK_LEFT) cursorPosition = getNextWordIndex(-1);
				if(key == KeyEvent.VK_RIGHT) cursorPosition = getNextWordIndex(1);
			}else {
				if(key == KeyEvent.VK_LEFT) cursorPosition--;
				if(key == KeyEvent.VK_RIGHT) cursorPosition++;	
			}
			if(key == KeyEvent.VK_HOME) cursorPosition = 0;
			if(key == KeyEvent.VK_END) cursorPosition = text.length();
			
			onTextOrCursorChanged();
			
			return true;
		}
		
		if(ctrl) {
			if(key == KeyEvent.VK_A) {
				if(text.length() > 0) {
					selection = 0;
					cursorPosition = text.length();
					onTextOrCursorChanged();
				}
				return true;
			}
			if(key == KeyEvent.VK_V) {
				String clipboard = removeInvalidCharacters(Utils.getClipboardString());
				if(clipboard != null && clipboard.length() > 0) {
					if(selection != -1) {
						int selStart = getSelectionStart();
						int selEnd = getSelectionEnd();
						
						text = text.substring(0, selStart) + clipboard + text.substring(selEnd);
						cursorPosition = selStart + clipboard.length();
					}else {
						text = text.substring(0, cursorPosition) + clipboard + text.substring(cursorPosition);
						cursorPosition += clipboard.length();
					}
					selection = -1;
					onTextOrCursorChanged();
				}
				return true;
			}
			if(key == KeyEvent.VK_C) {
				if(isTextSelected()) {
					Utils.copyString(text.substring(getSelectionStart(), getSelectionEnd()));
					
					return true;
				}
			}
		}
		return false;
	}
	
	private int getNextWordIndex(int dir) {
		dir = Utils.clampi(dir, -1, 1);
		if(dir == 0) return cursorPosition;
		
		for(int i = cursorPosition + dir; i > 0 && i < text.length(); i += dir) {
			if(text.charAt(i) != ' ' && text.charAt(i - 1) == ' ') return i;
		}
		
		return dir > 0 ? text.length() : 0;
	}
	
	private String removeInvalidCharacters(String string) {
		if(string == null) {
			return null;
		}
		StringBuilder str = new StringBuilder();
		for(int i=0; i < string.length(); i++) {
			char c = string.charAt(i);
			if(isCharacterAllowed(c)) {
				str.append(c);
			}
		}
		return str.toString();
	}
	
	private boolean isCharacterAllowed(char c) {
		return c != '\n' && c != 0;
	}
	
	protected boolean editCharEvent(char c) {
		// Character Typed
		
		if(isCharacterAllowed(c)) {
			if(isTextSelected()) {
				int selStart = getSelectionStart();
				int selEnd = getSelectionEnd();
				text = text.substring(0, selStart) + c + text.substring(selEnd);
				selection = -1;
				cursorPosition = selStart + 1;
			}else {
				text = text.substring(0, cursorPosition) + c + text.substring(cursorPosition);
				cursorPosition++;	
			}
			
			onTextOrCursorChanged();
		}
		
		return true;
	}
	
	protected void onTextOrCursorChanged() {
		clickTime = System.currentTimeMillis();
		
		if(text.length() == 0) {
			selection = -1;
			cursorPosition = 0;
		}
		
		InstallerGuiModern.getInstance().scheduleRepaint();
	}
	
	@Override
	public void setFocused(boolean focused) {
		if(focused != this.focused) {
			this.focused = focused;

			InstallerGuiModern.getInstance().scheduleRepaint();
			
			if(focused) {
				if(clickTime == 0) {
					clickTime = System.currentTimeMillis();
				}
			}else {
				this.clickTime = 0;
			}
			
			focusListeners.forEach((e) -> e.focusChanged(this));
		}
	}
	
	public void setText(String text) {
		if(text == null) {
			text = "";
		}
		this.text = text;
		
		cursorPosition = text.length();
		selection = -1;
	}
	
	public String getText() {
		return text;
	}

	@Override
	public boolean isFocused() {
		return focused;
	}

	@Override
	public boolean isFocusable() {
		return true;
	}

	@Override
	public ListenerList<FocusListener> getFocusListeners() {
		return focusListeners;
	}
	
	public boolean isTextSelected() {
		return selection != -1;
	}
	
	public int getSelectionStart() {
		if(!isTextSelected()) return -1;
		return Math.min(selection, cursorPosition);
	}
	
	public int getSelectionEnd() {
		if(!isTextSelected()) return -1;
		return Math.max(selection, cursorPosition);
	}
	
}
