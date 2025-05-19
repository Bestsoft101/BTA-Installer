package b100.installer.gui.modern.screen.multimc;

import b100.installer.gui.modern.element.GuiElement;
import b100.installer.gui.modern.screen.GuiFileChooser;
import b100.installer.gui.modern.screen.GuiScreen;
import b100.installer.gui.modern.util.ActionListener;
import b100.installer.util.Utils;

public class GuiChooseMultiMCFolder extends GuiFileChooser implements ActionListener {

	public GuiChooseMultiMCFolder(GuiScreen parentScreen) {
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
			
			setScreen(new GuiInstallMultiMC(parentScreen, Utils.multiMcInstanceFolderOverride));
		}
	}
	
}
