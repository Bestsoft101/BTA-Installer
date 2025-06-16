package b100.installer.gui.modern.screen.multimc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import b100.installer.gui.modern.element.GuiButton;
import b100.installer.gui.modern.element.GuiElement;
import b100.installer.gui.modern.element.GuiListButton;
import b100.installer.gui.modern.screen.GuiScreen;
import b100.installer.gui.modern.screen.GuiScrollListScreen;
import b100.installer.gui.modern.util.ActionListener;
import b100.installer.util.MultiMCHelper;
import b100.json.JsonParser;
import b100.json.element.JsonArray;
import b100.json.element.JsonElement;
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
		
		this.title = "Select Instance";
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
		final List<String> allInstanceGroups = new ArrayList<String>(); // All instance groups in order
		final Map<String, String> instanceToGroupMap = new HashMap<>(); // Get group name from instance name
		final Map<String, List<String>> groupToInstancesMap = new HashMap<>(); // Get contained instances from group name
		
		allInstanceGroups.add(DEFAULT_GROUP_ID);
		
		// Read instgroups.json
		File instgroupsFile = new File(instancesFolder, "instgroups.json");
		if(instgroupsFile.isFile()) {
			JsonObject groups = JsonParser.instance.parseFileContent(instgroupsFile).getObject("groups");
			for(JsonEntry entry : groups) {
				String groupName = entry.name;
				
				allInstanceGroups.add(groupName);
				
				JsonArray instancesInGroup = entry.value.getAsObject().getArray("instances");
				for(JsonElement element : instancesInGroup) {
					instanceToGroupMap.put(element.getAsString().value, groupName);
				}
			}	
		}
		
		// Get all instances in folder and create list for each group
		for(File instanceFolder : instancesFolder.listFiles()) {
			if(!MultiMCHelper.isInstance(instanceFolder)) {
				continue;
			}
			
			String instanceName = instanceFolder.getName();
			String instanceGroup = instanceToGroupMap.get(instanceName);
			if(instanceGroup == null) {
				instanceGroup = DEFAULT_GROUP_ID;
			}
			
			List<String> instancesInGroup = groupToInstancesMap.get(instanceGroup);
			if(instancesInGroup == null) {
				instancesInGroup = new ArrayList<>();
				groupToInstancesMap.put(instanceGroup, instancesInGroup);
			}
			
			instancesInGroup.add(instanceName);
		}
		
		// Go through all lists in order and create list elements for all instances
		for(String groupName : allInstanceGroups) {
			if(!DEFAULT_GROUP_ID.equals(groupName)) {
				scrollList.add(new GroupElement(groupName));	
			}
			
			List<String> instancesInGroup = groupToInstancesMap.get(groupName);
			if(instanceToGroupMap == null) {
				// Apparently instgroups.json can contain empty groups or groups with non-existant instances
				continue;
			}
			
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
				instanceName = MultiMCHelper.getInstanceName(instanceFolder);	
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
