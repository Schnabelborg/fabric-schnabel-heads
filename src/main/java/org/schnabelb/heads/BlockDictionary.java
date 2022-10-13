package org.schnabelb.heads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockDictionary{
	
	private HashMap<String, List<Head>> map;
	
	public BlockDictionary() {
		map = new HashMap<String, List<Head>>();
	}
	
	public void put(String key, Head head) {
		List<Head> list = map.get(key);
		if(list == null) {
			list = new ArrayList<Head>();
			this.map.put(key, list);
		}
		for(Head old : list) {
			if(head.equals(old)) {
				return;
			}
		}
		list.add(head);
	}
	
	public List<Head> get(String key) {
		List<Head> list = this.map.get(key);
		return list != null ? list : new ArrayList<Head>();
	}

}
