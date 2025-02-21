package b100.installer.gui.modern.screen;

import b100.installer.gui.modern.element.GuiBackground;
import b100.installer.gui.modern.element.GuiTextField;

public class GuiTest extends GuiScreen {

	public GuiTextField textField;
	
	public GuiTest(GuiScreen parentScreen) {
		super(parentScreen);
	}

	@Override
	protected void onInit() {
		add(new GuiBackground(this));
		
		textField = add(new GuiTextField(this));
	}
	
	@Override
	public void draw() {
		super.draw();
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
	
	@Override
	public void onResize() {
		textField.setPosition((width - textField.width) / 2, height - textField.height - 16);
		
		super.onResize();
	}
		
}
