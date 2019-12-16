package space.bbkr.shulkercharm.hooks;

import net.minecraft.item.ItemStack;

public interface CustomDurabilityItem {

	default int getDurability(ItemStack stack) {
		return 0;
	}

	default boolean showDurability(ItemStack stack) {
		return false;
	}

	default int getDurabilityColor(ItemStack stack) {
		return 0;
	}
}
