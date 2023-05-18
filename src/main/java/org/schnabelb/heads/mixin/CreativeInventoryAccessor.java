package org.schnabelb.heads.mixin;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.slot.Slot;
import net.minecraft.registry.tag.TagKey;

@Mixin(CreativeInventoryScreen.class)
public interface CreativeInventoryAccessor {
	
	@Accessor
	public Set<TagKey<Item>> getSearchResultTags();
	
	@Accessor
	public TextFieldWidget getSearchBox();
	
	@Accessor("ignoreTypedCharacter")
	public boolean getIgnoreTypedCharacter();
	
	@Accessor("ignoreTypedCharacter")
	public void setIgnoreTypedCharacter(boolean ignoreTypedCharacter);
	
	@Accessor("scrollPosition")
	public void setScrollPosition(float scrollPosition);
	
	@Accessor("selectedTab")
	public ItemGroup getSelectedTab();

	@Invoker("search")
	public void invokeSearch();

	@Invoker("isCreativeInventorySlot")
	public boolean invokeIsCreativeInventorySlot(Slot slot);
	
	
}
