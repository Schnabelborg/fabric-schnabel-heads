package org.schnabelb.heads.mixin;

import org.schnabelb.heads.Head;
import org.schnabelb.heads.HeadsMod;
import org.schnabelb.heads.gui.SaveHeadScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

@Mixin(HandledScreen.class)
public abstract class MixinHandledScreen<T extends ScreenHandler> extends Screen {


	protected MixinHandledScreen(Text title) {
		super(title);
	}

	@Inject(at = @At("TAIL"), method = "keyPressed(III)Z")
	public void handleSaveHeadPress(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> info) {
		//if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
            if (HeadsMod.saveHead.matchesKey(keyCode, scanCode)) {
            	Slot focusedSlot = ((HandledScreenAccessor) this).getFocusedSlot();
                if(focusedSlot != null && focusedSlot.getStack() != null) {
                	ItemStack stack = focusedSlot.getStack();
                	if(stack.getItem().equals(Items.PLAYER_HEAD)) {
                		String url = "";
                		if(stack.getNbt() != null && stack.getNbt().getCompound("SkullOwner") != null && stack.getNbt().getCompound("SkullOwner").getCompound("Properties") != null) {
                			NbtList textures = stack.getNbt().getCompound("SkullOwner").getCompound("Properties").getList("textures", NbtList.COMPOUND_TYPE);
                			if (textures != null && !textures.isEmpty() && textures.getCompound(0) != null) {
                				url = textures.getCompound(0).getString("Value");
                			}
                		}
                		this.client.setScreen(new SaveHeadScreen(new Head(stack.getName().getString(), url)));
                	}
                }
                
            }
        //}
	}
	
	
	/*
	 * @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            this.close();
            return true;
        }
        this.handleHotbarKeyPressed(keyCode, scanCode);
        if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
            if (this.client.options.pickItemKey.matchesKey(keyCode, scanCode)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 0, SlotActionType.CLONE);
            } else if (this.client.options.dropKey.matchesKey(keyCode, scanCode)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, HandledScreen.hasControlDown() ? 1 : 0, SlotActionType.THROW);
            }
        }
        return true;
    }
	 * */
	
	
}
