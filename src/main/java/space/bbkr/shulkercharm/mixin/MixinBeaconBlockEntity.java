package space.bbkr.shulkercharm.mixin;

import dev.emi.trinkets.api.*;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import space.bbkr.shulkercharm.ShulkerCharm;
import space.bbkr.shulkercharm.ShulkerCharmItem;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Mixin(BeaconBlockEntity.class)
public abstract class MixinBeaconBlockEntity extends BlockEntity {

	@Shadow int level;

	public MixinBeaconBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}


	@Inject(method = "tick", at = @At("HEAD"))
	private static void tick(World world, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity, CallbackInfo ci) {

		if (ShulkerCharm.config.rangeModifier == -1) {return;}

		int levelStatic = 0;
		levelStatic = Objects.requireNonNull(blockEntity.toUpdatePacket()).getNbt().getInt("Levels");

		//System.out.println("World Client?: " + world.isClient);
		//System.out.println("Level Int(??): " + levelStatic);
		if (!world.isClient && levelStatic > 0) {
			double range = levelStatic * ShulkerCharm.config.rangeModifier + 10;
			//System.out.println("Range: " + range);
			Box box = new Box(pos).expand(range).stretch(0, world.getHeight(), 0);
			List<PlayerEntity> players = world.getNonSpectatingEntities(PlayerEntity.class, box);
			//System.out.println("Player List: " + players);
			for (PlayerEntity player : players) {
				Optional<TrinketComponent> trinketComponent = TrinketsApi.getTrinketComponent(player);
				ItemStack stack = getItemStack(trinketComponent);
				//System.out.println("Charm Stack: " + stack);
				tryChargeStack(stack);
				tryChargeStack(player.getMainHandStack());
				tryChargeStack(player.getOffHandStack());
			}
		}
	}

	private int getLevel() {
		return level;
	}

	private static ItemStack getItemStack(Optional<TrinketComponent> trinketComponent) {
		for(int i = 0; i <trinketComponent.get().getAllEquipped().size(); i++) {
			if(trinketComponent.get().getAllEquipped().get(i).getRight().isOf(ShulkerCharm.SHULKER_CHARM)) {
				return trinketComponent.get().getAllEquipped().get(i).getRight();
			}
		}
		return ItemStack.EMPTY;
	}

	private static void tryChargeStack(ItemStack stack) {
		if (stack.getItem() instanceof ShulkerCharmItem) {
			ShulkerCharmItem charm = (ShulkerCharmItem)stack.getItem();
			charm.charge(stack);
		}
	}
}
