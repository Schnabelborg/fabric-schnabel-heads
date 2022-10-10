package org.schnabelb.heads.gui;

import java.util.ArrayList;
import java.util.List;

import org.schnabelb.heads.HeadsMod;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class HeadSelectionScreen extends CycleScreen {

	private List<ItemStack> heads;
	
	public HeadSelectionScreen(List<ItemStack> heads) {
		super(Text.of("Pick Head"), HeadsMod.pickHead);
		this.heads = new ArrayList<ItemStack>();
		this.heads.addAll(heads);
	}

	@Override
	protected int getCycleSize() {
		return heads.size();
	}

	@Override
	protected void drawElement(int i, int x, int y) {
		this.itemRenderer.renderGuiItemIcon(heads.get(i), x, y);
	}

	@Override
	protected void onClosed() {
		HeadsMod.givePickedHead(heads.get(this.selectedIndex), this.client);		
	}

	@Override
	protected String getCurrentText() {
		return heads.get(this.selectedIndex).getName().getString();
	}

	@Override
	protected void onCycle() {}

}
