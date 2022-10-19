package org.schnabelb.heads;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.schnabelb.heads.listener.HeadReloadListener;
import org.schnabelb.heads.listener.KeyListener;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@SuppressWarnings("resource")
public class HeadsMod implements ModInitializer {

	public static final String SETS_PATH = "/schnabelheads/sets";
	public static final String MODID = "schnabelheads";

	public static KeyBinding pickHead;
	public static KeyBinding saveHead;

	private static BlockDictionary blockDictionary;
	private static SetManager setManager;

	private IdentifiableResourceReloadListener headReloadListener = new HeadReloadListener();
	public static final ItemGroup SCHNABEL_HEADS = FabricItemGroupBuilder.create(new Identifier(MODID, "heads"))
			.icon(() -> classicHead()).appendItems(stacks -> {
				stacks.addAll(setManager.fillCreativeTab());
			}).build();

	@Override
	public void onInitialize() {
		setManager = new SetManager();
		blockDictionary = new BlockDictionary();
		pickHead = KeyBindingHelper.registerKeyBinding(
				new KeyBinding("key.schnabelheads.pickHead", GLFW.GLFW_KEY_V, "category.schnabelheads.keybinds"));
		saveHead = KeyBindingHelper.registerKeyBinding(
				new KeyBinding("key.schnabelheads.saveHead", GLFW.GLFW_KEY_B, "category.schnabelheads.keybinds"));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (pickHead.wasPressed()) {
				KeyListener.onPickHeadPressed(client);
			}
			if (saveHead.wasPressed()) {
				KeyListener.onSaveHeadPressed(client);
			}
		});
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(headReloadListener);
		loadItemData();
		System.out.println("Schnabelheads loaded");
	}

	@SuppressWarnings("deprecation")
	public static void loadItemData() {
		blockDictionary = new BlockDictionary();
		setManager = new SetManager();
		String mcDir = MinecraftClient.getInstance().runDirectory.getPath();
		File setsFolder = new File(mcDir + SETS_PATH);
		if (!setsFolder.exists()) {
			setsFolder.mkdirs();
		}
		File customSetsFolder = new File(mcDir + SETS_PATH + "/user");
		if (!customSetsFolder.exists()) {
			customSetsFolder.mkdirs();
		}

		List<File> setFiles = new ArrayList<File>();
		FilenameFilter jsonFilter = new FilenameFilter() {
			@Override
			public boolean accept(File f, String s) {
				return s.toLowerCase().endsWith(".json");
			}
		};
		File[] found = setsFolder.listFiles(jsonFilter);
		if (found != null) {
			setFiles.addAll(Arrays.asList(found));
		}
		found = customSetsFolder.listFiles(jsonFilter);
		if (found != null) {
			setFiles.addAll(Arrays.asList(found));
		}
		JsonParser parser = new JsonParser();
		for (File f : setFiles) {
			Reader reader;
			try {
				reader = new BufferedReader(new FileReader(f));
				JsonObject object = parser.parse(reader).getAsJsonObject();
				HeadSet set = setManager.parseJsonSet(object);
				set.setLastChanged(Files.getLastModifiedTime(Paths.get(f.getPath())).toMillis());
				boolean custom = !Paths.get(f.getParent()).equals(Paths.get(setsFolder.getPath()));
				set.setCustom(custom);
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
	}

	public static ItemStack classicHead() {
		NbtCompound tag = new NbtCompound();
		tag.putString("SkullOwner", "Schnabelborg");

		ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
		stack.setNbt(tag);
		return stack;
	}

	public static SetManager getSetManager() {
		return setManager;
	}

	public static BlockDictionary getBlockDictionary() {
		return blockDictionary;
	}
}
