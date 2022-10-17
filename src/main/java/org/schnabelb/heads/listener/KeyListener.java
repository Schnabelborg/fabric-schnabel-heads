package org.schnabelb.heads.listener;

import java.util.ArrayList;
import java.util.List;
import org.schnabelb.heads.Head;
import org.schnabelb.heads.HeadsMod;
import org.schnabelb.heads.gui.PickHeadScreen;
import org.schnabelb.heads.gui.SaveHeadScreen;

import com.google.common.collect.Lists;
import com.mojang.authlib.properties.Property;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class KeyListener {

	public static void onPickHeadPressed(MinecraftClient client) {
		HitResult raycast = client.crosshairTarget;
		if (raycast.getType() == HitResult.Type.BLOCK) {
			BlockPos pos = ((BlockHitResult) raycast).getBlockPos();
			BlockState blockState = client.world.getBlockState(pos);
			Block block = blockState.getBlock();
			Identifier id = Registry.BLOCK.getId(block);
			List<Head> heads = HeadsMod.getBlockDictionary().get(id.getPath());
			List<ItemStack> availableHeads = new ArrayList<ItemStack>(heads.stream().map(head -> head.toItemStack()).toList());

			if (block == Blocks.PLAYER_HEAD || block == Blocks.PLAYER_WALL_HEAD) {
				BlockEntity blockEntity = client.world.getBlockEntity(pos);
				ItemStack result = new ItemStack(block.asItem());
				if (blockEntity != null && blockEntity instanceof SkullBlockEntity) {
					SkullBlockEntity skullBE = (SkullBlockEntity) blockEntity;
					if (skullBE.getOwner() != null && skullBE.getOwner().getProperties() != null) {
						ArrayList<Property> textures = Lists
								.newArrayList(skullBE.getOwner().getProperties().get("textures"));
						String texture = textures.get(0).getValue();
						if (texture.startsWith("\"") && texture.endsWith("\"")) {
							texture = texture.substring(1, texture.length() - 1);
						}
						Head head = HeadsMod.getSetManager().getHeadByUrl(texture);
						if (head == null) {
							result = new Head(null, texture).toItemStack();
						} else {
							result = head.toItemStack();							
						}
					}
					availableHeads.add(result);
				}
			}

			if (!client.player.getAbilities().creativeMode) {
				List<ItemStack> inventoryHeads = new ArrayList<ItemStack>();
				for (ItemStack availableHead : availableHeads) {
					if (client.player.getInventory().getSlotWithStack(availableHead) != -1) {
						inventoryHeads.add(availableHead);
					}
				}
				availableHeads = inventoryHeads;
			}
			if (availableHeads.isEmpty()) {
				return;
			} else if (availableHeads.size() == 1) {
				givePickedHead(availableHeads.get(0), client);
			} else {
				PickHeadScreen screen = new PickHeadScreen(availableHeads);
				client.setScreen(screen);
			}
		}
	}

	public static void onSaveHeadPressed(MinecraftClient client) {
		HitResult raycast = client.crosshairTarget;
		if (raycast.getType() == HitResult.Type.BLOCK) {
			BlockPos pos = ((BlockHitResult) raycast).getBlockPos();
			BlockState blockState = client.world.getBlockState(pos);
			Block block = blockState.getBlock();
			if (block == Blocks.PLAYER_HEAD || block == Blocks.PLAYER_WALL_HEAD) {
				BlockEntity blockEntity = client.world.getBlockEntity(pos);
				if (blockEntity != null && blockEntity instanceof SkullBlockEntity) {
					SkullBlockEntity skullBE = (SkullBlockEntity) blockEntity;
					if (skullBE.getOwner() != null && skullBE.getOwner().getProperties() != null) {
						ArrayList<Property> textures = Lists
								.newArrayList(skullBE.getOwner().getProperties().get("textures"));
						String texture = textures.get(0).getValue();
						SaveHeadScreen screen = new SaveHeadScreen(new Head("", texture));
						client.setScreen(screen);
					}
				}
			}
		}
	}
	
	public static void givePickedHead(ItemStack pickedHead, MinecraftClient client) {
		ClientPlayerEntity player = client.player;
		PlayerInventory playerinventory = player.getInventory();
		int i = playerinventory.getSlotWithStack(pickedHead);
		if (player.getAbilities().creativeMode) {
			playerinventory.addPickBlock(pickedHead);
			client.interactionManager.clickCreativeStack(pickedHead, playerinventory.selectedSlot + 36);

		} else if (i != -1) {
			if (PlayerInventory.isValidHotbarIndex(i)) {
				playerinventory.selectedSlot = i;
			} else {
				client.interactionManager.pickFromInventory(i);
			}
		}
	}
}
