package b100.installer.gui.modern;

public class GuiTest extends GuiScreen {

	public GuiTest(GuiScreen parentScreen) {
		super(parentScreen);
	}

	@Override
	protected void onInit() {
		add(new GuiBackground(this));
	}
	
	@Override
	public void draw() {
		StringBuilder str = new StringBuilder();
		
		for(int i=0; i < 320; i++) {
			char c = (char) i;
			if(c == '\n') {
				c = 0;
			}
			if(i > 0 && i % 16 == 0) {
				str.append('\n');
			}
			str.append(c);
		}
		
		fontRenderer.drawString(str.toString(), 2, 2, 0xFFFFFF, true);
	}
		
}
