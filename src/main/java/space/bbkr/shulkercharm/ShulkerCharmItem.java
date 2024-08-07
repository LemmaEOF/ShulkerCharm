package space.bbkr.shulkercharm;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import io.github.ladysnake.pal.VanillaAbilities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShulkerCharmItem extends TrinketItem {
	public ShulkerCharmItem(Settings settings) {
		super(settings);
	}

	@Override
	public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
		super.tick(stack, slot, entity);
		if (entity instanceof PlayerEntity player) {
			int power = getPower(stack);
			if (player.getWorld().isClient) return;
			if (ShulkerCharm.CHARM_FLIGHT.grants(player, VanillaAbilities.ALLOW_FLYING)) {
				if (power <= 0) {
					ShulkerCharm.CHARM_FLIGHT.revokeFrom(player, VanillaAbilities.ALLOW_FLYING);
					if (!VanillaAbilities.ALLOW_FLYING.isEnabledFor(player)) {
						player.getAbilities().flying = false;
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
	}

	@Override
	public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
		super.onUnequip(stack, slot, entity);
		if (entity instanceof PlayerEntity player) {
			if (!player.getWorld().isClient
					&& ShulkerCharm.CHARM_FLIGHT.grants(player, VanillaAbilities.ALLOW_FLYING)
					&& !slot.inventory().getStack(0).isOf(ShulkerCharm.SHULKER_CHARM)) {
				ShulkerCharm.CHARM_FLIGHT.revokeFrom(player, VanillaAbilities.ALLOW_FLYING);
				if (!VanillaAbilities.ALLOW_FLYING.isEnabledFor(player)) {
					player.getAbilities().flying = false;
					player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 100));
				}
			}
		}
	}

	@Override
	public boolean isItemBarVisible(ItemStack stack) {
		if (ShulkerCharm.config.rangeModifier == -1) {
			return false;
		}
		//"-1" so that the bar doesnt show while flying at 'max charge' in range of a beacon
		return getPower(stack) < getMaxDurability() - 1;
	}

	@Override
	public int getItemBarStep(ItemStack stack) {
		return Math.round((float)getPower(stack) * 13.0F / (float)getMaxDurability());
	}

	@Override
	public int getItemBarColor(ItemStack stack) {
		return 0xC7C38D;
	}

	public int getMaxDurability() {
		return ShulkerCharm.config.maxPower;
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
		if (type.isAdvanced()) {
			tooltip.add(Text.translatable("tooltip.shulkercharm.power", getPower(stack), getMaxDurability()));
		}
	}

	/**
	 * Get the power of a shulker charm.
	 * @param stack The stack to get for.
	 * @return The amount of power it currently has.
	 */
	public int getPower(ItemStack stack) {
		if (ShulkerCharm.config.rangeModifier == -1) return getMaxDurability();
		return stack.getComponents().getOrDefault(ShulkerCharm.SHULKER_CHARM_CHARGE, 0);
	}

	/**
	 * Set the power of a shulker charm.
	 * @param stack The stack to set for.
	 * @param power The amount of power it should have.
	 */
	public void setPower(ItemStack stack, int power) {
		if(power >= getMaxDurability()) {
			stack.set(ShulkerCharm.SHULKER_CHARM_CHARGE, getMaxDurability());
		} else {
			stack.set(ShulkerCharm.SHULKER_CHARM_CHARGE, power);
		}
	}

	/**
	 * Charge a shulker charm from a beacon. Adds 2 energy, typically.
	 * @param stack The stack to charge.
	 */
	public void charge(ItemStack stack) {
		int power = getPower(stack);
		setPower(stack, power + 2);
		//System.out.println("Power: " + power);
	}
}
