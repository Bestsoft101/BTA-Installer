package b100.installer.gui.modern;

import b100.installer.gui.modern.multimc.GuiInstallMultiMc;

public class GuiMainMenu extends GuiScreen {
	
	public GuiButton buttonMultiMc;
	public GuiButton buttonBetaCraft;
	public GuiButton buttonVanillaLauncher;
	
	public GuiMainMenu(GuiScreen parentScreen) {
		super(parentScreen);
	}

	@Override
	protected void onInit() {
		add(new GuiBackground(this));
		
		buttonMultiMc = add(new GuiButton(this, "MultiMC / Prism Launcher").addActionListener((e) -> setScreen(new GuiInstallMultiMc(this, null))));
		buttonBetaCraft = add(new GuiButton(this, "BetaCraft"));
		buttonVanillaLauncher = add(new GuiButton(this, "Vanilla Launcher"));
	}
	
	@Override
	public void draw() {
		super.draw();
		
		int x = (renderer.getWidth() - Textures.logo.getWidth()) / 2;
		int y = 30;
		
		renderer.drawImage(Textures.logo, x, y);
		fontRenderer.drawString("Better Than Adventure! Installer", 2, 2, 0x505050, true);
	}
	
	@Override
	public void onResize() {
		int x1 = width / 2 - 100;
		int y1 = height / 4;
		int p = 24;
		
		buttonMultiMc.setPosition(x1, y1 + p * 2);
		buttonBetaCraft.setPosition(x1, y1 + p * 3);
		buttonVanillaLauncher.setPosition(x1, y1 + p * 4);
	}
	
}
