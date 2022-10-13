package org.schnabelb.heads.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.schnabelb.heads.HeadSet;
import org.schnabelb.heads.HeadsMod;
import org.schnabelb.heads.SetManager;

import com.google.common.collect.Lists;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SaveHeadScreen extends CycleScreen {

	private ArrayList<HeadSet> sets;
	private static final Identifier texture = new Identifier(HeadsMod.MODID + ":" + "textures/gui/container/save_head_screen.png");

	public SaveHeadScreen() {
		super(Text.of("Save head"), HeadsMod.saveHead, texture);
		this.sets = HeadsMod.getSetManager().getSets();
	}

	@Override
	protected String getCurrentText() {
		switch(this.selectedIndex) {
		case 0:
			return "Cancel";
		case 1:
			return "Create new set...";
		default:
			return this.sets.get(this.selectedIndex - 2).getDisplayName();
		}
	}

	@Override
	protected void onCycle() {}

	@Override
	protected int getCycleSize() {
		return this.sets.size() + 2;
	}

	@Override
	protected void drawElement(MatrixStack matrices, int i, int x, int y) {
		switch(i) {
		case 0:
			drawTexture(matrices, x, y, 32, 99, 16, 16, 256, 256);
			break;
		case 1:
			drawTexture(matrices, x, y, 0, 99, 16, 16, 256, 256);
			break;
		default:
			this.itemRenderer.renderGuiItemIcon(sets.get(i - 2).getIcon().toItemStack(), x, y);
			break;
		}
		
	}

	@Override
	protected void onClosed() {
		this.client.player.sendMessage(this.title);
	}

}