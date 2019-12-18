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
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import space.bbkr.shulkercharm.hooks.CustomDurabilityItem;

import javax.annotation.Nullable;
import java.util.List;

public class ShulkerCharmItem extends Item implements ITrinket, CustomDurabilityItem {
	public ShulkerCharmItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		return ITrinket.equipTrinket(user, hand);
	}

	@Override
	public void tick(PlayerEntity player, ItemStack stack) {
		if (player.abilities.flying) {
			CompoundTag tag = stack.getOrCreateTag();
			if (!tag.containsKey("Energy", NbtType.INT)) tag.putInt("Energy", 0);
			int energy = tag.getInt("Energy");
			if (energy > 0) {
				tag.putInt("Energy", energy - 1);
			}
		}
	}

	@Override
	public boolean canWearInSlot(String group, String slot) {
		return group.equals(SlotGroups.HEAD) && slot.equals(Slots.NECKLACE);
	}

	@Override
	public boolean shouldShowDurability(ItemStack stack) {
		if (ShulkerCharm.config.rangeModifier == -1) return false;
		CompoundTag tag = stack.getOrCreateTag();
		if (tag.containsKey("Energy", NbtType.INT)) return tag.getInt("Energy") != getMaxDurability(stack);
		else return true;
	}

	@Override
	public int getMaxDurability(ItemStack stack) {
		return ShulkerCharm.config.maxEnergy;
	}

	@Override
	public int getDurability(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		if (tag.containsKey("Energy", NbtType.INT)) {
			return tag.getInt("Energy");
		}
		return 0;
	}

	@Override
	public int getDurabilityColor(ItemStack stack) {
		return 0xC7C38D;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		if (context.isAdvanced()) {
			CompoundTag tag = stack.getOrCreateTag();
			if (!tag.containsKey("Energy", NbtType.INT)) tag.putInt("Energy", 0);
			int energy = tag.getInt("Energy");
			tooltip.add(new TranslatableText("tooltip.shulkercharm.energy", energy, ShulkerCharm.config.maxEnergy));
		}
	}
}
