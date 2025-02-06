package b100.installer.gui.modern;

/**
 * All {@link GuiElement}s implementing {@link FocusListener} will automatically receive focus changes for all elements on the same screen
 */
public interface FocusListener {
	
	public void focusChanged(Focusable focusable);

}
