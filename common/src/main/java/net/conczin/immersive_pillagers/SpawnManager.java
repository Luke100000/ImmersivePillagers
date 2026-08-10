package net.conczin.immersive_pillagers;

import net.conczin.immersive_pillagers.config.Config;
import net.conczin.immersive_pillagers.player.PlayerHordeData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.biome.Biome;

public final class SpawnManager {
    private static final int SPAWN_CHECK_INTERVAL = 20;

    public static void tick(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (isSpawnCheckTick(player)) {
                trySpawnWaves(player);
            }
        }
    }

    private static boolean isSpawnCheckTick(ServerPlayer player) {
        return Math.floorMod(player.getUUID().hashCode() + player.serverLevel().getGameTime(), SPAWN_CHECK_INTERVAL) == 0;
    }

    private static void trySpawnWaves(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        BlockPos position = player.blockPosition();

        if (level.getDifficulty() == Difficulty.PEACEFUL || level.isVillage(position)) {
            return;
        }

        int difficulty = level.getDifficulty().getId() + 2;
        long gameTime = level.getGameTime();

        for (String waveType : PillagerManager.getHordeNames()) {
            if (!level.getBiome(position).is(biomeTag(waveType)) || !shouldSpawn(player, waveType, gameTime)) {
                continue;
            }

            PillagerManager.spawnHorde(waveType, level, position, player, difficulty)
                    .ifPresent(PillagerManager::addActiveHorde);
        }
    }

    private static boolean shouldSpawn(ServerPlayer player, String waveType, long gameTime) {
        long ticksBetweenWaves = Config.getInstance().ticksBetweenWaves;
        if (ticksBetweenWaves <= 0) {
            return false;
        }

        long timeSinceLastRaid = PlayerHordeData.get(player)
                .ticksSinceLastRaid(waveType, gameTime)
                .orElse(gameTime);
        double chance = Math.min(5.0, timeSinceLastRaid / (double) ticksBetweenWaves) * SPAWN_CHECK_INTERVAL / ticksBetweenWaves;
        return player.getRandom().nextDouble() < chance;
    }

    private static TagKey<Biome> biomeTag(String waveType) {
        return TagKey.create(Registries.BIOME, ImmersivePillagers.locate(waveType));
    }
}
