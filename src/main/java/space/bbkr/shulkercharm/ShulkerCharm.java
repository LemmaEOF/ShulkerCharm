package space.bbkr.shulkercharm;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import dev.emi.trinkets.TrinketsMixinPlugin;
import dev.emi.trinkets.api.SlotGroups;
import dev.emi.trinkets.api.Slots;
import dev.emi.trinkets.api.TrinketSlots;
import dev.emi.trinkets.api.TrinketsApi;
import io.github.cottonmc.jankson.JanksonFactory;
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

	public static final Item SHULKER_CHARM = Registry.register(Registry.ITEM, new Identifier(MODID, "shulker_charm"), new ShulkerCharmItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE).group(ItemGroup.TRANSPORTATION)));

	public static ShulkerCharmConfig config;

	@Override
	public void onInitialize() {
		config = loadConfig();
		TrinketSlots.addSlot(SlotGroups.HEAD, Slots.NECKLACE, new Identifier("trinkets", "textures/item/empty_trinket_slot_necklace.png"));
	}

	public ShulkerCharmConfig loadConfig() {
		try {
			Jankson jankson = JanksonFactory.createJankson();
			File file = FabricLoader.getInstance().getConfigDirectory().toPath().resolve("shulkercharm.json5").toFile();
			if (!file.exists()) saveConfig(new ShulkerCharmConfig());
			JsonObject json = jankson.load(file);
			ShulkerCharmConfig result =  jankson.fromJson(json, ShulkerCharmConfig.class);
			JsonElement jsonElementNew = jankson.toJson(new ShulkerCharmConfig());
			if(jsonElementNew instanceof JsonObject){
				JsonObject jsonNew = (JsonObject) jsonElementNew;
				if(json.getDelta(jsonNew).size()>= 0){
					saveConfig(result);
				}
			}
		} catch (Exception e) {
			logger.error("[ShulkerCharm] Error loading config: {}", e.getMessage());
		}
		return new ShulkerCharmConfig();
	}

	public void saveConfig(ShulkerCharmConfig config) {
		try {
			File file = FabricLoader.getInstance().getConfigDirectory().toPath().resolve("shulkercharm.json5").toFile();
			JsonElement json = JanksonFactory.createJankson().toJson(config);
			String result = json.toJson(true, true);
			if (!file.exists()) file.createNewFile();
			FileOutputStream out = new FileOutputStream(file,false);
			out.write(result.getBytes());
			out.flush();
			out.close();
		} catch (Exception e) {
			logger.error("[ShulkerCharm] Error saving config: {}", e.getMessage());
		}
	}
}
