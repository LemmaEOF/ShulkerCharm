package space.bbkr.shulkercharm;

import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.TrinketsApi;
import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;

public class ShulkerCharm implements ModInitializer {
	public static final String MODID = "shulkercharm";

	public static final Logger logger = LogManager.getLogger();

	public static final Identifier CHARM_FLIGHT_ID = new Identifier(MODID, "shulker_charm_flight");

	public static final AbilitySource CHARM_FLIGHT = Pal.getAbilitySource(CHARM_FLIGHT_ID);

	public static final Item SHULKER_CHARM = Registry.register(Registry.ITEM, new Identifier(MODID, "shulker_charm"), new ShulkerCharmItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE).group(ItemGroup.TRANSPORTATION)));

	public static ShulkerCharmConfig config;

	@Override
	public void onInitialize() {
		AutoConfig.register(ShulkerCharmConfig.class, JanksonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(ShulkerCharmConfig.class).getConfig();
		if (config.maxEnergy != -1) {
			config.maxPower = config.maxEnergy;
			config.maxEnergy = -1;
		}
		//trinkets now adds slots via data tags
		//TrinketSlots.addSlot(SlotGroups.CHEST, Slots.NECKLACE, new Identifier("trinkets", "textures/item/empty_trinket_slot_necklace.png"));
	}
}
