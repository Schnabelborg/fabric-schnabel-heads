package org.schnabelb.heads;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.lwjgl.glfw.GLFW;
import org.schnabelb.heads.gui.HeadSelectionScreen;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

@SuppressWarnings("resource")
public class HeadsMod implements ModInitializer {

	public static final String SETS_PATH = "/schnabelheads/sets";
	public static final String MODID = "schnabelheads";

	public static KeyBinding pickHead;
	public static KeyBinding saveHead;

	private static HeadDictionary blockMap;
	private static List<ItemStack> loadedHeads;

	private static final Identifier CREATIVE_TAB_ID = new Identifier(MODID, "heads");
	public static final ItemGroup SCHNABEL_HEADS = FabricItemGroup.builder()
			.icon(() -> classicHead())
			.displayName(Text.translatable("itemGroup.schnabelheads.heads"))
			.entries((context, stacks) -> {
				stacks.addAll(loadedHeads);
			}).build();

	private IdentifiableResourceReloadListener headReloadListener = new HeadReloadListener();

	@Override
	public void onInitialize() {
		blockMap = new HeadDictionary();
		pickHead = KeyBindingHelper.registerKeyBinding(
				new KeyBinding("key.schnabelheads.pickHead", GLFW.GLFW_KEY_V, "category.schnabelheads.keybinds"));
		saveHead = KeyBindingHelper.registerKeyBinding(
				new KeyBinding("key.schnabelheads.saveHead", GLFW.GLFW_KEY_B, "category.schnabelheads.keybinds"));
		Registry.register(Registries.ITEM_GROUP, CREATIVE_TAB_ID, SCHNABEL_HEADS);
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (pickHead.wasPressed()) {
				onPickHeadPressed(client);
			}
			if (saveHead.wasPressed()) {
				onSaveHeadPressed(client);
			}
		});
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(headReloadListener);
		loadItemData();
		System.out.println("Schnabelheads loaded");
	}

	private void onPickHeadPressed(MinecraftClient client) {
		HitResult raycast = client.crosshairTarget;
		if (raycast.getType() == HitResult.Type.BLOCK) {
			BlockPos pos = ((BlockHitResult) raycast).getBlockPos();
			BlockState blockState = client.world.getBlockState(pos);
			Block block = blockState.getBlock();
			Identifier id = Registries.BLOCK.getId(block);
			List<ItemStack> availableHeads = blockMap.get(id.getPath());

			if (block == Blocks.PLAYER_HEAD || block == Blocks.PLAYER_WALL_HEAD) {
				BlockEntity blockEntity = client.world.getBlockEntity(pos);
				ItemStack result = new ItemStack(block.asItem());
				if (blockEntity != null && blockEntity instanceof SkullBlockEntity) {
					SkullBlockEntity skullBE = (SkullBlockEntity) blockEntity;
					if (skullBE.getOwner() != null && skullBE.getOwner().getProperties() != null) {
						ArrayList<Property> textures = Lists
								.newArrayList(skullBE.getOwner().getProperties().get("textures"));
						String texture = textures.get(0).value();
						if (texture.startsWith("\"") && texture.endsWith("\"")) {
							texture = texture.substring(1, texture.length() - 1);
						}
						result = getHeadByUrl(texture);
						if (result.isEmpty()) {
							result = getHead(texture, null, null);
						}
						System.out.println(getHeadByUrl(texture));
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
				HeadSelectionScreen screen = new HeadSelectionScreen(availableHeads);
				client.setScreen(screen);
			}
		}
	}

	private void onSaveHeadPressed(MinecraftClient client) {
		ItemStack heldItem = client.player.getStackInHand(Hand.MAIN_HAND);
		if (getHeadUrl(heldItem) != null) {
			
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

	private static ItemStack getHeadByUrl(String url) {
		for (ItemStack head : loadedHeads) {
			NbtCompound tag = head.getNbt();
			if (head.getItem() != Items.PLAYER_HEAD || tag == null) {
				continue;
			}
			NbtCompound skullOwner = (NbtCompound) tag.get("SkullOwner");
			NbtCompound properties = (NbtCompound) skullOwner.get("Properties");
			NbtList textures = (NbtList) properties.get("textures");
			NbtCompound texture = (NbtCompound) textures.get(0);
			String itemUrl = texture.getString("Value");
			if (itemUrl.equals(url)) {
				return head;
			}
		}
		return ItemStack.EMPTY;
	}

	/*private static List<ItemStack> addEmptySlots(int n) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (int i = 0; i < n; i++) {
			list.add(ItemStack.EMPTY);
		}
		return list;
	}*/

	private static ItemStack getHead(String url, String name, String setName) {
		NbtCompound tag = new NbtCompound();
		NbtCompound skullOwner = new NbtCompound();
		NbtCompound properties = new NbtCompound();
		NbtList textures = new NbtList();
		NbtCompound texture = new NbtCompound();

		texture.putString("Value", url);
		textures.add(texture);
		properties.put("textures", textures);
		skullOwner.putUuid("Id", UUID.fromString("8ea77fe0-23e8-427d-9a96-4c058c612c61"));
		skullOwner.put("Properties", properties);
		tag.put("SkullOwner", skullOwner);

		if (name != null || setName != null) {
			NbtCompound display = new NbtCompound();

			if (name != null) {
				display.putString("Name", "{\"text\":\"" + name + "\",\"italic\":false}");
			}

			if (setName != null) {
				NbtList lore = new NbtList();
				NbtString loreText = NbtString
						.of("{\"text\":\"Schnabel's " + setName + " Set\",\"italic\":false,\"color\":\"blue\"}");
				lore.add(loreText);
				display.put("Lore", lore);
			}

			tag.put("display", display);
		}
		ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
		stack.setNbt(tag);
		return stack;
	}

	@SuppressWarnings("deprecation")
	private static ArrayList<JsonObject> loadHeadData() {
		ArrayList<JsonObject> sets = new ArrayList<JsonObject>();
		String mcDir = MinecraftClient.getInstance().runDirectory.getPath();
		File setsFolder = new File(mcDir + SETS_PATH);
		if (!setsFolder.exists()) {
			setsFolder.mkdirs();
		}
		File[] setFiles = setsFolder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File f, String s) {
				return s.toLowerCase().endsWith(".json");
			}
		});
		if (setFiles == null) {
			return sets;
		}
		JsonParser parser = new JsonParser();
		for (File f : setFiles) {
			Reader reader;
			try {
				reader = new BufferedReader(new FileReader(f));
				JsonObject object = parser.parse(reader).getAsJsonObject();
				sets.add(object);
			} catch (Exception e) {
				e.printStackTrace();
				Text emsg = Text.of("\u00A7cError loading heads from \u00A74" + f.getName());
				ClientPlayerEntity player = MinecraftClient.getInstance().player;
				if (player != null) {
					player.sendMessage(emsg);
				} else {
					System.out.println("Error loading heads from " + f.getName());
				}
			}
		}
		return sets;

	}

	public static void loadItemData() {
		blockMap = new HeadDictionary();
		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		ArrayList<JsonObject> sets = loadHeadData();
		for (JsonObject set : sets) {
			try {
				JsonElement displayNameElement = set.get("displayName");
				String setName = displayNameElement == null ? "Schnabelheads" : displayNameElement.getAsString();
				JsonArray heads = set.get("heads").getAsJsonArray();
				for (JsonElement e : heads) {
					JsonObject head = e.getAsJsonObject();
					String url = head.get("url").getAsString();
					String name = head.get("name").getAsString();
					ItemStack stack = getHead(url, name, setName);
					stacks.add(stack);
					if (head.get("blocks") != null && head.get("blocks").isJsonArray()) {
						JsonArray blocks = head.get("blocks").getAsJsonArray();
						blocks.forEach(element -> {
							if (element.isJsonPrimitive()) {
								String blockId = element.getAsString();
								addToBlockMap(blockId, stack);
							}
						});
					}
				}
				//int num = heads.size();
				//stacks.addAll(addEmptySlots(9 - num % 9));
			} catch (Exception e) {
				e.printStackTrace();
				Text emsg = Text.of("\u00A7cError loading heads");
				ClientPlayerEntity player = MinecraftClient.getInstance().player;
				if (player != null) {
					player.sendMessage(emsg);
				} else {
					System.out.println("Error loading heads");
				}
			}
		}
		loadedHeads = stacks;
	}

	private static void addToBlockMap(String blockId, ItemStack stack) {
		blockMap.put(blockId, stack);

	}

	private static String getHeadUrl(ItemStack head) {
		if (head.getItem() == Items.PLAYER_HEAD) {
			NbtCompound headNbt = head.getNbt();
			if (headNbt != null && headNbt.contains("SkullOwner", NbtCompound.COMPOUND_TYPE)) {
				return ((NbtCompound) headNbt.getCompound("SkullOwner").getCompound("Properties").getList("textures", NbtList.COMPOUND_TYPE).get(0)).getString("Value");
			}
		}
		return null;
	}
	
	public static ItemStack classicHead() {
		NbtCompound tag = new NbtCompound();
		tag.putString("SkullOwner", "Schnabelborg");

		ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
		stack.setNbt(tag);
		return stack;
	}
}
