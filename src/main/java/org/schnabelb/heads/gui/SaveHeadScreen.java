package org.schnabelb.heads.gui;

import java.util.List;

import org.schnabelb.heads.HeadsMod;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class SaveHeadScreen extends CycleScreen {

	private List<ItemStack> sets = Lists.newArrayList();

	public SaveHeadScreen() {
		super(Text.of("Save head"), HeadsMod.saveHead);
	}

	@Override
	protected String getCurrentText() {
		switch(this.selectedIndex) {
		case 0:
			return "Cancel";
		case 1:
			return "Create new set...";
		default:
			return this.sets.get(this.selectedIndex - 2).getName().getString();
		}
	}

	@Override
	protected void onCycle() {}

	@Override
	protected int getCycleSize() {
		return this.sets.size() + 2;
	}

	@Override
	protected void drawElement(int i, int x, int y) {
		switch(i) {
		case 0:
			this.itemRenderer.renderGuiItemIcon(new ItemStack(Items.BARRIER), x, y);
			break;
		case 1:
			this.itemRenderer.renderGuiItemIcon(new ItemStack(Items.LIME_DYE), x, y);
			break;
		default:
			this.itemRenderer.renderGuiItemIcon(sets.get(i - 2), x, y);
			break;
		}
		
	}

	@Override
	protected void onClosed() {
		this.client.player.sendMessage(this.title);
	}

}
