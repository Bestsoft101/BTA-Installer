package b100.installer.gui.modern;

public class GuiInstallMultiMc extends GuiScreen {

	public GuiInstallMultiMc(GuiScreen parentScreen) {
		super(parentScreen);
	}

	@Override
	protected void onInit() {
		add(new GuiBackground(this));
	}
	
	@Override
	public void draw() {
		super.draw();
		
		fontRenderer.drawString("Install MultiMC / Prism Launcher", 2, 2);
	}
	
}
