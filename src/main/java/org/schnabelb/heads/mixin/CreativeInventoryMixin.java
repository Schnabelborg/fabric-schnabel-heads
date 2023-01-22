package org.schnabelb.heads.mixin;

import java.util.Locale;
import java.util.Objects;

import org.lwjgl.glfw.GLFW;
import org.schnabelb.heads.Head;
import org.schnabelb.heads.HeadsMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen.CreativeScreenHandler;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchProvider;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryMixin extends AbstractInventoryScreen<CreativeScreenHandler> {

	public CreativeInventoryMixin(CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

	@Inject(method = "search()V", at = @At("HEAD"), cancellable = true)
	private void search(CallbackInfo info) {
		ItemGroup itemGroup = ItemGroup.GROUPS[((CreativeInventoryAccessor)this).invokeGetSelectedTab()];
		if(itemGroup == HeadsMod.SCHNABEL_HEADS) {
			((CreativeScreenHandler) this.handler).itemList.clear();
			((CreativeInventoryAccessor) this).getSearchResultTags().clear();
			String query = ((CreativeInventoryAccessor) this).getSearchBox().getText();
			if (query.isEmpty()) {
				for (Head h : HeadsMod.getSetManager().getAllHeads()) {
					((CreativeScreenHandler)this.handler).itemList.add(h.toItemStack());
				}
			} else {
				SearchProvider<ItemStack> searchProvider;
				searchProvider = this.client.getSearchProvider(SearchManager.ITEM_TOOLTIP);
				((CreativeScreenHandler)this.handler).itemList.addAll(searchProvider.findAll(query.toLowerCase(Locale.ROOT)));
			}
			((CreativeInventoryAccessor) this).setScrollPosition(0.0f);
			((CreativeScreenHandler)this.handler).scrollItems(0.0f);
			info.cancel();
		}
		
	}
	
	@Inject(method = "setSelectedTab(Lnet/minecraft/item/ItemGroup;)V", at = @At("TAIL"))
	private void setSelectedTab(ItemGroup group, CallbackInfo info) {
		TextFieldWidget searchBox = ((CreativeInventoryAccessor) this).getSearchBox();
		if (searchBox != null) {
            if (group == HeadsMod.SCHNABEL_HEADS) {
                searchBox.setVisible(true);
                searchBox.setFocusUnlocked(false);
                searchBox.setTextFieldFocused(true);
                searchBox.setText("");
                ((CreativeInventoryAccessor)this).invokeSearch();
            }
        }
	}
	
	@Inject(method = "charTyped(CI)Z", at = @At("HEAD"), cancellable = true)
    private void charTyped(char chr, int modifiers, CallbackInfoReturnable<Boolean> info) {
		CreativeInventoryAccessor accessor = ((CreativeInventoryAccessor)this);
        if (accessor.getIgnoreTypedCharacter()) {
            return;
        }
		ItemGroup itemGroup = ItemGroup.GROUPS[accessor.invokeGetSelectedTab()];
		if(itemGroup != HeadsMod.SCHNABEL_HEADS) {
			System.out.println("Not the head tab");
            return;
        }
		System.out.println("Head tab char typed");
        String string = accessor.getSearchBox().getText();
        if (accessor.getSearchBox().charTyped(chr, modifiers)) {
    		System.out.println("SearchBox char typed: " + chr);
            if (!Objects.equals(string, accessor.getSearchBox().getText())) {
        		System.out.println("searching");
                accessor.invokeSearch();
            }
            info.setReturnValue(true);
    		System.out.println("returning true");
            info.cancel();
            return;
        }
    }

	@Inject(method = "keyPressed(III)Z", at = @At("HEAD"), cancellable = true)
    private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> info) {
		CreativeInventoryAccessor accessor = ((CreativeInventoryAccessor)this);
		ItemGroup itemGroup = ItemGroup.GROUPS[accessor.invokeGetSelectedTab()];
		if(itemGroup == HeadsMod.SCHNABEL_HEADS) {
			boolean bl = !accessor.invokeIsCreativeInventorySlot(accessor.getFocusedSlot()) || accessor.getFocusedSlot().hasStack();
	        boolean bl2 = InputUtil.fromKeyCode(keyCode, scanCode).toInt().isPresent();
	        if (bl && bl2 && this.handleHotbarKeyPressed(keyCode, scanCode)) {
	            accessor.setIgnoreTypedCharacter(true);
	            info.setReturnValue(true);
	        }
	        
			String string = accessor.getSearchBox().getText();
	        if (accessor.getSearchBox().keyPressed(keyCode, scanCode, modifiers)) {
	            if (!Objects.equals(string, accessor.getSearchBox().getText())) {
	                accessor.invokeSearch();
	            }
	            info.setReturnValue(true);
	        }
	        if (accessor.getSearchBox().isFocused() && accessor.getSearchBox().isVisible() && keyCode != GLFW.GLFW_KEY_ESCAPE) {
	            info.setReturnValue(true);
	        }
        }
    }

}
