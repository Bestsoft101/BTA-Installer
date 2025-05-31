package b100.installer.gui.modern.screen;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import b100.installer.Versions;
import b100.installer.Versions.Channel;
import b100.installer.Versions.Version;
import b100.installer.gui.classic.VersionListGUI.VersionFilter;
import b100.installer.gui.modern.element.GuiBackground;
import b100.installer.gui.modern.element.GuiButton;
import b100.installer.gui.modern.element.GuiCheckbox;
import b100.installer.gui.modern.element.GuiElement;
import b100.installer.gui.modern.element.GuiListButton;
import b100.installer.gui.modern.element.GuiScrollBar;
import b100.installer.gui.modern.element.GuiScrollableList;
import b100.installer.gui.modern.element.GuiTextElement;
import b100.installer.gui.modern.util.ActionListener;
import b100.installer.gui.modern.util.FocusListener;
import b100.installer.gui.modern.util.Focusable;
import b100.installer.util.ModLoader;

public class GuiSelectVersion extends GuiScreen implements ActionListener, FocusListener {
	
	private static Map<Channel, Boolean> enabledChannels = new HashMap<>();
	
	public VersionFilter versionFilter;
	public Consumer<Version> consumer;
	
	public GuiTextElement title;
	
	public GuiButton selectButton;
	
	public Version selectedVersion = null;

	private GuiBackground scrollListBackground;
	public GuiScrollableList scrollList;
	public GuiScrollBar scrollBar;
	
	public List<ChannelCheckbox> channelCheckboxes;
	
	public GuiSelectVersion(GuiScreen parentScreen, VersionFilter versionFilter, Consumer<Version> consumer, Version initialSelection) {
		super(parentScreen);
		
		this.versionFilter = versionFilter;
		this.consumer = consumer;
		this.selectedVersion = initialSelection;
	}
	
	@Override
	protected void onInit() {
		add(new GuiBackground(this));
		title = add(new GuiTextElement("Select Version", 0.5, 0.5));
		
		scrollListBackground = add(new GuiBackground(null, 0x202020));
		
		scrollList = add(new GuiScrollableList(this));
		scrollList.createShadows(this);
		scrollList.useScissor = true;
		
		scrollBar = add(new GuiScrollBar(this, scrollList));

		selectButton = new GuiButton(this, "Select");
		selectButton.addActionListener(this);
		
		channelCheckboxes = new ArrayList<ChannelCheckbox>();
		List<Channel> channels = Versions.getInstance().getAllChannels();
		for(Channel channel : channels) {
			if(!enabledChannels.containsKey(channel)) {
				enabledChannels.put(channel, true);
			}
			
			ChannelCheckbox checkbox = new ChannelCheckbox(this, channel);
			checkbox.setChecked(isChannelActive(channel));
			checkbox.addActionListener(this);
			add(checkbox);
			channelCheckboxes.add(checkbox);
		}
		
		updateVersionList();
		
		add(selectButton);
	}
	
	@Override
	public void draw() {
		scrollListBackground.offset = (int) (scrollList.getScrollAmount() / 2.0);
		
		super.draw();
	}
	
	public void updateVersionList() {
		scrollList.removeAll();
		
		boolean selectedVersionIsVisible = false;
		
		List<Version> versions = Versions.getInstance().getAllVersions(versionFilter, ModLoader.None);
		for(int i=0; i < versions.size(); i++) {
			Version version = versions.get(i);
			if(!isChannelActive(version.channel)) {
				continue;
			}
			
			VersionButton button = new VersionButton(this, version);
			button.addFocusListener(this);
			if(version.equals(selectedVersion)) {
				button.setFocused(true);
				selectedVersionIsVisible = true;
			}
			
			scrollList.add(button);
		}
		
		selectButton.setClickable(selectedVersionIsVisible);
	}
	
	public boolean isChannelActive(Channel channel) {
		Boolean enabled = enabledChannels.get(channel);
		return enabled != null && enabled;
	}
	
	@Override
	public void onResize() {
		int headerSize = 32;
		int footerSize = 32;
		int listWidth = width - 120;
		int listHeight = height - headerSize - footerSize;
		
		title.setPosition(0, 0).setSize(width, headerSize);
		
		scrollList.setPosition(0, headerSize);
		scrollList.setSize(listWidth, listHeight);
		scrollListBackground.setPositionAndSize(scrollList);
		
		scrollBar.setPosition(scrollList.posX + scrollList.width, scrollList.posY);
		scrollBar.height = scrollList.height;
		
		int x1 = width / 2 - 100;
		int y1 = height - headerSize / 2 - 10;
		int p = 4;
		
		selectButton.setPosition(x1, y1);
		
		for(int i=0; i < channelCheckboxes.size(); i++) {
			GuiCheckbox checkbox = channelCheckboxes.get(i);
			checkbox.setPosition(scrollList.posX + scrollList.width + scrollBar.width + p, scrollList.posY + i * (20 + p));
			checkbox.width = 100;
		}
		
		super.onResize();
	}

	@Override
	public void actionPerformed(GuiElement source) {
		if(source == selectButton) {
			consumer.accept(selectedVersion);
		}
		if(source instanceof ChannelCheckbox) {
			ChannelCheckbox checkbox = (ChannelCheckbox) source;
			enabledChannels.put(checkbox.channel, checkbox.isChecked());
			updateVersionList();
			onResize();
		}
	}
	
	@Override
	public boolean keyEvent(int key, boolean pressed) {
		if(super.keyEvent(key, pressed)) {
			return true;
		}
		if((key == KeyEvent.VK_ENTER || key == KeyEvent.VK_SPACE) && pressed) {
			consumer.accept(selectedVersion);
			return true;
		}
		return false;
	}
	
	@Override
	public void focusChanged(Focusable focusable) {
		if(focusable.isFocused() && focusable instanceof VersionButton) {
			VersionButton versionButton = (VersionButton) focusable;
			
			selectedVersion = versionButton.version;
			selectButton.setClickable(selectedVersion != null);
		}
		super.focusChanged(focusable);
	}
	
	class VersionButton extends GuiListButton {

		public final Version version;
		
		public VersionButton(GuiScreen screen, Version version) {
			super(screen);
			
			this.version = version;
			this.text = version.getDisplayName();
		}
	}
	
	class ChannelCheckbox extends GuiCheckbox {

		public final Channel channel;
		
		public ChannelCheckbox(GuiScreen screen, Channel channel) {
			super(screen, channel.getDisplayName());
			
			this.channel = channel;
		}
	}
}
