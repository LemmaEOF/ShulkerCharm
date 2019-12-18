package space.bbkr.shulkercharm.mixin;

import dev.emi.trinkets.api.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import space.bbkr.shulkercharm.ShulkerCharm;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {
	@Shadow @Final public PlayerAbilities abilities;

	@Shadow public abstract void sendAbilitiesUpdate();

	protected MixinPlayerEntity(EntityType<? extends LivingEntity> type, World world) {
		super(type, world);
	}

	@Inject(method = "tickMovement", at = @At("HEAD"))
	private void injectFlight(CallbackInfo info) {
		//TODO: make an event somewhere so this doesn't collide with anyone else's creative-flight mods
		if (((PlayerEntity)(Object)this).isCreative()) abilities.allowFlying = true;
		if (!this.world.isClient && !((PlayerEntity)(Object)this).isCreative()) {
			TrinketComponent comp = TrinketsApi.TRINKETS.get(this);
			ItemStack stack = comp.getStack(SlotGroups.HEAD, Slots.NECKLACE);
			if (stack.getItem() == ShulkerCharm.SHULKER_CHARM) {
				if (ShulkerCharm.config.rangeModifier != -1) {
					CompoundTag tag = stack.getOrCreateTag();
					if (tag.containsKey("Energy")) {
						int energy = tag.getInt("Energy");
						abilities.allowFlying = energy > 0;
						if (!abilities.allowFlying) abilities.flying = false;
						if (energy == 1) {
							addPotionEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 200));
						}
					} else {
						tag.putInt("Energy", 0);
						abilities.allowFlying = false;
					}
				} else {
					abilities.allowFlying = true;
				}
			} else {
				abilities.allowFlying = false;
				if (abilities.flying) {
					abilities.flying = false;
					addPotionEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 100));
				}
			}
			this.sendAbilitiesUpdate();
		}
	}
}
