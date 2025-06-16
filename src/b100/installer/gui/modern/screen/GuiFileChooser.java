package b100.installer.gui.modern.screen;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import javax.swing.filechooser.FileSystemView;

import b100.installer.gui.modern.InstallerGuiModern;
import b100.installer.gui.modern.element.GuiBackground;
import b100.installer.gui.modern.element.GuiButton;
import b100.installer.gui.modern.element.GuiElement;
import b100.installer.gui.modern.element.GuiListButton;
import b100.installer.gui.modern.element.GuiScrollBar;
import b100.installer.gui.modern.element.GuiScrollableList;
import b100.installer.gui.modern.element.GuiScrollableList.ListLayout.Align;
import b100.installer.gui.modern.element.GuiTextField;
import b100.installer.gui.modern.render.Textures;
import b100.installer.gui.modern.util.ActionListener;

public class GuiFileChooser extends GuiScreen {

	public GuiScrollableList rootList;
	
	public GuiScrollableList currentDirectoryList;
	public GuiScrollBar currentDirectoryListScrollBar;
	
	public Button buttonBack;
	public Button buttonParentDirectory;
	
	public Button buttonCancel;
	public Button buttonOpen;
	
	private List<File> previousDirectories = new ArrayList<>();
	private File currentDirectory = null;
	private File selectedFile = null;
	
	public GuiTextField pathTextField;
	
	public Function<File, Boolean> fileFilter;
	
	public GuiFileChooser(GuiScreen parentScreen) {
		super(parentScreen);
	}

	@Override
	protected void onInit() {
		add(new GuiBackground(this));

		pathTextField = add(new GuiTextField(this));
		
		ActionListener actionListener = getInternalActionListener();
		
		buttonBack = add(new Button(this, 0, 0));
		buttonBack.addActionListener(actionListener);
		buttonBack.setClickable(false);
		
		buttonParentDirectory = add(new Button(this, 2, 0));
		buttonParentDirectory.addActionListener(actionListener);
		buttonParentDirectory.setClickable(false);
		
		List<File> rootFilesList = new ArrayList<>();
		rootFilesList.add(FileSystemView.getFileSystemView().getHomeDirectory());
		for(File rootFile : File.listRoots()) {
			rootFilesList.add(rootFile);
		}
		
		rootList = new FileList(this);
		
		for(int i=0; i < rootFilesList.size(); i++) {
			File rootFile = rootFilesList.get(i);
			
			FileElement fileElement = new FileElement(this, rootFile);
			rootList.add(fileElement);
		}
		
		currentDirectoryList = new FileList(this);
		
		add(rootList);
		add(currentDirectoryList);
		
		currentDirectoryListScrollBar = new GuiScrollBar(this, currentDirectoryList);
		add(currentDirectoryListScrollBar);
		
		buttonCancel = add(new Button(this, "Cancel"));
		buttonCancel.addActionListener(actionListener);
		
		buttonOpen = add(new Button(this, "Choose Folder"));
	}
	
	@Override
	public boolean keyEvent(int key, boolean pressed) {
		if(pressed) {
			if(pathTextField.isFocused() && key == KeyEvent.VK_ENTER) {
				File file = new File(pathTextField.getText());
				if(file.isDirectory()) {
					setDirectory(file, true, false);	
				}
			}
			if(InstallerGuiModern.getInstance().isAltPressed() && key == KeyEvent.VK_UP) {
				directoryUp();
				return true;
			}
		}
		if(super.keyEvent(key, pressed)) {
			return true;
		}
		if(pressed) {
			if(key == KeyEvent.VK_BACK_SPACE) {
				directoryBack();
				return true;
			}
		}
		return false;
	}
	
	public boolean directoryBack() {
		if(previousDirectories.size() > 0) {
			setDirectory(previousDirectories.remove(0), false, false);
			return true;
		}
		return false;
	}
	
	public boolean directoryUp() {
		if(currentDirectory != null) {
			File parent = currentDirectory.getAbsoluteFile().getParentFile();
			if(parent != null) {
				setDirectory(parent, true, false);
				return true;
			}	
		}
		return false;
	}
	
	@Override
	public void draw() {
		super.draw();
		
		if(currentDirectory != null) {
//			fontRenderer.drawString(currentDirectory.getAbsolutePath(), 16, 16);
		}
	}
	
