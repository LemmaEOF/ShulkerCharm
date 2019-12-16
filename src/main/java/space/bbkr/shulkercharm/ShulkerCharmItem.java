package space.bbkr.shulkercharm;

import dev.emi.trinkets.api.ITrinket;
import dev.emi.trinkets.api.SlotGroups;
import dev.emi.trinkets.api.Slots;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import space.bbkr.shulkercharm.hooks.CustomDurabilityItem;

import javax.annotation.Nullable;
import java.util.List;

public class ShulkerCharmItem extends Item implements ITrinket, CustomDurabilityItem {
	public ShulkerCharmItem(Settings settings) {
		super(settings);
	}

	@Override
	public void tick(PlayerEntity player, ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		if (!tag.contains("Energy", NbtType.INT)) tag.putInt("Energy", 0);
		int energy = tag.getInt("Energy");
		if (energy > 0) {
			tag.putInt("Energy", energy - 1);
		}
	}

	@Override
	public boolean canWearInSlot(String group, String slot) {
		return group.equals(SlotGroups.HEAD) && slot.equals(Slots.NECKLACE);
	}

	@Override
	public int getDurability(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		if (tag.contains("Energy", NbtType.INT)) {
			return tag.getInt("Energy");
		}
		return 0;
	}

	@Override
	public boolean showDurability(ItemStack stack) {
		return true;
	}

	@Override
	public int getDurabilityColor(ItemStack stack) {
		return 0;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		if (context.isAdvanced()) {
			CompoundTag tag = stack.getOrCreateTag();
			if (!tag.contains("Energy", NbtType.INT)) tag.putInt("Energy", 0);
			int energy = tag.getInt("Energy");
			tooltip.add(new TranslatableText("tooltip.shulkercharm.energy", energy, ShulkerCharm.config.maxEnergy));
		}
	}
}
