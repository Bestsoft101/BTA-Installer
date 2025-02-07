package b100.installer.gui.modern;

import java.util.List;
import java.util.function.Consumer;

import b100.installer.ModLoader;
import b100.installer.VersionList;
import b100.installer.gui.classic.VersionListGUI.VersionFilter;

public class GuiSelectVersion extends GuiScrollListScreen implements ActionListener, FocusListener {
	
	public VersionFilter versionFilter;
	public Consumer<String> consumer;
	
	public String selectedVersion = null;
	
	public GuiButton buttonSelect;
	
	public String selection;
	
	public GuiSelectVersion(GuiScreen parentScreen, VersionFilter versionFilter, Consumer<String> consumer, String selection) {
		super(parentScreen);
		
		this.versionFilter = versionFilter;
		this.consumer = consumer;
		this.selection = selection;
		
		this.title = "Select Version";
	}
	
	@Override
	protected void onInit() {
		buttonSelect = new GuiButton(this, "Select");
		buttonSelect.setClickable(selectedVersion != null);
		buttonSelect.addActionListener(this);
		
		super.onInit();
		
		add(buttonSelect);
	}
	
	@Override
	public void initScrollElements() {
		List<String> versions = VersionList.getAllVersions(versionFilter, ModLoader.None);
		
		for(int i=0; i < versions.size(); i++) {
			String version = versions.get(i);
			
			GuiListButton button = new GuiListButton(this);
			button.text = version;
			button.addFocusListener(this);
			if(version.equals(selection)) {
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
		
		buttonSelect.setPosition(x1, y1);
	}

	@Override
	public void actionPerformed(GuiElement source) {
		consumer.accept(selectedVersion);
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
			buttonSelect.setClickable(selectedVersion != null);
			
			System.out.println("Selected Version: " + selectedVersion);
		}
		super.focusChanged(focusable);
	}
	
}
