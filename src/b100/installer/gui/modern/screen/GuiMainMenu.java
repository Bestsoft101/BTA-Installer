package b100.installer.gui.modern.screen;

import b100.installer.Utils;
import b100.installer.gui.modern.element.GuiBackground;
import b100.installer.gui.modern.element.GuiButton;
import b100.installer.gui.modern.element.GuiElement;
import b100.installer.gui.modern.render.Textures;
import b100.installer.gui.modern.screen.multimc.GuiChooseMultiMcFolder;
import b100.installer.gui.modern.screen.multimc.GuiInstallMultiMc;
import b100.installer.gui.modern.util.ActionListener;

public class GuiMainMenu extends GuiScreen implements ActionListener {
	
	public GuiButton buttonMultiMc;
	public GuiButton buttonBetaCraft;
	public GuiButton buttonVanillaLauncher;
	
	public GuiMainMenu(GuiScreen parentScreen) {
		super(parentScreen);
	}

	@Override
	protected void onInit() {
		add(new GuiBackground(this));
		
		buttonMultiMc = add(new GuiButton(this, "MultiMC / Prism Launcher").addActionListener(this));
		buttonBetaCraft = add(new GuiButton(this, "BetaCraft"));
		buttonVanillaLauncher = add(new GuiButton(this, "Vanilla Launcher"));
		
		buttonBetaCraft.setClickable(false);
		buttonVanillaLauncher.setClickable(false);
	}
	
	@Override
	public void draw() {
		super.draw();
		
		int x = (renderer.getWidth() - Textures.logo.getWidth()) / 2;
		int y = 30;
		
		renderer.drawImage(Textures.logo, x, y + 8);
		fontRenderer.drawString("Better Than Adventure! Installer", 2, 2, 0x505050, true);
	}
	
	@Override
	public void onResize() {
		int x1 = width / 2 - 100;
		int y1 = height / 4;
		int p = 24;
		
		buttonMultiMc.setPosition(x1, y1 + p * 3);
		buttonBetaCraft.setPosition(x1, y1 + p * 4);
		buttonVanillaLauncher.setPosition(x1, y1 + p * 5);
	}

	@Override
	public void actionPerformed(GuiElement source) {
		if(source == buttonMultiMc) {
			if(Utils.multiMcInstanceFolderOverride != null) {
				setScreen(new GuiInstallMultiMc(this, Utils.multiMcInstanceFolderOverride));
			}else {
				setScreen(new GuiChooseMultiMcFolder(this));
			}
		}
	}
	
}
