package net.conczin.immersive_pillagers.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalLong;

public final class PlayerHordeData extends SavedData {
    public static final Codec<PlayerHordeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("has_killed_pillager", false).forGetter(PlayerHordeData::hasKilledPillager),
            Codec.unboundedMap(Codec.STRING, Codec.LONG).optionalFieldOf("last_raid_times", Map.of()).forGetter(PlayerHordeData::lastRaidTimes),
            Codec.LONG.optionalFieldOf("scheduled_raid_time", -1L).forGetter(PlayerHordeData::scheduledRaidTime)
    ).apply(instance, PlayerHordeData::new));

    private boolean hasKilledPillager;
    private final Map<String, Long> lastRaidTimes;
    private long scheduledRaidTime;

    public PlayerHordeData() {
        this(false, Map.of(), -1L);
    }

    private PlayerHordeData(boolean hasKilledPillager, Map<String, Long> lastRaidTimes, long scheduledRaidTime) {
        this.hasKilledPillager = hasKilledPillager;
        this.lastRaidTimes = new HashMap<>(lastRaidTimes);
        this.scheduledRaidTime = scheduledRaidTime;
    }

    public static PlayerHordeData get(ServerPlayer player) {
        Identifier id = ImmersivePillagers.locate("player_horde_" + player.getUUID());
        SavedDataType<PlayerHordeData> type = new SavedDataType<>(id, PlayerHordeData::new, CODEC, DataFixTypes.SAVED_DATA_MAP_DATA);
        return player.level().getServer().overworld().getDataStorage().computeIfAbsent(type);
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
            scheduledRaidTime = -1L;
            setDirty();
        }
    }

    public void scheduleRaid(long gameTime) {
        scheduledRaidTime = gameTime;
        setDirty();
    }

    public boolean hasScheduledRaidDue(long gameTime) {
        return scheduledRaidTime >= 0 && gameTime >= scheduledRaidTime;
    }

    public void clearScheduledRaid() {
        if (scheduledRaidTime >= 0) {
            scheduledRaidTime = -1L;
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

    private long scheduledRaidTime() {
        return scheduledRaidTime;
    }

}
