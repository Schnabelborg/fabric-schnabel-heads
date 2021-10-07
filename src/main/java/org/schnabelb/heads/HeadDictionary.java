package org.schnabelb.heads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.item.ItemStack;

public class HeadDictionary{
	
	private HashMap<String, List<ItemStack>> map;
	
	public HeadDictionary() {
		map = new HashMap<String, List<ItemStack>>();
	}
	
	public void put(String key, ItemStack value) {
		List<ItemStack> list = map.get(key);
		if(list == null) {
			list = new ArrayList<ItemStack>();
			this.map.put(key, list);
		}
		for(ItemStack old : list) {
			if(ItemStack.areEqual(value, old)) {
				return;
			}
		}
		list.add(value);
	}
	
	public List<ItemStack> get(String key) {
		List<ItemStack> list = this.map.get(key);
		return list != null ? list : new ArrayList<ItemStack>();
	}

}
