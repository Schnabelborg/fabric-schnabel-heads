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
	
	public HeadSet(String id, String displayName) {
		this(id, displayName, new ArrayList<Head>());
	}
	
	public HeadSet(String id, String displayName, List<Head> heads) {
		this.id = id;
		this.displayName = displayName;
		this.heads = heads;
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
		this.icon = heads.get(0);
		return icon;
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
	
}
