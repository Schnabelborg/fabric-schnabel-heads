package org.schnabelb.heads.gui;

import java.util.ArrayList;
import org.schnabelb.heads.Head;
import org.schnabelb.heads.HeadSet;
import org.schnabelb.heads.HeadsMod;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SaveHeadScreen extends CycleScreen {

	private ArrayList<HeadSet> sets;
	private Head head;
	private static final Identifier texture = new Identifier(HeadsMod.MODID + ":" + "textures/gui/container/save_head_screen.png");

	public SaveHeadScreen(Head head) {
		super(Text.of("Save head"), HeadsMod.saveHead, texture);
		this.sets = HeadsMod.getSetManager().getSets();
		this.head = head;
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
			Head icon = sets.get(i - 2).getIcon();
			if(icon == null) {
				drawTexture(matrices, x, y, 16, 99, 16, 16, 256, 256);
			} else {
				this.itemRenderer.renderGuiItemIcon(icon.toItemStack(), x, y);
			}
			break;
		}
		
	}

	@Override
	protected void onClosed() {
		if (this.selectedIndex == 1) {
			CreateSetScreen screen = new CreateSetScreen(this.head);
			this.client.setScreen(screen);
		} else if(this.selectedIndex > 1) {
			sets.get(this.selectedIndex - 2).addHead(this.head);
		}
		
	}

}
