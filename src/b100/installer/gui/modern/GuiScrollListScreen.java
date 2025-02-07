package b100.installer.gui.modern;

import b100.installer.gui.modern.GuiElementShadow.Position;
import b100.installer.gui.modern.GuiScrollableList.Layout;
import b100.installer.gui.modern.GuiScrollableList.ListLayout;

public abstract class GuiScrollListScreen extends GuiScreen {

	public GuiScrollableList scrollList;
	public GuiScrollBar scrollBar;
	public Layout listLayout;
	
	public int headerSize = 32;
	public int footerSize = 32;
	
	public String title;
	
	private GuiBackground header;
	private GuiBackground footer;

	public GuiScrollListScreen(GuiScreen parentScreen) {
		super(parentScreen);
	}
	
	public abstract void initScrollElements();

	@Override
	protected void onInit() {
		add(new GuiBackground(this, 0x202020));
		
		listLayout = getListLayout();
		scrollList = add(new GuiScrollableList(this, listLayout));
		
		initScrollElements();
		
		header = add(new GuiBackground(null));
		footer = add(new GuiBackground(null));
		
		add(new GuiElementShadow(header, Textures.shadow_2, Position.BELOW_ELEMENT));
		add(new GuiElementShadow(footer, Textures.shadow_1, Position.ABOVE_ELEMENT));
		
		scrollBar = add(new GuiScrollBar(this, scrollList));
	}
	
	@Override
	public void draw() {
		super.draw();
		if(title != null) {
			fontRenderer.drawCenteredString(title, width / 2, headerSize / 2 - 4, 0xFFFFFFFF, true);
		}
	}
	
	@Override
	public void onResize() {
		final int scrollBarWidth = 6;
		scrollList.setPosition(0, headerSize).setSize(this.width, this.height - (headerSize + footerSize));
		int contentWidth = listLayout.getContentWidth(scrollList);
		scrollBar.setPosition(scrollList.posX + scrollList.width / 2 + contentWidth / 2 + 16, scrollList.posY).setSize(scrollBarWidth, scrollList.height);
		
		header.setPosition(0, 0).setSize(width, headerSize);
		footer.setPosition(0, height - footerSize).setSize(width, footerSize);
		
		super.onResize();
	}

	public void setDoubleFooterButtonPositions(GuiElement left, GuiElement right) {
		setDoubleFooterButtonPositions(this, height - footerSize + 4, left, right);
	}
	
	public Layout getListLayout() {
		return new ListLayout();
	}
	
	public static void setDoubleFooterButtonPositions(GuiScreen screen, int y, GuiElement left, GuiElement right) {
		int p = 2;
		int w = 150;
		int center = screen.width / 2;
		int x0 = center - w - p;
		int x1 = center + p;
		left.setPosition(x0, y).setSize(w, 20);
		right.setPosition(x1, y).setSize(w, 20);
	}

}
