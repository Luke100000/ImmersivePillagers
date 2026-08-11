package net.conczin.immersive_pillagers.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class PlayerHordeData extends SavedData {
    public static final int SPAWN_REGION_SIZE = 256;

    private static final Codec<LongSet> LONG_SET_CODEC = Codec.LONG.listOf().xmap(LongOpenHashSet::new, ArrayList::new);
    private static final String DATA_NAME_PREFIX = ImmersivePillagers.MOD_ID + "_player_horde_";

    public static final Codec<PlayerHordeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("has_killed_pillager", false).forGetter(PlayerHordeData::hasKilledPillager),
            LONG_SET_CODEC.optionalFieldOf("enabled_spawn_regions", new LongOpenHashSet()).forGetter(PlayerHordeData::enabledSpawnRegions),
            Codec.unboundedMap(Codec.STRING, Codec.LONG).optionalFieldOf("last_raid_times", Map.of()).forGetter(PlayerHordeData::lastRaidTimes)
    ).apply(instance, PlayerHordeData::new));

    private boolean hasKilledPillager;
    private final LongSet enabledSpawnRegions;
    private final Map<String, Long> lastRaidTimes;

    public PlayerHordeData() {
        this(false, new LongOpenHashSet(), Map.of());
    }

    private PlayerHordeData(boolean hasKilledPillager, LongSet enabledSpawnRegions, Map<String, Long> lastRaidTimes) {
        this.hasKilledPillager = hasKilledPillager;
        this.enabledSpawnRegions = new LongOpenHashSet(enabledSpawnRegions);
        this.lastRaidTimes = new HashMap<>(lastRaidTimes);
    }

    public static PlayerHordeData get(ServerPlayer player) {
        return Objects.requireNonNull(player.getServer()).overworld().getDataStorage().computeIfAbsent(
                PlayerHordeData::load,
                PlayerHordeData::new,
                DATA_NAME_PREFIX + player.getUUID()
        );
    }

    public static PlayerHordeData load(CompoundTag tag) {
        return CODEC.parse(NbtOps.INSTANCE, tag)
                .resultOrPartial(error -> ImmersivePillagers.LOGGER.warn("Could not load player horde data: {}", error))
                .orElseGet(PlayerHordeData::new);
    }

    public boolean hasKilledPillager() {
        return hasKilledPillager;
    }

    public boolean markPillagerKilled() {
        if (!hasKilledPillager) {
            hasKilledPillager = true;
            setDirty();
            return true;
        }
        return false;
    }

    public void pardon() {
        if (hasKilledPillager) {
            hasKilledPillager = false;
            setDirty();
        }
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

    public void markRaidStarted(String waveType, long gameTime) {
        lastRaidTimes.put(waveType, gameTime);
        setDirty();
    }

    public OptionalLong lastRaidTime(String waveType) {
        Long time = lastRaidTimes.get(waveType);
        return time == null ? OptionalLong.empty() : OptionalLong.of(time);
    }

    public OptionalLong ticksSinceLastRaid(String waveType, long gameTime) {
        OptionalLong lastRaidTime = lastRaidTime(waveType);
        return lastRaidTime.isPresent() ? OptionalLong.of(Math.max(0, gameTime - lastRaidTime.getAsLong())) : OptionalLong.empty();
    }

    public Map<String, Long> lastRaidTimes() {
        return Map.copyOf(lastRaidTimes);
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag) {
        CODEC.encodeStart(NbtOps.INSTANCE, this)
                .resultOrPartial(error -> ImmersivePillagers.LOGGER.warn("Could not save player horde data: {}", error))
                .filter(CompoundTag.class::isInstance)
                .map(CompoundTag.class::cast)
                .ifPresent(tag::merge);
        return tag;
    }
}
