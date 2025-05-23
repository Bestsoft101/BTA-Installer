package b100.installer.gui.modern.element;

public class GuiProgressBar extends GuiElement {

	protected float progress = 0.0f;
	protected boolean enabled = true;
	
	public GuiProgressBar() {
		setSize(100, 2);
	}
	
	@Override
	public void draw() {
		if(!enabled) {
			return;
		}
		
		int a = Math.round(progress * width);
		int b = width - a;

		renderer.setColor(0x808080);
		renderer.drawRectangle(posX + a, posY, b, height);
		renderer.setColor(0x80FF80);
		renderer.drawRectangle(posX, posY, a, height);
	}
	
	public GuiProgressBar setProgress(float progress) {
		this.progress = progress;
		return this;
	}
	
	public GuiProgressBar setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}
	
	public float getProgress() {
		return progress;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public boolean isSolid() {
		return enabled;
	}
}
