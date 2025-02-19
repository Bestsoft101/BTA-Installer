package b100.installer.gui.modern.screen.multimc;

import b100.installer.Utils;
import b100.installer.gui.modern.element.GuiElement;
import b100.installer.gui.modern.screen.GuiFileChooser;
import b100.installer.gui.modern.screen.GuiScreen;
import b100.installer.gui.modern.util.ActionListener;

public class GuiChooseMultiMcFolder extends GuiFileChooser implements ActionListener {

	public GuiChooseMultiMcFolder(GuiScreen parentScreen) {
		super(parentScreen);
		
		this.fileFilter = (file) -> file.isDirectory();
	}
	
	@Override
	protected void onInit() {
		super.onInit();
		
		buttonOpen.addActionListener(this);
	}

	@Override
	public void actionPerformed(GuiElement source) {
		if(source == buttonOpen) {
			Utils.multiMcInstanceFolderOverride = getSelectedFile();
			
			setScreen(new GuiInstallMultiMc(parentScreen, Utils.multiMcInstanceFolderOverride));
		}
	}
	
}
