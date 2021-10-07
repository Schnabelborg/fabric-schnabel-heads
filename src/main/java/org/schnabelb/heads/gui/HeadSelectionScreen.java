package org.schnabelb.heads.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.schnabelb.heads.HeadsMod;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
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

	public HeadSelectionScreen(List<ItemStack> heads) {
		super(Text.of("Kacka Pupu"));
		this.heads = new ArrayList<ItemStack>();
		this.heads.addAll(heads);
		this.selectedIndex = 0;
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		matrixStack.push();
		RenderSystem.enableBlend();
		client.getTextureManager().bindTexture(texture);
		drawTexture(matrixStack, this.width / 2 - 87, this.height / 2 - 57, 0, 0, 175, 75, 256, 256);
		matrixStack.pop();
		ItemStack selected = heads.get(this.selectedIndex);
		drawCenteredText(matrixStack, this.textRenderer, selected.getName().asString(), this.width / 2,
				this.height / 2 - 50, TextColor.fromFormatting(Formatting.YELLOW).getRgb());
		String num = (this.selectedIndex + 1) + "/" + this.heads.size();
		drawCenteredText(matrixStack, this.textRenderer, num, this.width / 2,
				this.height / 2 + 7, TextColor.fromFormatting(Formatting.GRAY).getRgb());
		int itemWidth = 16 + 9;
		int xOffset = selectedIndex * itemWidth;
		int y = (this.height - itemWidth) / 2 - 20;
		int first = Math.max(0, this.selectedIndex - 3);
		int last = Math.min(heads.size(), this.selectedIndex + 4);
		for (int i = first; i < last; i++) {
			int x = this.width / 2 + i * itemWidth - itemWidth / 2 - xOffset;
			matrixStack.push();
			RenderSystem.enableBlend();
			client.getTextureManager().bindTexture(texture);
			if((i == first && i > 0) || (i == last - 1 && i < heads.size() - 1)) {
				//RenderSystem.color4f(1, 1, 1, 0.5F);
			}
			matrixStack.translate((double) x, (double) y, 0);
			drawTexture(matrixStack, 0, 0, 0, 75, 25, 25, 256, 256);
			if (i == selectedIndex) {
				drawTexture(matrixStack, 0, 0, 25, 75, 25, 25, 256, 256);
			}
			matrixStack.pop();
			this.itemRenderer.renderGuiItemIcon(heads.get(i), x + 5, y + 5);
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
	public boolean mouseScrolled(double x, double y, double scrollAmount) {
		//double d0 = (this.client.options.discreteMouseScroll ? Math.signum(y) : y) * this.client.options.mouseWheelSensitivity;
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
		return true; // TODO: Das muehsemn wrir nohc am eings Ehnde aedndrern...
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	public void close() {
		this.client.setScreen(null);
		HeadsMod.givePickedHead(heads.get(this.selectedIndex), this.client);
	}

}
