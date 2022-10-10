package org.schnabelb.heads.gui;

import org.schnabelb.heads.HeadsMod;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public abstract class CycleScreen extends Screen {

	private static final Identifier texture = new Identifier(HeadsMod.MODID + ":" + "textures/gui/container/pick_head_screen.png");
	protected int selectedIndex;
	private float time = 0;
	private double accumulatedScrollDelta;
	private AbstractTexture pickHeadTexture = new ResourceTexture(texture);
	private KeyBinding cycleKey;
	private float closeTimer = 20;
	
	public CycleScreen(Text title, KeyBinding cycleKey) {
		super(title);
		this.selectedIndex = 0;
		this.cycleKey = cycleKey;
	}

	@Override
	protected void init() {
		client.getTextureManager().registerTexture(texture, pickHeadTexture);
		super.init();
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		matrixStack.push();
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, texture);
		drawTexture(matrixStack, this.width / 2 - 87, this.height / 2 - 57, 0, 0, 175, 75, 256, 256);
		matrixStack.pop();
		drawCenteredText(matrixStack, this.textRenderer, this.getCurrentText(), this.width / 2,
				this.height / 2 - 50, TextColor.fromFormatting(Formatting.YELLOW).getRgb());
		String num = (this.selectedIndex + 1) + "/" + this.getCycleSize();
		drawCenteredText(matrixStack, this.textRenderer, num, this.width / 2,
				this.height / 2 + 7, TextColor.fromFormatting(Formatting.GRAY).getRgb());
		int itemWidth = 16 + 9;
		int xOffset = selectedIndex * itemWidth;
		int y = (this.height - itemWidth) / 2 - 20;
		int first = Math.max(0, this.selectedIndex - 3);
		int last = Math.min(this.getCycleSize(), this.selectedIndex + 4);
		for (int i = first; i < last; i++) {
			int x = this.width / 2 + i * itemWidth - itemWidth / 2 - xOffset;
			matrixStack.push();
			RenderSystem.enableBlend();
			RenderSystem.setShaderTexture(0, texture);
			if((i == first && i > 0) || (i == last - 1 && i < this.getCycleSize() - 1)) {
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
			}
			matrixStack.translate((double) x, (double) y, 0);
			drawTexture(matrixStack, 0, 0, 0, 75, 25, 25, 256, 256);
			if (i == selectedIndex) {
				drawTexture(matrixStack, 0, 0, 25, 75, 25, 25, 256, 256);
			}
			matrixStack.pop();
			this.drawElement(i, x + 5, y + 5);
		}

		this.time += partialTicks;
		if (time >= closeTimer) {
			this.close();
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.cycleKey.matchesKey(keyCode, scanCode)) {
			selectedIndex = ((selectedIndex + 1) % this.getCycleSize());
			this.time = 0;
			this.onCycle();
		} else {
			this.close();
		}
		return false;
	}

	@Override
	public boolean mouseScrolled(double x, double y, double scrollAmount) {
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
			this.selectedIndex = this.selectedIndex < 0 ? this.getCycleSize() - 1 : this.selectedIndex;
		} else if (direction < 0) {
			this.selectedIndex++;
			this.selectedIndex = this.selectedIndex >= this.getCycleSize() ? 0 : this.selectedIndex;
		}
		this.time = 0;
		this.onCycle();
		return true;
	}



	@Override
	public boolean shouldPause() {
		return false;
	}

	@Override
	public void close() {
		this.client.setScreen(null);
		this.onClosed();
	}

	protected abstract String getCurrentText();

	protected void setCloseTimer(float closeTimer) {
		this.closeTimer = closeTimer;
	}

	protected abstract void onCycle();

	protected abstract int getCycleSize();

	protected abstract void drawElement(int i, int x, int y);

	protected abstract void onClosed();

}
