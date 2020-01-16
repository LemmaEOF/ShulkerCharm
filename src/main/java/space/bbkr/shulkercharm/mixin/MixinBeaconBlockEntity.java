package space.bbkr.shulkercharm.mixin;

import dev.emi.trinkets.api.*;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import space.bbkr.shulkercharm.ShulkerCharm;
import space.bbkr.shulkercharm.ShulkerCharmItem;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(BeaconBlockEntity.class)
public abstract class MixinBeaconBlockEntity extends BlockEntity {
	@Shadow private int level;

	public MixinBeaconBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void chargeShulkerCharm(CallbackInfo info) {
		if (ShulkerCharm.config.rangeModifier == -1) return;
		if (!world.isClient && this.level > 0) {
			double range = this.level * ShulkerCharm.config.rangeModifier + 10;
			Box box = new Box(this.pos).expand(range).stretch(0, world.getHeight(), 0);
			List<PlayerEntity> players = world.getNonSpectatingEntities(PlayerEntity.class, box);
			for (PlayerEntity player : players) {
				TrinketComponent comp = TrinketsApi.TRINKETS.get(player);
				ItemStack stack = comp.getStack(SlotGroups.CHEST, Slots.NECKLACE);
				tryChargeStack(stack);
				tryChargeStack(player.getMainHandStack());
				tryChargeStack(player.getOffHandStack());
			}
		}
	}

	void tryChargeStack(ItemStack stack) {
		if (stack.getItem() instanceof ShulkerCharmItem) {
			ShulkerCharmItem charm = (ShulkerCharmItem)stack.getItem();
			charm.charge(stack);
		}
	}
}
