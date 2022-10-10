package org.schnabelb.heads.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;

@Mixin(ItemRenderer.class)
public interface ItemRendererAccessor {
	
	@Accessor
    float getZOffset();
	
	@Accessor
	TextureManager getTextureManager();

}
