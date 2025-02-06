package b100.installer.gui.modern;

/**
 * If a GuiElement implementing a ScreenListener is added to a GuiScreen, it will automatically be registered 
 */
public interface ScreenListener {
	
	public void onScreenOpened(GuiScreen screen);

}
