package b100.installer.gui.modern;

import java.awt.event.KeyEvent;

public enum FocusDirection {
	
	NEXT_ELEMENT(true, true, false, false),
	PREV_ELEMENT(false, true, false, false),
	UP(false, false, false, true),
	DOWN(true, false, false, true),
	LEFT(false, false, false, true),
	RIGHT(true, false, false, true),
	HOME(false, false, true, false),
	END(true, false, true, false);
	
	private boolean forwards;
	private boolean tab;
	private boolean listNavigation;
	private boolean isArrowKey;
	
	private FocusDirection(boolean forwards, boolean tab, boolean listNavigation, boolean isArrowKey) {
		this.forwards = forwards;
		this.tab = tab;
		this.listNavigation = listNavigation;
		this.isArrowKey = isArrowKey;
	}
	
	public boolean isForwards() {
		return forwards;
	}
	
	public boolean isTab() {
		return tab;
	}
	
	public boolean isListNavigation() {
		return listNavigation;
	}
	
	public boolean isArrowKey() {
		return isArrowKey;
	}
	
	public static FocusDirection get(int keyCode) {
		boolean shift = InstallerGuiModern.getInstance().isShiftPressed();
		
		if(keyCode == KeyEvent.VK_TAB) return shift ? PREV_ELEMENT : NEXT_ELEMENT;
		if(keyCode == KeyEvent.VK_UP) return UP;
		if(keyCode == KeyEvent.VK_DOWN) return DOWN;
		if(keyCode == KeyEvent.VK_LEFT) return LEFT;
		if(keyCode == KeyEvent.VK_RIGHT) return RIGHT;
		if(keyCode == KeyEvent.VK_HOME) return HOME;
		if(keyCode == KeyEvent.VK_END) return END;
		
		return null;
	}
	
}
