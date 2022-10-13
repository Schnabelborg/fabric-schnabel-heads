package org.schnabelb.heads.gui;

import java.util.ArrayList;
import java.util.List;

import org.schnabelb.heads.HeadsMod;
import org.schnabelb.heads.listener.KeyListener;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PickHeadScreen extends CycleScreen {

	private List<ItemStack> heads;
	private static final Identifier texture = new Identifier(HeadsMod.MODID + ":" + "textures/gui/container/pick_head_screen.png");
	
	public PickHeadScreen(List<ItemStack> heads) {
		super(Text.of("Pick Head"), HeadsMod.pickHead, texture);
		this.heads = new ArrayList<ItemStack>();
		this.heads.addAll(heads);
	}

	@Override
	protected int getCycleSize() {
		return heads.size();
	}

	@Override
	protected void drawElement(MatrixStack matrices, int i, int x, int y) {
		this.itemRenderer.renderGuiItemIcon(heads.get(i), x, y);
	}

	@Override
	protected void onClosed() {
		KeyListener.givePickedHead(heads.get(this.selectedIndex), this.client);		
	}

	@Override
	protected String getCurrentText() {
		return heads.get(this.selectedIndex).getName().getString();
	}

	@Override
	protected void onCycle() {}

}