	@Override
	public void onResize() {
		int outerPadding = 12;
		int innerPadding = 6;

		int h0 = 12;
		
		int y0 = outerPadding;
		int y1 = y0 + h0 + innerPadding;
		int y2 = height - outerPadding - 10 - innerPadding;
		
		int h1 = y2 - y1 - innerPadding;
		int h2 = 14;
		
		buttonParentDirectory.setPosition(width - outerPadding - h0, y0).setSize(h0, h0);
		buttonBack.setPosition(buttonParentDirectory.posX - h0 - innerPadding, y0).setSize(h0, h0);
		
		rootList.setPosition(outerPadding, y1);
		rootList.setSize(100, h1);
		
		currentDirectoryList.setPosition(outerPadding + rootList.width + innerPadding, y1);
		currentDirectoryList.setSize(width - rootList.width - 2 * outerPadding - innerPadding, h1);
		
		currentDirectoryListScrollBar.setPosition(currentDirectoryList.posX + currentDirectoryList.width - currentDirectoryListScrollBar.width, currentDirectoryList.posY);
		currentDirectoryListScrollBar.setSize(6, currentDirectoryList.height);
		
		for(int i=0; i < currentDirectoryList.elements.size(); i++) {
			currentDirectoryList.elements.get(i).width = currentDirectoryList.width - currentDirectoryListScrollBar.width - 1;
		}

		buttonCancel.setSize(fontRenderer.getStringWidth(buttonCancel.text) + 10, h2);
		buttonCancel.setPosition(width - outerPadding - buttonCancel.width, y2);
		
		buttonOpen.setSize(fontRenderer.getStringWidth(buttonOpen.text) + 10, h2);
		buttonOpen.setPosition(buttonCancel.posX - innerPadding - buttonOpen.width, y2);
		
		pathTextField.setPosition(outerPadding, y0).setSize(width - 2 * outerPadding - 2 * h0 - 2 * innerPadding, h0);
		
		super.onResize();
	}
	
	public boolean setDirectory(File directory, boolean addToPreviousDirectories, boolean focusFirstElement) {
		if(!directory.isDirectory()) {
			return false;
		}
		if(directory.equals(currentDirectory)) {
			return false;
		}
		
		if(currentDirectory != null && addToPreviousDirectories) {
			if(previousDirectories.size() == 0 || !previousDirectories.get(0).equals(currentDirectory)) {
				previousDirectories.add(0, currentDirectory);	
			}
		}
		currentDirectory = directory;
		pathTextField.setText(directory.getAbsolutePath());
		
		currentDirectoryList.removeAll();
		File[] filesInDirectory = directory.listFiles();
		
		if(filesInDirectory != null) {
			// Apparently this can be null
			
			Arrays.sort(filesInDirectory, new FileComparator());
			
			for(int i=0; i < filesInDirectory.length; i++) {
				File file = filesInDirectory[i];
				
				if(fileFilter != null) {
					if(!fileFilter.apply(file)) {
						continue;
					}
				}
				
				FileElement element = new FileElement(this, file);
				currentDirectoryList.add(element);
				if(focusFirstElement && i == 0) {
					element.setFocused(true);
				}
			}
		}
		
		buttonParentDirectory.setClickable(currentDirectory.getAbsoluteFile().getParentFile() != null);
		buttonBack.setClickable(previousDirectories.size() > 0);
		
		onResize();
		InstallerGuiModern.getInstance().scheduleRepaint();
		
		return true;
	}
	
	public File getSelectedFile() {
		if(selectedFile == null) {
			return currentDirectory;
		}
		return selectedFile;
	}
	
	protected ActionListener getInternalActionListener() {
		return new ActionListenerImpl();
	}
	
	protected class ActionListenerImpl implements ActionListener {

		@Override
		public void actionPerformed(GuiElement source) {
			if(source == buttonBack) {
				directoryBack();
			}
			if(source == buttonParentDirectory) {
				directoryUp();
			}
			if(source == buttonCancel) {
				back();
			}
		}
		
	}
	
	protected static class FileList extends GuiScrollableList {

		static ListLayout layout = new ListLayout();
		static {
			layout.innerPadding = 0;
			layout.outerPadding = 0;
			layout.align = Align.LEFT;
		}
		
