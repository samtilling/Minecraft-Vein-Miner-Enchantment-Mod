package yeasty.veinminer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class VeinMiningHandler {
	/** Points at data/veinminer/enchantment/vein_miner.json. */
	public static final ResourceKey<Enchantment> VEIN_MINER =
			ResourceKey.create(Registries.ENCHANTMENT, VeinMiner.id("vein_miner"));

	/** The most extra blocks one vein-mine can break, so huge veins can't lag the server. */
	private static final int MAX_BLOCKS = 64;

	/**
	 * True while we are breaking the extra blocks. Each of those breaks fires the
	 * block-break event again, and this flag stops that from starting another vein-mine.
	 */
	private static boolean veinMining = false;

	private VeinMiningHandler() {}

	public static void onBlockBroken(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		if (veinMining || level.isClientSide() || !(player instanceof ServerPlayer serverPlayer)) {
			return;
		}
		// Sneak to mine just one block, like normal.
		if (serverPlayer.isShiftKeyDown()) {
			return;
		}
		if (!isOre(state)) {
			return;
		}

		ItemStack tool = serverPlayer.getMainHandItem();
		if (!hasVeinMiner(level, tool) || !serverPlayer.hasCorrectToolForDrops(state)) {
			return;
		}

		List<BlockPos> vein = findVein(level, pos, state.getBlock());

		veinMining = true;
		try {
			for (BlockPos target : vein) {
				ItemStack held = serverPlayer.getMainHandItem();
				// Stop if the pickaxe was swapped out, or stop one hit early so it never breaks.
				if (held != tool || (held.isDamageableItem() && held.getMaxDamage() - held.getDamageValue() <= 1)) {
					break;
				}
				// Goes through the normal survival mining path, so drops, Fortune/Silk Touch,
				// durability, hunger, XP and statistics all behave like a regular block break.
				serverPlayer.gameMode.destroyBlock(target);
			}
		} finally {
			veinMining = false;
		}
	}

	private static boolean isOre(BlockState state) {
		// minecraft:ores covers vanilla; c:ores also covers ores added by other mods.
		return state.is(BlockTags.ORES) || state.is(ConventionalBlockTags.ORES);
	}

	private static boolean hasVeinMiner(Level level, ItemStack tool) {
		if (tool.isEmpty()) {
			return false;
		}
		Optional<Holder.Reference<Enchantment>> enchantment =
				level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(VEIN_MINER);
		return enchantment.isPresent() && EnchantmentHelper.getItemEnchantmentLevel(enchantment.get(), tool) > 0;
	}

	/**
	 * Flood-fills outwards from the broken block, collecting touching blocks of the same
	 * type. Diagonals count as touching, because ore veins often connect only diagonally.
	 */
	private static List<BlockPos> findVein(Level level, BlockPos start, Block oreBlock) {
		List<BlockPos> found = new ArrayList<>();
		Set<BlockPos> visited = new HashSet<>();
		ArrayDeque<BlockPos> queue = new ArrayDeque<>();
		visited.add(start);
		queue.add(start);

		while (!queue.isEmpty() && found.size() < MAX_BLOCKS) {
			BlockPos current = queue.poll();
			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					for (int dz = -1; dz <= 1; dz++) {
						BlockPos next = current.offset(dx, dy, dz);
						if (found.size() >= MAX_BLOCKS || !visited.add(next)) {
							continue;
						}
						if (level.getBlockState(next).is(oreBlock)) {
							found.add(next);
							queue.add(next);
						}
					}
				}
			}
		}
		return found;
	}
}
