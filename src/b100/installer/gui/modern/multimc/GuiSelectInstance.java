package b100.installer.gui.modern.multimc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import b100.installer.gui.modern.ActionListener;
import b100.installer.gui.modern.GuiButton;
import b100.installer.gui.modern.GuiElement;
import b100.installer.gui.modern.GuiListButton;
import b100.installer.gui.modern.GuiScreen;
import b100.installer.gui.modern.GuiScrollListScreen;
import b100.installer.installer.MultiMcInstaller;
import b100.json.JsonParser;
import b100.json.element.JsonArray;
import b100.json.element.JsonEntry;
import b100.json.element.JsonObject;

public class GuiSelectInstance extends GuiScrollListScreen implements ActionListener {

	private static final String DEFAULT_GROUP_ID = "_DEFAULT_";
	
	public File instancesFolder;
	public Consumer<String> consumer;
	
	public GuiButton selectButton;
	
	/** The folder name of the currently selected instance */
	public String selectedInstance;
	
//	/** Only used to move the screen. To get the current selection, use selectedInstance instead */
//	private InstanceElement selectedElement;
	
	public GuiSelectInstance(GuiScreen parentScreen, File instancesFolder, Consumer<String> consumer, String initialSelection) {
		super(parentScreen);
		
		this.instancesFolder = instancesFolder;
		this.consumer = consumer;
		this.selectedInstance = initialSelection;
	}
	
	@Override
	protected void onInit() {
		selectButton = new GuiButton(this, "Select");
		selectButton.setClickable(false);
		selectButton.addActionListener(this);
		
		super.onInit();
		
		add(selectButton);
	}

	@Override
	public void initScrollElements() {
		// Read instance groups
		List<String> instanceGroups = new ArrayList<String>();
		Map<String, String> instanceToGroup = new HashMap<>();
		{
			instanceGroups.add(DEFAULT_GROUP_ID);
			
			File instgroupsFile = new File(instancesFolder, "instgroups.json");
			JsonObject instgroups = JsonParser.instance.parseFileContent(instgroupsFile);
			JsonObject groups = instgroups.getObject("groups");
			
			for(JsonEntry entry : groups) {
				String groupName = entry.name;
				
				instanceGroups.add(groupName);
				
				JsonArray instancesInGroup = entry.value.getAsObject().getArray("instances");
				for(int j=0; j < instancesInGroup.length(); j++) {
					String instanceName = instancesInGroup.get(j).getAsString().value;
					
					instanceToGroup.put(instanceName, groupName);
				}
			}
		}
		
		Map<String, List<String>> groupToInstanceList = new HashMap<>();
		{
			File[] files = instancesFolder.listFiles();
			for(int i=0; i < files.length; i++) {
				File instanceFolder = files[i];
				
				if(!MultiMcInstaller.isInstance(instanceFolder)) {
					continue;
				}
				
				String instanceName = instanceFolder.getName();
				String groupName = instanceToGroup.get(instanceName);
				if(groupName == null) {
					groupName = DEFAULT_GROUP_ID;
				}
				
				List<String> instancesInGroup = groupToInstanceList.get(groupName);
				if(instancesInGroup == null) {
					instancesInGroup = new ArrayList<>();
					groupToInstanceList.put(groupName, instancesInGroup);
				}
				
				instancesInGroup.add(instanceName);
			}
		}
		
		for(String groupName : instanceGroups) {
			if(!DEFAULT_GROUP_ID.equals(groupName)) {
				scrollList.add(new GroupElement(groupName));	
			}
			
			List<String> instancesInGroup = groupToInstanceList.get(groupName);
			
			for(String instanceName : instancesInGroup) {
				File instanceFolder = new File(instancesFolder, instanceName);
				
				InstanceElement element = new InstanceElement(this, instanceFolder);
				scrollList.add(element);
				
				if(selectedInstance != null && selectedInstance.equals(instanceFolder.getName())) {
					element.setFocused(true);
				}
			}
		}
	}
	
	@Override
	public void onResize() {
		super.onResize();
		
		int x1 = width / 2 - 100;
		int y1 = height - headerSize / 2 - 10;
		
		selectButton.setPosition(x1, y1);
		
//		scrollList.scrollToElement(selectedElement);
	}

	@Override
	public void actionPerformed(GuiElement source) {
		if(source == selectButton) {
			consumer.accept(selectedInstance);
		}
	}
	
	class GroupElement extends GuiElement {

		public String groupName;
		
		public GroupElement(String groupName) {
			this.groupName = groupName;
			
			setSize(200, 20);
		}
		
		@Override
		public void draw() {
			int x1 = posX + width / 2;
			int y1 = posY + height / 2 - 4;
			
			fontRenderer.drawCenteredString(groupName, x1, y1, 0x808080, true);
		}
		
	}
	
	class InstanceElement extends GuiListButton {
		
		public File instanceFolder;
		public String instanceName;
		
		public InstanceElement(GuiScreen screen, File instanceFolder) {
			super(screen);
			
			this.instanceFolder = instanceFolder;
			try {
				instanceName = MultiMcInstaller.getInstanceName(instanceFolder);	
			}catch (Exception e) {
				e.printStackTrace();
			}
			if(instanceName == null) {
				instanceName = instanceFolder.getName();
			}
			
			this.height = 24;
		}
		
		@Override
		public void draw() {
			super.draw();
			
			int x0 = posX + 3;
			int y0 = posY + 3;
			
			fontRenderer.drawString(instanceName, x0, y0, 0xFFFFFF, true);
			fontRenderer.drawString(instanceFolder.getName(), x0, y0 + 10, 0x808080, true);
		}
		
		@Override
		public void onFocusChanged() {
			super.onFocusChanged();
			
			if(isFocused()) {
				selectedInstance = instanceFolder.getName();
				
				selectButton.setClickable(selectedInstance != null);
				
//				selectedElement = this;
			}
		}
	}
}
