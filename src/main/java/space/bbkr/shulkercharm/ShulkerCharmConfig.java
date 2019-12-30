package space.bbkr.shulkercharm;

import blue.endless.jankson.Comment;

public class ShulkerCharmConfig {
	@Comment("The modifier for the maximum distance from a beacon to keep charging.\n"
			+ "Formula is <beacon level> * <modifier> + 10.\n"
			+ "Vanilla range modifier is 10; set to -1 to disable charge requirement.")
	public int rangeModifier = 10;

	@Comment("Deprecated; use maxPower instead.")
	public int maxEnergy = -1;

	@Comment("The maximum amount of power a shulker charm can store.\n"
			+ "Charm gains 20 energy per second when in beacon range,\n"
			+ "and loses 20 energy per second when out of it.\n"
			+ "Default value 1200 (60 seconds).")
	public int maxPower = 1200;
}