package b100.installer.gui.modern.screen;

import java.awt.event.KeyEvent;

import b100.installer.gui.modern.InstallerGuiModern;
import b100.installer.gui.modern.element.GuiContainer;
import b100.installer.gui.modern.element.GuiElement;
import b100.installer.gui.modern.util.ContainerListener;
import b100.installer.gui.modern.util.FocusDirection;
import b100.installer.gui.modern.util.FocusListener;
import b100.installer.gui.modern.util.Focusable;
import b100.installer.gui.modern.util.ListenerList;
import b100.installer.gui.modern.util.ScreenListener;

public abstract class GuiScreen extends GuiContainer implements FocusListener, ContainerListener {
	
	private boolean initialized = false;
	
	public double mouseX = -1;
	public double mouseY = -1;
	
	public GuiScreen parentScreen;
	
	/**
	 * Only one element per screen should be focused
	 */
	protected Focusable focusedElement;

	public final ListenerList<ScreenListener> screenListeners = new ListenerList<>(this);
	public final ListenerList<FocusListener> focusListeners = new ListenerList<>(this);
	
	public GuiScreen(GuiScreen parentScreen) {
		this.parentScreen = parentScreen;
		
		containerListeners.add(this);
	}
	
	public final void init() {
		if(initialized) {
			initialized = false;
			
			removeAll();
			
			width = 0;
			height = 0;
		}
		
		onInit();
		
		initialized = true;
	}
	
	/**
	 *      Called one time when the screen is first created.
	 * <br> Should only be used to create GUI elements, positioning is done in {@link GuiScreen#onResize()}
	 */
	protected abstract void onInit();
	
	/**
	 * Called once when the screen is created and every time the size of the game window changed, should be used to position GUI Elements
	 */
	@Override
	public void onResize() {
		super.onResize();
	}
	
	@Override
	public boolean keyEvent(int key, boolean pressed) {
		if(pressed && key == KeyEvent.VK_ESCAPE) {
			close();
			return true;
		}
		
		if(super.keyEvent(key, pressed)) {
			return true;
		}
		
		if(pressed && key == KeyEvent.VK_BACK_SPACE) {
			back();
			return true;
		}
		
		if(pressed) {
			FocusDirection focusDirection = FocusDirection.get(key);
			if(focusDirection != null) {
				if(focusNextElement(focusDirection)) {
					return true;
				}
			}	
		}
		
		return false;
	}
	
	public boolean focusNextElement(FocusDirection direction) {
		Focusable next = getNextScreenFocusableElement(focusedElement, direction);
		if(next != null) {
			next.setFocused(true);
			return true;
		}
		return false;
	}
	
	/**
	 * Search for the next focusable element on this screen, starting at the given element.
	 * <br> Screens with custom focus behavior should override this method!
	 */
	public Focusable getNextScreenFocusableElement(Focusable focusedElement, FocusDirection direction) {
		Focusable next;
		if(focusedElement != null) {
			next = Focusable.findNextFocusableElement((GuiElement) focusedElement, direction);
			
			if(next == null && direction.isTab()) {
				// Loop around
				next = getFirstFocusableElement(direction);
			}
		}else {
			next = getFirstFocusableElement(direction);
		}
		return next;
	}
	
	@Override
	public void elementAdded(GuiContainer parent, GuiElement element) {
		// Listen for focus changes on all added elements
		if(element instanceof Focusable) {
			Focusable focusable = (Focusable) element;
			focusable.getFocusListeners().add(this);
		}
		
		// Register listeners automatically
		if(element instanceof ScreenListener) {
			ScreenListener screenListener = (ScreenListener) element;
			screenListeners.add(screenListener);
		}
		if(element instanceof FocusListener) {
			FocusListener focusListener = (FocusListener) element;
			focusListeners.add(focusListener);
		}
		
		// Add a ContainerListener to all containers added to this screen
		// Make sure this method is called for all elements already added to the container
		if(element instanceof GuiContainer) {
			GuiContainer container = (GuiContainer) element;
			
			// Make sure elements that were already added also get a FocusListener
			container.elements.forEach((e) -> elementAdded(container, e));
			
			container.containerListeners.add(this);
		}
	}
	
	@Override
	public void focusChanged(Focusable focusable) {
		if(focusable.isFocused()) {
			if(focusedElement != null) {
				focusedElement.setFocused(false);
			}
			this.focusedElement = focusable;
		}
		focusListeners.forEach((listener) -> listener.focusChanged(focusable));
	}
	
	public GuiElement getMouseOver() {
		return getClickElementAt(mouseX, mouseY);
	}
	
	public boolean isMouseOver(GuiElement element) {
		return getClickElementAt(mouseX, mouseY) == element;
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	public void back() {
		setScreen(parentScreen);
	}
	
	public void close() {
		setScreen(null);
	}
	
	public void setScreen(GuiScreen screen) {
		InstallerGuiModern.getInstance().setScreen(screen);
	}
	
	public void onScreenOpened() {
		screenListeners.forEach((listener) -> listener.onScreenOpened(this));
	}

}
