package net.conczin.immersive_pillagers;

import net.conczin.immersive_pillagers.config.Config;
import net.conczin.immersive_pillagers.player.HordeRegionData;
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
        long gameTime = level.getGameTime();

        if (trySpawnScheduledWave(player, level, position, gameTime)) {
            return;
        }

        if (!HordeRegionData.get(level).isSpawningEnabledAt(position)) {
            return;
        }

        if (!PlayerHordeData.get(player).hasKilledPillager()) {
            return;
        }

        if (level.getDifficulty() == Difficulty.PEACEFUL || level.isVillage(position)) {
            return;
        }

        int difficulty = waveDifficulty(level, position);
        for (String waveType : PillagerManager.getHordeNames()) {
            if (!level.getBiome(position).is(biomeTag(waveType)) || !shouldSpawn(player, waveType, gameTime)) {
                continue;
            }

            PillagerManager.spawnHorde(waveType, level, position, player, difficulty)
                    .ifPresent(PillagerManager::addActiveHorde);
        }
    }

    private static boolean trySpawnScheduledWave(ServerPlayer player, ServerLevel level, BlockPos position, long gameTime) {
        PlayerHordeData data = PlayerHordeData.get(player);
        if (!data.hasScheduledRaidDue(gameTime) || level.getDifficulty() == Difficulty.PEACEFUL || level.isVillage(position)) {
            return false;
        }

        if (PillagerManager.spawnRandomHorde(level, position, player, waveDifficulty(level, position)).map(horde -> {
            PillagerManager.addActiveHorde(horde);
            return true;
        }).orElse(false)) {
            data.clearScheduledRaid();
            return true;
        }
        return false;
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

    public static int waveDifficulty(ServerLevel level, BlockPos position) {
        float localDifficulty = level.getCurrentDifficultyAt(position).getEffectiveDifficulty();
        return Math.max(1, Math.round(localDifficulty * (float) Config.getInstance().baseDifficultyFactor));
    }
}
