package org.schnabelb.heads;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class SetManager {

	private Map<String, HeadSet> sets;

	public SetManager() {
		sets = new HashMap<String, HeadSet>();
	}

	public void addSet(HeadSet set) {
		this.sets.put(set.getId(), set);
	}

	public HeadSet getSet(String id) {
		return this.sets.get(id);
	}

	public HeadSet getSet(Head head) {
		for (HeadSet set : sets.values()) {
			if (set.getHeads().contains(head)) {
				return set;
			}
		}
		return null;
	}

	public Head getHeadByUrl(String url) {
		for (HeadSet set : sets.values()) {
			for (Head head : set.getHeads()) {
				if (head.getURL().equals(url)) {
					return head;
				}
			}
		}

		return null;
	}

	public List<ItemStack> fillCreativeTab() {
		List<ItemStack> itemList = new ArrayList<ItemStack>();
		List<HeadSet> sortedSets = new ArrayList<HeadSet>(sets.values());
		sortedSets.sort(Comparator.comparing(set -> set.getDisplayName().toUpperCase()));
		sortedSets.forEach(set -> {
			set.getHeads().forEach(head -> {
				itemList.add(head.toItemStack());
			});
			for (int i = 0; i < (9 - (set.getHeads().size() % 9)) % 9; i++) {
				itemList.add(ItemStack.EMPTY);
			}
		});
		return itemList;
	}

	public HeadSet parseJsonSet(JsonObject jsonSet) {
		JsonElement setNameElement = jsonSet.get("setName");
		if (setNameElement == null) {
			throw new IllegalArgumentException();
		}
		String setName = setNameElement.getAsString();
		JsonElement displayNameElement = jsonSet.get("displayName");
		String displayName = displayNameElement == null ? setName : displayNameElement.getAsString();
		HeadSet set = new HeadSet(setName, displayName);
		this.sets.put(setName, set);
		JsonElement heads = jsonSet.get("heads");
		if (heads != null && heads.isJsonArray()) {
			for (JsonElement e : heads.getAsJsonArray()) {
				JsonObject headObj = e.getAsJsonObject();
				String url = headObj.get("url").getAsString();
				String name = headObj.get("name").getAsString();

				Head head = new Head(name, url);
				set.addHead(head);
				if (headObj.get("blocks") != null && headObj.get("blocks").isJsonArray()) {
					JsonArray blocks = headObj.get("blocks").getAsJsonArray();
					List<String> blockList = new ArrayList<String>();
					blocks.forEach(element -> {
						if (element.isJsonPrimitive()) {
							String blockId = element.getAsString();
							HeadsMod.getBlockDictionary().put(blockId, head);
							blockList.add(blockId);
						}
					});
				}
			}
		}
		JsonElement metadataElement = jsonSet.get("metadata");
		if (metadataElement != null && metadataElement.isJsonObject()) {
			JsonObject metadata = metadataElement.getAsJsonObject();
			JsonElement authorElement = metadata.get("author");
			if (authorElement != null) {
				set.setAuthor(authorElement.getAsString());
			}
			JsonElement descElement = metadata.get("description");
			if (descElement != null) {
				set.setDescription(descElement.getAsString());
			}
			JsonElement iconElement = metadata.get("icon");
			if (iconElement != null) {
				int iconIndex = iconElement.getAsInt();
				set.setIcon(set.getHeads().get(iconIndex));
			}

		}
		
		return set;

	}

	public JsonObject setToJson(HeadSet set) {
		JsonObject setObject = new JsonObject();
		setObject.addProperty("setName", set.getId());
		setObject.addProperty("displayName", set.getDisplayName());
		if (set.getAuthor() != null || set.getDescription() != null || set.getIcon() != null) {
			JsonObject metadata = new JsonObject();
			if (set.getAuthor() != null) {
				metadata.addProperty("author", set.getAuthor());
			}
			if (set.getDescription() != null) {
				metadata.addProperty("description", set.getDescription());
			}
			if (set.getIcon() != null) {
				metadata.addProperty("icon", set.getIconIndex());
			}
			setObject.add("metadata", metadata);
		}
		JsonArray headArray = new JsonArray();
		for (Head h : set.getHeads()) {
			headArray.add(headToJson(h));
		}
		setObject.add("heads", headArray);
		return setObject;
	}

	public JsonObject headToJson(Head head) {
		JsonObject headObject = new JsonObject();
		JsonArray blocks = new JsonArray();
		head.getBlocks().forEach(b -> {
			blocks.add(b);
		});
		headObject.add("blocks", blocks);
		headObject.addProperty("name", head.getName());
		headObject.addProperty("url", head.getURL());
		return headObject;
	}

	@SuppressWarnings("resource")
	public void saveToFile(HeadSet set) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Writer writer;
		String mcDir = MinecraftClient.getInstance().runDirectory.getPath();
		String subFolder = set.isCustom() ? "/user/" : "/";
		Path setPath = Paths.get(mcDir + HeadsMod.SETS_PATH + subFolder + set.getId() + ".json").toAbsolutePath();
		try {
			if(!Files.exists(setPath.getParent())) {
				Files.createDirectories(setPath.getParent());
			}
			if (!Files.exists(setPath)) {
				Files.createFile(setPath);
			}
			writer = Files.newBufferedWriter(setPath);
			gson.toJson(this.setToJson(set), writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			Text emsg = Text.of("\u00A7cError writing set to file");
			ClientPlayerEntity player = MinecraftClient.getInstance().player;
			if (player != null) {
				player.sendMessage(emsg);
			} else {
				System.out.println("Error writing set to file ");
			}
		}
	}

	public ArrayList<HeadSet> getSets() {
		return new ArrayList<HeadSet>(this.sets.values());
	}

}
