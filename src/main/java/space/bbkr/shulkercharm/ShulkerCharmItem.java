package space.bbkr.shulkercharm;

import dev.emi.trinkets.api.SlotGroups;
import dev.emi.trinkets.api.Slots;
import dev.emi.trinkets.api.TrinketItem;
import io.github.ladysnake.pal.VanillaAbilities;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import space.bbkr.shulkercharm.hooks.CustomDurabilityItem;

import javax.annotation.Nullable;
import java.util.List;

public class ShulkerCharmItem extends TrinketItem implements CustomDurabilityItem {
	public ShulkerCharmItem(Settings settings) {
		super(settings);
	}

	@Override
	public void tick(PlayerEntity player, ItemStack stack) {
		int power = getPower(stack);
		if (player.world.isClient) return;
		if (ShulkerCharm.CHARM_FLIGHT.grants(player, VanillaAbilities.ALLOW_FLYING)) {
			if (power <= 0) {
				ShulkerCharm.CHARM_FLIGHT.revokeFrom(player, VanillaAbilities.ALLOW_FLYING);
				if (!VanillaAbilities.ALLOW_FLYING.isEnabledFor(player)) {
					player.abilities.flying = false;
					player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 200));
				}
			}
		} else {
			if (power > 0) {
				ShulkerCharm.CHARM_FLIGHT.grantTo(player, VanillaAbilities.ALLOW_FLYING);
			}
		}
		if (VanillaAbilities.FLYING.isEnabledFor(player) && ShulkerCharm.config.rangeModifier != -1) {
			setPower(stack, power - 1);
		}
	}

	@Override
	public void onUnequip(PlayerEntity player, ItemStack stack) {
		if (!player.world.isClient && ShulkerCharm.CHARM_FLIGHT.grants(player, VanillaAbilities.ALLOW_FLYING)) {
			ShulkerCharm.CHARM_FLIGHT.revokeFrom(player, VanillaAbilities.ALLOW_FLYING);
			if (!VanillaAbilities.ALLOW_FLYING.isEnabledFor(player)) {
				player.abilities.flying = false;
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 100));
			}
		}
	}

	@Override
	public boolean canWearInSlot(String group, String slot) {
		return group.equals(SlotGroups.CHEST) && slot.equals(Slots.NECKLACE);
	}

	@Override
	public boolean shouldShowDurability(ItemStack stack) {
		if (ShulkerCharm.config.rangeModifier == -1) return false;
		CompoundTag tag = stack.getOrCreateTag();
		if (tag.contains("Power", NbtType.INT)) return tag.getInt("Power") != getMaxDurability(stack);
		else return true;
	}

	@Override
	public int getMaxDurability(ItemStack stack) {
		return ShulkerCharm.config.maxPower;
	}

	@Override
	public int getDurability(ItemStack stack) {
		return getPower(stack);
	}

	@Override
	public int getDurabilityColor(ItemStack stack) {
		return 0xC7C38D;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		if (context.isAdvanced()) {
			int power = getPower(stack);
			tooltip.add(new TranslatableText("tooltip.shulkercharm.power", power, ShulkerCharm.config.maxPower));
		}
	}

	/**
	 * Get the power of a shulker charm.
	 * @param stack The stack to get for.
	 * @return The amount of power it currently has.
	 */
	public int getPower(ItemStack stack) {
		if (ShulkerCharm.config.rangeModifier == -1) return ShulkerCharm.config.maxPower;
		CompoundTag tag = stack.getOrCreateTag();
		if (!tag.contains("Power", NbtType.INT)) tag.putInt("Power", 0);
		return tag.getInt("Power");
	}

	/**
	 * Set the power of a shulker charm.
	 * @param stack The stack to set for.
	 * @param power The amount of power it should have.
	 */
	public void setPower(ItemStack stack, int power) {
		CompoundTag tag = stack.getOrCreateTag();
		tag.putInt("Power", Math.max(0, Math.min(power, getMaxDurability(stack))));
	}

	/**
	 * Charge a shulker charm from a beacon. Adds 2 energy, typically.
	 * @param stack The stack to charge.
	 */
	public void charge(ItemStack stack) {
		int power = getPower(stack);
		setPower(stack, power + 2);
	}
}
