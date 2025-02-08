package b100.installer.gui.modern.screen;

import java.util.List;
import java.util.function.Consumer;

import b100.installer.ModLoader;
import b100.installer.VersionList;
import b100.installer.gui.classic.VersionListGUI.VersionFilter;
import b100.installer.gui.modern.element.GuiButton;
import b100.installer.gui.modern.element.GuiElement;
import b100.installer.gui.modern.element.GuiListButton;
import b100.installer.gui.modern.util.ActionListener;
import b100.installer.gui.modern.util.FocusListener;
import b100.installer.gui.modern.util.Focusable;

public class GuiSelectVersion extends GuiScrollListScreen implements ActionListener, FocusListener {
	
	public VersionFilter versionFilter;
	public Consumer<String> consumer;
	
	public GuiButton selectButton;
	
	public String selectedVersion = null;
	
	public GuiSelectVersion(GuiScreen parentScreen, VersionFilter versionFilter, Consumer<String> consumer, String initialSelection) {
		super(parentScreen);
		
		this.versionFilter = versionFilter;
		this.consumer = consumer;
		this.selectedVersion = initialSelection;
		
		this.title = "Select Version";
	}
	
	@Override
	protected void onInit() {
		selectButton = new GuiButton(this, "Select");
		selectButton.setClickable(selectedVersion != null);
		selectButton.addActionListener(this);
		
		super.onInit();
		
		add(selectButton);
	}
	
	@Override
	public void initScrollElements() {
		List<String> versions = VersionList.getAllVersions(versionFilter, ModLoader.None);
		
		for(int i=0; i < versions.size(); i++) {
			String version = versions.get(i);
			
			GuiListButton button = new GuiListButton(this);
			button.text = version;
			button.addFocusListener(this);
			if(version.equals(selectedVersion)) {
				button.setFocused(true);
			}
			
			scrollList.add(button);
		}
	}
	
	@Override
	public void onResize() {
		super.onResize();
		
		int x1 = width / 2 - 100;
		int y1 = height - headerSize / 2 - 10;
		
		selectButton.setPosition(x1, y1);
	}

	@Override
	public void actionPerformed(GuiElement source) {
		if(source == selectButton) {
			consumer.accept(selectedVersion);
		}
	}
	
	@Override
	public boolean keyEvent(int key, boolean pressed) {
		if(super.keyEvent(key, pressed)) {
			return true;
		}
//		if((key == KeyEvent.VK_ENTER || key == KeyEvent.VK_SPACE) && pressed) {
//			consumer.accept(selectedVersion);
//			return true;
//		}
		return false;
	}
	
	@Override
	public void focusChanged(Focusable focusable) {
		if(focusable.isFocused() && focusable instanceof GuiListButton) {
			GuiListButton listButton = (GuiListButton) focusable;
			
			selectedVersion = listButton.text;
			selectButton.setClickable(selectedVersion != null);
			
			System.out.println("Selected Version: " + selectedVersion);
		}
		super.focusChanged(focusable);
	}
	
}
