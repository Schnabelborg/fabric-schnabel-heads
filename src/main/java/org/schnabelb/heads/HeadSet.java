package org.schnabelb.heads;

import java.util.ArrayList;
import java.util.List;

public class HeadSet {
	
	private String id;
	private List<Head> heads;
	private String displayName;
	private String description;
	private Head icon;
	private String author;
	private long lastChanged;
	private boolean custom;
	
	public HeadSet(String id, String displayName) {
		this(id, displayName, new ArrayList<Head>(), System.currentTimeMillis());
	}
	
	public HeadSet(String id, String displayName, List<Head> heads, long lastChanged) {
		this.id = id;
		this.displayName = displayName;
		this.heads = heads;
		this.lastChanged = lastChanged;
	}
	
	public void addHead(Head head) {
		this.heads.add(head);
	}

	public List<Head> getHeads() {
		return heads;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Head getIcon() {
		if (this.icon != null) {
			return icon;			
		}
		if(this.heads.isEmpty()) {
			return null;
		}
		this.icon = heads.get(0);
		return icon;
	}
	
	public int getIconIndex() {
		return this.getHeads().indexOf(this.getIcon());
	}

	public void setIcon(Head icon) {
		this.icon = icon;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getId() {
		return id;
	}
	
	public long getLastChanged() {
		return this.lastChanged;
	}

	public void setLastChanged(long lastChanged) {
		this.lastChanged = lastChanged;
	}

	public boolean isCustom() {
		return custom;
	}

	public void setCustom(boolean custom) {
		this.custom = custom;
	}

	public void save() {
		this.lastChanged = System.currentTimeMillis();
		HeadsMod.getSetManager().saveToFile(this);
	}
	
	
}
