package space.bbkr.shulkercharm.mixin;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import space.bbkr.shulkercharm.ShulkerCharm;
import space.bbkr.shulkercharm.ShulkerCharmItem;

import java.util.List;

@Mixin(BeaconBlockEntity.class)
public abstract class MixinBeaconBlockEntity extends BlockEntity {
	@Shadow int level;

	public MixinBeaconBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	private int getLevel() {
		return level;
	}


	@Inject(method = "tick", at = @At("TAIL"))
	private static void chargeShulkerCharm(World world, BlockPos pos, BlockState state, BeaconBlockEntity be, CallbackInfo info) {
		if (ShulkerCharm.config.rangeModifier == -1) return;
		if (!world.isClient && ((MixinBeaconBlockEntity) (Object) be).getLevel() > 0) {
			double range = ((MixinBeaconBlockEntity) (Object) be).getLevel() * ShulkerCharm.config.rangeModifier + 10;
			Box box = new Box(pos).expand(range).stretch(0, world.getHeight(), 0);
			List<PlayerEntity> players = world.getNonSpectatingEntities(PlayerEntity.class, box);
			//System.out.println("Player List: " + players);
			for (PlayerEntity player : players) {
				TrinketComponent comp = TrinketsApi.TRINKET_COMPONENT.get(player);
				List<Pair<SlotReference, ItemStack>> stacks = comp.getEquipped(ShulkerCharm.SHULKER_CHARM);
				for (Pair<SlotReference, ItemStack> pair : stacks) {
					tryChargeStack(pair.getRight());
				}
				tryChargeStack(player.getMainHandStack());
				tryChargeStack(player.getOffHandStack());
			}
		}
	}

	private static void tryChargeStack(ItemStack stack) {
		if (stack.getItem() instanceof ShulkerCharmItem) {
			ShulkerCharmItem charm = (ShulkerCharmItem)stack.getItem();
			charm.charge(stack);
		}
	}
}
