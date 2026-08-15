package net.conczin.immersive_pillagers.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;

public final class PlayerHordeData extends SavedData {
    private static final String DATA_NAME_PREFIX = ImmersivePillagers.MOD_ID + "_player_horde_";

    public static final Codec<PlayerHordeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("has_killed_pillager", false).forGetter(PlayerHordeData::hasKilledPillager),
            Codec.unboundedMap(Codec.STRING, Codec.LONG).optionalFieldOf("last_raid_times", Map.of()).forGetter(PlayerHordeData::lastRaidTimes)
    ).apply(instance, PlayerHordeData::new));

    private boolean hasKilledPillager;
    private final Map<String, Long> lastRaidTimes;

    public PlayerHordeData() {
        this(false, Map.of());
    }

    private PlayerHordeData(boolean hasKilledPillager, Map<String, Long> lastRaidTimes) {
        this.hasKilledPillager = hasKilledPillager;
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
