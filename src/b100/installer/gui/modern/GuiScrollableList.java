package b100.installer.gui.modern;

import b100.installer.Utils;

public class GuiScrollableList extends GuiContainer {
	
	public GuiScreen screen;
	public Layout layout;
	
	private double scrollAmount = 0.0;
	private double maxScrollAmount = 0.0;
	private int contentHeight;
	private int scrollRegionHeight;
	
	private boolean scrollAmountChanged = false;
	
	public GuiScrollableList(GuiScreen screen, Layout layout) {
		this.screen = screen;
		this.layout = layout;
		this.isList = true;
	}
	
	@Override
	public void tick() {
		setScrollAmount(scrollAmount);
		if(scrollAmountChanged) {
			scrollAmountChanged = false;
			layout.moveElements(this);
			InstallerGuiModern.getInstance().scheduleRepaint();
			super.onResize();
		}
		
		super.tick();
	}
	
	@Override
	public void onResize() {
		scrollRegionHeight = getScrollRegionHeight();
		contentHeight = layout.getContentHeight(this);
		maxScrollAmount = Math.max(0.0, contentHeight - scrollRegionHeight);
		scrollAmount = Utils.clamp(scrollAmount, 0.0, maxScrollAmount);
		layout.moveElements(this);
		
		super.onResize();
	}
	
	@Override
	public boolean scrollEvent(double verticalAmount, double mouseX, double mouseY) {
		if(isInside(mouseX, mouseY)) {
			scroll(verticalAmount * 16.0);
			return true;
		}
		return false;
	}
	
	public void scroll(double amount) {
		setScrollAmount(scrollAmount - amount);
	}
	
	public void setScrollAmount(double newScrollAmount) {
		if(contentHeight < height) {
			newScrollAmount = -(height - contentHeight) / 2;
		}else {
			newScrollAmount = Utils.clamp(newScrollAmount, 0.0, maxScrollAmount);	
		}
		if(newScrollAmount == scrollAmount) {
			return;
		}
		scrollAmount = newScrollAmount;
		scrollAmountChanged = true;
	}
	
	@Override
	public boolean isSolid() {
		return true;
	}
	
	public int getScrollRegionHeight() {
		return height;
	}
	
	public int getContentHeight() {
		return contentHeight;
	}
	
	public int getScrollOffset() {
		return -Utils.floor(scrollAmount);
	}
	
	public double getScrollAmount() {
		return scrollAmount;
	}
	
	public double getMaxScrollAmount() {
		return maxScrollAmount;
	}
	
	@Override
	public GuiElement getClickElementAt(double x, double y) {
		if(!isInside(x, y)) {
			return null;
		}
		return super.getClickElementAt(x, y);
	}
	
	@Override
	public Focusable getFirstFocusableElement(FocusDirection direction) {
		Focusable lastFocusedElement = getLastFocusedElement();
		if(lastFocusedElement != null && direction.isTab()) {
			return lastFocusedElement;
		}
		return super.getFirstFocusableElement(direction);
	}
	
	@Override
	public Focusable getNextFocusable(GuiElement element, FocusDirection direction) {
		return super.getNextFocusable(element, direction);
	}
	
	@Override
	public void onElementAdded(GuiElement element) {
		super.onElementAdded(element);
	}
	
	@Override
	public void onElementRemoved(GuiElement element) {
		super.onElementRemoved(element);
	}
	
	public static interface Layout {
		
		public void moveElements(GuiScrollableList list);
		
		public int getContentHeight(GuiScrollableList list);
		
		public int getContentWidth(GuiScrollableList list);
		
	}

	@Override
	public void focusChanged(Focusable focusable) {
		scrollToElement(focusable);
		
		super.focusChanged(focusable);
	}
	
	public boolean scrollToElement(Focusable focusable) {
		if(focusable == null) {
			return false;
		}
		GuiElement element = (GuiElement) focusable;
		if(focusable.isFocused() && contains(element)) {
			int offset = 0;
			if(element.posY < posY) {
				offset = posY - element.posY + 4;
			}
			if(element.posY + element.height > posY + height) {
				offset = (posY + height) - element.posY - element.height - 4;
			}
			if(offset != 0) {
				scroll(offset);
			}
			return true;
		}
		return false;
	}
	
	public static class ListLayout implements Layout {
		
		public int outerPadding = 5;
		public int innerPadding = 0;
		public Align align = Align.CENTER;
		
		public ListLayout() {
		}
		
		@Override
		public void moveElements(GuiScrollableList list) {
			int offset = list.getScrollOffset();
			offset += outerPadding;
			
			for(int i=0; i < list.elements.size(); i++) {
				GuiElement element = list.elements.get(i);
				
				int x;
				if(align == Align.LEFT) {
					x = list.posX + outerPadding;
				}else if(align == Align.CENTER) {
					x = list.posX + list.width / 2 - element.width / 2;
				}else {
					x = list.posX + list.width - element.width - outerPadding;
				}
				
				element.setPosition(x, list.posY + offset);
				offset += element.height;
				offset += innerPadding;
			}
		}

		@Override
		public int getContentHeight(GuiScrollableList list) {
			int contentHeight = 0;
			
			for(int i=0; i < list.elements.size(); i++) {
				contentHeight += list.elements.get(i).height;
			}
			
			contentHeight += 2 * outerPadding;
			contentHeight += (list.elements.size() - 1) * innerPadding;
			
			return contentHeight;
		}

		@Override
		public int getContentWidth(GuiScrollableList list) {
			int contentWidth = 0;
			
			for(int i=0; i < list.elements.size(); i++) {
				contentWidth = Math.max(contentWidth, list.elements.get(i).width);
			}
			
			contentWidth += 2 * outerPadding;
			
			return contentWidth;
		}
		
		public static enum Align {
			
			LEFT, CENTER, RIGHT;
			
		}

	}

}
