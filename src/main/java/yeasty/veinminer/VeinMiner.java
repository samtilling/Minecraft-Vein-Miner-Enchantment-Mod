package yeasty.veinminer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VeinMiner implements ModInitializer {
	public static final String MOD_ID = "veinminer";

	// This logger is used to write text to the console and the log file.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// The enchantment itself is defined in data/veinminer/enchantment/vein_miner.json.
		// Here we only hook in the behaviour: after a block is broken, mine the rest of the vein.
		PlayerBlockBreakEvents.AFTER.register(VeinMiningHandler::onBlockBroken);

		LOGGER.info("Vein Miner loaded");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
