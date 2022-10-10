package org.schnabelb.heads.injection;

import org.schnabelb.heads.mixin.ItemRendererAccessor;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public interface ItemRendererScaled {
	
	default void renderGuiItemModel(ItemStack stack, int x, int y, float scale) {
		boolean bl;
		ItemRenderer thisObj = (ItemRenderer) (Object) this;
		BakedModel model = thisObj.getModel(stack, null, null, 0);
		TextureManager textureManager = ((ItemRendererAccessor) thisObj).getTextureManager();
		textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
		RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		MatrixStack matrixStack = RenderSystem.getModelViewStack();
		matrixStack.push();
		float zOffset = ((ItemRendererAccessor) thisObj).getZOffset();
		matrixStack.translate(x, y, 100.0f + zOffset);
		matrixStack.translate(8.0 * scale, 8.0 * scale, 0.0);
		matrixStack.scale(1.0f, -1.0f, 1.0f);
		matrixStack.scale(16.0f * scale, 16.0f * scale, 16.0f * scale);
		RenderSystem.applyModelViewMatrix();
		MatrixStack matrixStack2 = new MatrixStack();
		VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders()
				.getEntityVertexConsumers();
		boolean bl2 = bl = !model.isSideLit();
		if (bl) {
			DiffuseLighting.disableGuiDepthLighting();
		}
		thisObj.renderItem(stack, ModelTransformation.Mode.GUI, false, matrixStack2, immediate,
				LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, model);
		immediate.draw();
		RenderSystem.enableDepthTest();
		if (bl) {
			DiffuseLighting.enableGuiDepthLighting();
		}
		matrixStack.pop();
		RenderSystem.applyModelViewMatrix();
	}
}