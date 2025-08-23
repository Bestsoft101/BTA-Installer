package b100.installer.gui.modern.element;

import b100.installer.gui.modern.InstallerGuiModern;
import b100.installer.gui.modern.render.Textures;
import b100.installer.gui.modern.screen.GuiScreen;

public class GuiDialog extends GuiContainer {

	public boolean enabled = true;
	public GuiScreen screen;
	
	private boolean updated = true;
	
	private GuiBackground background = new GuiBackground(null);
	
	public GuiDialog(GuiScreen screen) {
		this.screen = screen;
	}
	
	@Override
	public void draw() {
		if(!enabled) {
			return;
		}
		
		if(updated) {
			updated = false;
			
			int innerPadding = 8;
			int outerPadding = 8;
			
			background.width = 0;
			background.height = 0;
			
			for(GuiElement element : elements) {
				background.width = Math.max(element.width, background.width);
				background.height += element.height;
			}
			
			background.width += 2 * outerPadding;
			background.height += 2 * outerPadding;
			
			background.height += (elements.size() - 1) * innerPadding;
			
			background.setSize(background.width, background.height);
			
			int x = (screen.width - background.width) / 2;
			int y = (screen.height - background.height) / 2;
			
			int yOffset = 0;
			
			for(int i=0; i < elements.size(); i++) {
				GuiElement element = elements.get(i);
				
				int x1 = x + (background.width - element.width) / 2;
				int y1 = outerPadding + y + yOffset;
				
				element.setPosition(x1, y1);
				yOffset += element.height;
				yOffset += innerPadding;
			}
			
			background.setPosition(x, y);
		}
		
		setPosition(screen.posX, screen.posY);
		setSize(screen.width, screen.height);
		
		renderer.drawImageStretched(Textures.dialogBackground, posX, posY, width, height);
		
		renderer.setColor(0xFF808080);
		renderer.drawRectangleOutline(background.posX - 1, background.posY - 1, background.width + 2, background.height + 2);
		
		renderer.resetColor();
		
		background.draw();
		
		super.draw();
	}
	
	@Override
	public boolean mouseEvent(int button, boolean pressed, double mouseX, double mouseY) {
		if(enabled) {
			if(pressed && screen.isMouseOver(this)) {
				close();
				return true;
			}
			return super.mouseEvent(button, pressed, mouseX, mouseY);	
		}
		return false;
	}
	
	@Override
	public GuiElement getClickElementAt(double x, double y) {
		if(!enabled) {
			return null;
		}
		return super.getClickElementAt(x, y);
	}
	
	public void close() {
		enabled = false;
		InstallerGuiModern.getInstance().scheduleRepaint();
	}
	
	public void open() {
		enabled = true;
		InstallerGuiModern.getInstance().scheduleRepaint();
	}
	
	public boolean isOpened() {
		return enabled;
	}
	
	@Override
	public void onResize() {
		super.onResize();
		updated = true;
	}
	
	@Override
	public void onElementAdded(GuiElement element) {
		super.onElementAdded(element);
		updated = true;
	}
	
	@Override
	public boolean isSolid() {
		return enabled;
	}
}
