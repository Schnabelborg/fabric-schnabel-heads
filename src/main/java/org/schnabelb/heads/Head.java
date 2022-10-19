package org.schnabelb.heads;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

public class Head {

	private List<String> blocks;
	private String name;
	private String url;
	
	public Head(String name, String url, List<String> blocks) {
		this.name = name;
		this.url = url;
		this.blocks = blocks;
	}
	
	public Head(String name, String url) {
		this(name, url, new ArrayList<String>());
	}

	public List<String> getBlocks() {
		return blocks;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getURL() {
		return url;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Head)) {
			return false;
		}
		Head otherHead = (Head) other;
		return otherHead.url == this.url && otherHead.name == this.name;
	}

	public ItemStack toItemStack() {
		NbtCompound tag = new NbtCompound();
		NbtCompound skullOwner = new NbtCompound();
		NbtCompound properties = new NbtCompound();
		NbtList textures = new NbtList();
		NbtCompound texture = new NbtCompound();

		texture.putString("Value", this.url);
		textures.add(texture);
		properties.put("textures", textures);
		skullOwner.putUuid("Id", UUID.fromString("8ea77fe0-23e8-427d-9a96-4c058c612c61"));
		skullOwner.put("Properties", properties);
		tag.put("SkullOwner", skullOwner);

		HeadSet set = HeadsMod.getSetManager().getSet(this);
		
		if (name != null || set != null) {
			NbtCompound display = new NbtCompound();
			if (name != null) {
				display.putString("Name", "{\"text\":\"" + name + "\",\"italic\":false}");
			}


			if (set != null) {
				NbtList lore = new NbtList();
				NbtString loreText = NbtString
						.of("{\"text\":\"Schnabel's " + set.getDisplayName() + " Set\",\"italic\":false,\"color\":\"blue\"}");
				lore.add(loreText);
				display.put("Lore", lore);
			}

			tag.put("display", display);
		}
		ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
		stack.setNbt(tag);
		return stack;
	}
	
	
}
