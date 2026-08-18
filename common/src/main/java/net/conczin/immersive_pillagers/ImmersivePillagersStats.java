package net.conczin.immersive_pillagers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public final class ImmersivePillagersStats {
    private static final Map<String, ResourceLocation> WAVES_DEFEATED = new HashMap<>();

    public static void init(BiConsumer<ResourceLocation, ResourceLocation> registrar) {
        PillagerManager.getHordeNames().forEach(waveType -> registerWave(waveType, registrar));
    }

    private static void registerWave(String waveType, BiConsumer<ResourceLocation, ResourceLocation> registrar) {
        ResourceLocation id = ImmersivePillagers.locate("waves_defeated/" + waveType);
        WAVES_DEFEATED.put(waveType, id);
        registrar.accept(id, id);
    }

    public static void awardWaveDefeated(ServerPlayer player, String waveType) {
        player.awardStat(getWaveDefeatedStat(waveType));
    }

    public static Stat<ResourceLocation> getWaveDefeatedStat(String waveType) {
        ResourceLocation id = WAVES_DEFEATED.get(waveType);
        if (id == null) {
            throw new IllegalArgumentException("Unknown horde wave type: " + waveType);
        }
        return Stats.CUSTOM.get(id);
    }
}
