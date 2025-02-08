package b100.installer.gui.modern;

public class GuiScrollBar extends GuiElement {

	public GuiScreen screen;
	public GuiScrollableList list;
	
	protected boolean dragging = false;
	protected int dragButton = 0;
	protected double previousScrollAmount;
	
	protected double clickPosX;
	protected double clickPosY;
	
	public GuiScrollBar(GuiScreen screen, GuiScrollableList list) {
		this.screen = screen;
		this.list = list;
	}
	
	@Override
	public void tick() {
		int scrollRegionHeight = list.getScrollRegionHeight();
		int contentHeight = list.getContentHeight();
		
		int scrollerHeight = getScrollerHeight();
		
		if(dragging) {
			double offset = clickPosY - screen.mouseY;
			
			int scrollableAreaLength = height - scrollerHeight;
			
			double d = offset / (double) scrollableAreaLength;
			d *= (contentHeight - scrollRegionHeight);
			
			list.setScrollAmount(previousScrollAmount - d);
		}
	}
	
	protected int getScrollerHeight() {
		int scrollRegionHeight = list.getScrollRegionHeight();
		int contentHeight = list.getContentHeight();
		
		float f = scrollRegionHeight / (float) contentHeight;
		int scrollerHeight = (int) (this.height * f);
		scrollerHeight = Math.max(scrollerHeight, 32);
		scrollerHeight = Math.min(scrollerHeight, this.height - this.width);
		return scrollerHeight;
	}
	
	@Override
	public void draw() {
		int scrollerHeight = getScrollerHeight();
		
		float scrollFactor = (float) (list.getScrollAmount() / list.getMaxScrollAmount());
		int scrollerOffset = (int) (scrollFactor * (height - scrollerHeight));
		
		renderer.setColor(0x000000);
		renderer.drawRectangle(posX, posY, width, height);

		renderer.setColor(0x808080);
		renderer.drawRectangle(posX, posY + scrollerOffset, width, scrollerHeight);
		renderer.setColor(0xc0c0c0);
		renderer.drawRectangle(posX, posY + scrollerOffset, width - 1, scrollerHeight - 1);
		renderer.setColor(0xFFFFFF);
	}
	
	@Override
	public boolean mouseEvent(int button, boolean pressed, double mouseX, double mouseY) {
		if(!pressed && dragging && button == dragButton) {
			dragging = false;
		}
		if(pressed && !dragging && screen.isMouseOver(this)) {
			dragging = true;
			dragButton = button;
			previousScrollAmount = list.getScrollAmount();
			clickPosX = mouseX;
			clickPosY = mouseY;
		}
		return super.mouseEvent(button, pressed, mouseX, mouseY);
	}
	
	@Override
	public boolean isSolid() {
		return list.getContentHeight() > list.getScrollRegionHeight();
	}

}
