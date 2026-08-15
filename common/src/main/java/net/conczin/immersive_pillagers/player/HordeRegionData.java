package net.conczin.immersive_pillagers.player;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class HordeRegionData extends SavedData {
    public static final int SPAWN_REGION_SIZE = 256;

    private static final String DATA_NAME = ImmersivePillagers.MOD_ID + "_horde_regions";

    private static final Codec<LongSet> LONG_SET_CODEC = Codec.LONG.listOf().xmap(LongOpenHashSet::new, ArrayList::new);
    private static final Codec<HordeRegionData> CODEC = LONG_SET_CODEC.optionalFieldOf("enabled_spawn_regions", new LongOpenHashSet())
            .xmap(HordeRegionData::new, HordeRegionData::enabledSpawnRegions)
            .codec();

    private final LongSet enabledSpawnRegions;

    public HordeRegionData() {
        this(new LongOpenHashSet());
    }

    private HordeRegionData(LongSet enabledSpawnRegions) {
        this.enabledSpawnRegions = new LongOpenHashSet(enabledSpawnRegions);
    }

    public static HordeRegionData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(
                HordeRegionData::load,
                HordeRegionData::new,
                DATA_NAME
        );
    }

    public static HordeRegionData load(CompoundTag tag) {
        return CODEC.parse(NbtOps.INSTANCE, tag)
                .resultOrPartial(error -> ImmersivePillagers.LOGGER.warn("Could not load horde region data: {}", error))
                .orElseGet(HordeRegionData::new);
    }

    public static long spawnRegionKey(BlockPos position) {
        return ChunkPos.asLong(Math.floorDiv(position.getX(), SPAWN_REGION_SIZE), Math.floorDiv(position.getZ(), SPAWN_REGION_SIZE));
    }

    public void enableSpawningAt(BlockPos position) {
        if (enabledSpawnRegions.add(spawnRegionKey(position))) {
            setDirty();
        }
    }

    public boolean isSpawningEnabledAt(BlockPos position) {
        return enabledSpawnRegions.contains(spawnRegionKey(position));
    }

    public LongSet enabledSpawnRegions() {
        return new LongOpenHashSet(enabledSpawnRegions);
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag) {
        CODEC.encodeStart(NbtOps.INSTANCE, this)
                .resultOrPartial(error -> ImmersivePillagers.LOGGER.warn("Could not save horde region data: {}", error))
                .filter(CompoundTag.class::isInstance)
                .map(CompoundTag.class::cast)
                .ifPresent(tag::merge);
        return tag;
    }
}
