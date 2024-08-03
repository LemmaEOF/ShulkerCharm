package space.bbkr.shulkercharm;

import com.mojang.serialization.Codec;
import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ShulkerCharm implements ModInitializer {
	public static final String MODID = "shulkercharm";

	public static final Identifier CHARM_FLIGHT_ID = Identifier.of(MODID, "shulker_charm_flight");

	public static final AbilitySource CHARM_FLIGHT = Pal.getAbilitySource(CHARM_FLIGHT_ID);

	public static final Item SHULKER_CHARM = Registry.register(
			Registries.ITEM,
			Identifier.of(MODID, "shulker_charm"),
			new ShulkerCharmItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE).component(ShulkerCharm.SHULKER_CHARM_CHARGE, 0)));

	public static ShulkerCharmConfig config;

	//as of 1.20.5, NBT is no longer a thing? instead we get complicated component things
	public static final ComponentType<Integer> SHULKER_CHARM_CHARGE = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(MODID, "shulker_charm_charge"),
			ComponentType.<Integer>builder().codec(Codec.INT).build()
	);

	@Override
	public void onInitialize() {
		AutoConfig.register(ShulkerCharmConfig.class, JanksonConfigSerializer::new);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
			content.add(SHULKER_CHARM);
		});

		config = AutoConfig.getConfigHolder(ShulkerCharmConfig.class).getConfig();

	}

}
