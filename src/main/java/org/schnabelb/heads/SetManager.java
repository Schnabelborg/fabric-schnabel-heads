package org.schnabelb.heads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;

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
		for(HeadSet set : sets.values()) {
			if(set.getHeads().contains(head)) {
				return set;
			}
		}
		return null;
	}

	public Head getHeadByUrl(String url) {
		for (HeadSet set : sets.values()) {
			for(Head head : set.getHeads()) {
				if (head.getURL().equals(url)) {
					return head;
				}				
			}
		}
		
		return null;
	}
	
	public List<ItemStack> fillCreativeTab() {
		List<ItemStack> itemList = new ArrayList<ItemStack>();
		System.out.println(sets.size());
		sets.values().forEach(set -> {
			set.getHeads().forEach(head -> {
				itemList.add(head.toItemStack());
			});
			for(int i = 0; i < (9 - (set.getHeads().size() % 9)) %9; i++) {
				itemList.add(ItemStack.EMPTY);
			}
		});
		return itemList;
	}

	public void parseJsonSet(JsonObject jsonSet) {
		JsonElement setNameElement = jsonSet.get("setName");
		if(setNameElement == null) {
			throw new IllegalArgumentException();
		}
		String setName = setNameElement.getAsString();
		JsonElement displayNameElement = jsonSet.get("displayName");
		String displayName = displayNameElement == null ? setName : displayNameElement.getAsString();
		HeadSet set = new HeadSet(setName, displayName);
		this.sets.put(setName, set);
		JsonElement heads = jsonSet.get("heads");
		if(heads != null && heads.isJsonArray()) {
			for (JsonElement e : heads.getAsJsonArray()) {
				JsonObject headObj = e.getAsJsonObject();
				String url = headObj.get("url").getAsString();
				String name = headObj.get("name").getAsString();

				Head head = new Head(name, url);
				set.addHead(head);
				if (headObj.get("blocks") != null && headObj.get("blocks").isJsonArray()) {
					JsonArray blocks = headObj.get("blocks").getAsJsonArray();
					blocks.forEach(element -> {
						if (element.isJsonPrimitive()) {
							String blockId = element.getAsString();
							HeadsMod.getBlockDictionary().put(blockId, head);
						}
					});
				}
			}			
		}
		JsonElement metadataElement = jsonSet.get("metadata");
		if(metadataElement != null && metadataElement.isJsonObject()) {
			JsonObject metadata = metadataElement.getAsJsonObject();
			JsonElement authorElement = metadata.get("author");
			if(authorElement != null) {
				set.setAuthor(authorElement.getAsString());
			}
			JsonElement descElement = metadata.get("description");
			if(descElement != null) {
				set.setDescription(descElement.getAsString());
			}
			JsonElement iconElement = metadata.get("icon");
			if(iconElement != null) {
				int iconIndex = iconElement.getAsInt();
				set.setIcon(set.getHeads().get(iconIndex));
			}
			
		}
		
	}

	public ArrayList<HeadSet> getSets() {
		return new ArrayList<HeadSet>(this.sets.values());
	}
	
}
