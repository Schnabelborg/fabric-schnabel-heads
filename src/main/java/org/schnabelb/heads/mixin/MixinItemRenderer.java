package org.schnabelb.heads.mixin;

import org.schnabelb.heads.injection.ItemRendererScaled;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer implements ItemRendererScaled {

	@Override
	public void renderGuiItemModel(ItemStack stack, int x, int y, float scale) {}
}