		public FileList(GuiScreen screen) {
			super(screen, layout);
			
			centerElements = false;
			useScissor = true;
			scissorBorder = 1;
			scrollToElementOffset = 0;
		}
		
		@Override
		public void draw() {
			renderer.setColor(0x000000);
			renderer.drawRectangle(posX, posY, width, height);
			renderer.setColor(0x808080);
			renderer.drawRectangleOutline(posX - 1, posY - 1, width + 2, height + 2);
			renderer.resetColor();
			
			super.draw();
		}
		
	}
	
	protected static class FileElement extends GuiListButton {

		public final GuiFileChooser fileChooser;
		public final File file;
		
		private final String name;
		
		public FileElement(GuiFileChooser screen, File file) {
			super(screen);
			this.fileChooser = screen;
			this.file = file;
			
			String name = file.getName();
			if(name == null || name.length() == 0) {
				name = file.getAbsolutePath();
			}
			this.name = name;
			
			setSize(100, 12);
		}
		
		@Override
		public void draw() {
			if(isFocused()) {
				renderer.setColor(0xFFFFFF);
				renderer.drawRectangleOutline(posX - 1, posY - 1, width + 2, height + 2);
				renderer.resetColor();
			}
			
			int x1 = posX + 2;
			
			int iconX, iconY;
			if(file.isDirectory()) {
				iconX = 0;
				iconY = 8;
			}else {
				iconX = 8;
				iconY = 8;
			}
			
			renderer.drawSubImage(Textures.icons, x1, posY + height / 2 - 4, 8, 8, iconX, iconY);
			fontRenderer.drawString(name, x1 + 10, posY + height / 2 - 4, 0xFFFFFF, false);
		}
		
		@Override
		public boolean mouseEvent(int button, boolean pressed, double mouseX, double mouseY) {
			if(screen.isMouseOver(this) && pressed) {
				if(isFocused()) {
					if(click(false)) {
						return true;
					}
				}	
			}
			
			return super.mouseEvent(button, pressed, mouseX, mouseY);
		}
		
		@Override
		public boolean keyEvent(int key, boolean pressed) {
			if(isFocused() && pressed && (key == KeyEvent.VK_SPACE || key == KeyEvent.VK_ENTER)) {
				if(click(true)) {
					return true;
				}
			}
			return super.keyEvent(key, pressed);
		}
		
		public boolean click(boolean focus) {
			if(file.isDirectory()) {
				fileChooser.setDirectory(file, true, focus);
				return true;
			}
			return false;
		}
	}
	
	protected  static class FileComparator implements Comparator<File> {
		@Override
		public int compare(File o1, File o2) {
			return Boolean.compare(o2.isDirectory(), o1.isDirectory());
		}
	}
	
	protected static class Button extends GuiButton {

		public int iconX;
		public int iconY;
		
		public Button(GuiScreen screen, String text) {
			super(screen, text);
			
			this.iconX = -1;
			this.iconY = -1;
		}

		public Button(GuiScreen screen, int iconX, int iconY) {
			super(screen, null);
			
			this.iconX = iconX;
			this.iconY = iconY;
		}
		
		@Override
		public void draw() {
			int outlineColor;
			int iconColor = 0xFFFFFF;
			if(state == 2) {
				outlineColor = 0xFFFFFF;
			}else if(state == 1) {
				outlineColor = 0x808080;
			}else {
				outlineColor = 0x404040;
				iconColor = 0x404040;
			}
			
			renderer.setColor(outlineColor);
			renderer.drawRectangleOutline(posX - 1, posY - 1, width + 2, height + 2);
			renderer.setColor(0x000000);
			renderer.drawRectangle(posX, posY, width, height);
			
			if(iconX >= 0 && iconY >= 0) {
				renderer.setColor(iconColor);
				renderer.drawSubImage(Textures.icons, posX + width / 2 - 4, posY + height / 2 - 4, 8, 8, iconX * 8, iconY * 8);
			}

			if(text != null) {
				int textWidth = fontRenderer.getStringWidth(text);
				int textX = posX + (width - textWidth) / 2;
				int textY = posY + height / 2 - 4;
				fontRenderer.drawString(text, textX, textY, 0xFFFFFF, true);
			}
			
			renderer.resetColor();
		}
	}
}
