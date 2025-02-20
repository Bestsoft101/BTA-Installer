package b100.installer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import b100.installer.gui.classic.VersionListGUI.VersionFilter;
import b100.json.element.JsonArray;
import b100.json.element.JsonEntry;
import b100.json.element.JsonObject;

public class Versions {
	
	private static final Versions INSTANCE = new Versions();
	
	public static Versions getInstance() {
		return INSTANCE;
	}
	
	private List<Channel> allChannels = new ArrayList<>();
	private List<Version> allVersions = new ArrayList<>();
	
	private Map<String, Version> idToVersionMap = new HashMap<>();
	
	private Versions() {
		System.out.println("Loading version list...");
		
		long start = System.currentTimeMillis();
		
		downloadVersionList();
		
		long end = System.currentTimeMillis();
		long delta = end - start;
		
		System.out.println("Loading version list took " + delta + "ms");
	}
	
	private void downloadVersionList() {
		JsonObject channelsObject = DownloadHelper.getJson("bta-client/channels.json");
		JsonArray channels = channelsObject.getArray("channels");
		
		for(int i=0; i < channels.length(); i++) {
			String channelId = channels.get(i).getAsString().value;
			
			Channel channel = new Channel(channelId, null);
			allChannels.add(channel);
			
			JsonObject versionsRoot = DownloadHelper.getJson("bta-client/" + channelId + "/versions.json");
			JsonObject versions = versionsRoot.getObject("versions");
			
			for(int j=0; j < versions.entryList().size(); j++) {
				JsonEntry entry = versions.entryList().get(j);
				JsonObject versionManifest = entry.value.getAsObject();
				
				String versionId = entry.name;
				String displayName = null;
				if(versionManifest.has("displayName")) {
					displayName = versionManifest.getString("displayName");
				}
				
				Version version = new Version(versionId, displayName, channel, versionManifest);
				allVersions.add(version);
				idToVersionMap.put(versionId, version);
			}
		}
		
		System.out.println("All Versions: ");
		
		for(Version version : allVersions) {
			System.out.println(version);
		}
	}
	
	public Version get(String id) {
		return idToVersionMap.get(id);
	}
	
	public List<Version> getAllVersions() {
		return allVersions;
	}
	
	public List<Version> getAllVersions(VersionFilter filter, ModLoader loader) {
		if(loader == null) {
			throw new NullPointerException("ModLoader is null!");
		}
		
		List<Version> filteredVersions = new ArrayList<>();

		for(Version version : allVersions) {
			if(filter.isCompatible(version.id, loader)) {
				filteredVersions.add(version);
			}
		}
		
		return filteredVersions;
	}
	
	public Version getLatestVersion() {
		// TODO
		
		return allVersions.get(0);
	}
	
	public class Channel {
		
		/** e.g. "prerelease" */
		public final String id;
		
		/** e.g. "Pre-Release" */
		public final String displayName;
		
		public Channel(String id, String displayName) {
			this.id = id;
			this.displayName = displayName;
		}
		
		public String getDisplayName() {
			if(displayName != null) {
				return displayName;
			}
			return id;
		}
		
		@Override
		public String toString() {
			return getDisplayName();
		}
	}
	
	public class Version {
		
		/** The ID does not contain any special characters or spaces, e.g. "7.3-pre1" */
		public final String id;
		
		/** The display name may contain special characters or spaces, e.g. "7.3 Prerelease 1". May be null! */
		public final String displayName;
		public final Channel channel;
		public final JsonObject manifest;
		
		public Version(String id, String displayName, Channel channel, JsonObject manifest) {
			this.id = id;
			this.displayName = displayName;
			this.channel = channel;
			this.manifest = manifest;
		}
		
		public File getFile(String filename) {
			return DownloadHelper.getFile("bta-client/" + channel.id + "/" + id + "/" + filename);
		}
		
		public String getDisplayName() {
			if(displayName != null) {
				return displayName;
			}
			return id;
		}
		
		@Override
		public String toString() {
			return getDisplayName();
		}
		
	}
}
