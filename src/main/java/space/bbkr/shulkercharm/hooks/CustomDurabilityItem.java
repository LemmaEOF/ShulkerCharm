package space.bbkr.shulkercharm.hooks;

import net.minecraft.item.ItemStack;

public interface CustomDurabilityItem {

	int getMaxDurability(ItemStack stack);

	int getDurability(ItemStack stack);

	int getDurabilityColor(ItemStack stack);
}
