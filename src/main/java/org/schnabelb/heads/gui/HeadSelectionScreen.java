package org.schnabelb.heads.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.schnabelb.heads.HeadsMod;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class HeadSelectionScreen extends Screen {

	private static final Identifier texture = new Identifier(HeadsMod.MODID + ":" + "textures/gui/container/pick_head_screen.png");
	private List<ItemStack> heads;
	private int selectedIndex;
	private float time = 0;
	private double accumulatedScrollDelta;
	private AbstractTexture pickHeadTexture = new ResourceTexture(texture);

	public HeadSelectionScreen(List<ItemStack> heads) {
		super(Text.of("Pick Head"));
		this.heads = new ArrayList<ItemStack>();
		this.heads.addAll(heads);
		this.selectedIndex = 0;
	}

	@Override
	protected void init() {
		client.getTextureManager().registerTexture(texture, pickHeadTexture);
		super.init();
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
		context.getMatrices().push();
		RenderSystem.enableBlend();
		context.drawTexture(texture, this.width / 2 - 87, this.height / 2 - 57, 0, 0, 175, 75, 256, 256);
		context.getMatrices().pop();
		ItemStack selected = heads.get(this.selectedIndex);
		context.drawCenteredTextWithShadow(this.textRenderer, selected.getName(), this.width / 2,
				this.height / 2 - 50, TextColor.fromFormatting(Formatting.YELLOW).getRgb());
		String num = (this.selectedIndex + 1) + "/" + this.heads.size();
		context.drawCenteredTextWithShadow(this.textRenderer, num, this.width / 2,
				this.height / 2 + 7, TextColor.fromFormatting(Formatting.GRAY).getRgb());
		int itemWidth = 16 + 9;
		int xOffset = selectedIndex * itemWidth;
		int y = (this.height - itemWidth) / 2 - 20;
		int first = Math.max(0, this.selectedIndex - 3);
		int last = Math.min(heads.size(), this.selectedIndex + 4);
		for (int i = first; i < last; i++) {
			int x = this.width / 2 + i * itemWidth - itemWidth / 2 - xOffset;
			context.getMatrices().push();
			RenderSystem.enableBlend();
			//RenderSystem.setShaderTexture(0, texture);
			if((i == first && i > 0) || (i == last - 1 && i < heads.size() - 1)) {
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
			}
			context.getMatrices().translate((double) x, (double) y, 0);
			context.drawTexture(texture, 0, 0, 0, 75, 25, 25, 256, 256);
			if (i == selectedIndex) {
				context.drawTexture(texture, 0, 0, 25, 75, 25, 25, 256, 256);
			}
			context.getMatrices().pop();
			context.drawItem(heads.get(i), x + 5, y + 5);
		}

		this.time += partialTicks;
		if (time >= 20) {
			this.close();
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_V) {
			selectedIndex = ((selectedIndex + 1) % heads.size());
			this.time = 0;
		} else {
			this.close();
		}
		return false;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double scrollAmount) {
		if (this.accumulatedScrollDelta != 0.0D && Math.signum(scrollAmount) != Math.signum(this.accumulatedScrollDelta)) {
			this.accumulatedScrollDelta = 0.0D;
		}
		this.accumulatedScrollDelta += scrollAmount;
		double direction = Math.floor(this.accumulatedScrollDelta);
		if (direction == 0.0F) {
			return false;
		}

		this.accumulatedScrollDelta -= direction;
		if (direction > 0) {
			this.selectedIndex--;
			this.selectedIndex = this.selectedIndex < 0 ? this.heads.size() - 1 : this.selectedIndex;
		} else if (direction < 0) {
			this.selectedIndex++;
			this.selectedIndex = this.selectedIndex >= this.heads.size() ? 0 : this.selectedIndex;
		}
		this.time = 0;
		return true;
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	public void close() {
		this.client.setScreen(null);
		HeadsMod.givePickedHead(heads.get(this.selectedIndex), this.client);
	}

}
