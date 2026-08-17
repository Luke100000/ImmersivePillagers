package net.conczin.immersive_pillagers.hordes;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.ImmersivePillagersStats;
import net.conczin.immersive_pillagers.PillagerManager;
import net.conczin.immersive_pillagers.player.HordeRegionData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ActiveHorde {
    private final UUID id = UUID.randomUUID();
    private final String type;
    private final ServerLevel level;
    @Nullable
    private final UUID targetId;
    @Nullable
    private BlockPos regionToLiberate;
    private final Set<UUID> members = new HashSet<>();
    private boolean hasPlayerKill;
    private final int initialMembers;
    private final ServerBossEvent bossEvent;

    public ActiveHorde(String type, ServerLevel level, Iterable<? extends Entity> members, @Nullable ServerPlayer target) {
        this.type = type;
        this.level = level;
        this.targetId = target == null ? null : target.getUUID();

        for (Entity member : members) {
            this.members.add(member.getUUID());
        }
        this.initialMembers = Math.max(1, this.members.size());

        MutableComponent name = Component.translatable("horde." + ImmersivePillagers.MOD_ID + "." + type);
        this.bossEvent = new ServerBossEvent(UUID.randomUUID(), name, BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);
        this.bossEvent.setDarkenScreen(false);
        this.bossEvent.setCreateWorldFog(false);
        this.bossEvent.setPlayBossMusic(false);
    }

    public UUID id() {
        return id;
    }

    public String type() {
        return type;
    }

    public int memberCount() {
        return members.size();
    }

    public void setRegionToLiberate(BlockPos position) {
        regionToLiberate = position.immutable();
    }

    public boolean shouldDespawnAfterTargetDeath() {
        if (targetId == null) {
            return false;
        }

        ServerPlayer target = level.getServer().getPlayerList().getPlayer(targetId);
        if (target == null || target.isAlive()) {
            return false;
        }

        double range = 128.0 * 128.0;
        return level.players().stream()
                .filter(player -> player != target && player.isAlive())
                .noneMatch(player -> player.distanceToSqr(target) <= range);
    }

    public boolean tick() {
        if (shouldDespawnAfterTargetDeath()) {
            discard();
            return false;
        }

        members.removeIf(this::removeInactiveMember);

        if (members.isEmpty()) {
            if (hasPlayerKill) {
                awardWaveCompletion();
            }
            bossEvent.removeAllPlayers();
            return false;
        }

        bossEvent.removeAllPlayers();
        for (var player : level.players()) {
            bossEvent.addPlayer(player);
        }
        bossEvent.setProgress((float) members.size() / initialMembers);

        return true;
    }

    private boolean removeInactiveMember(UUID uuid) {
        Entity entity = level.getEntity(uuid);
        if (entity == null) {
            return true;
        }

        if (entity instanceof LivingEntity living) {
            if (!living.isDeadOrDying() && !living.isRemoved()) {
                return false;
            }

            hasPlayerKill |= living.isDeadOrDying() && living.getKillCredit() instanceof ServerPlayer;
            return true;
        }

        return entity.isRemoved() || !entity.isAlive();
    }

    public void discard() {
        for (UUID uuid : members) {
            Entity entity = level.getEntity(uuid);
            if (entity != null) {
                Entity vehicle = entity.getVehicle();
                entity.discard();
                if (vehicle != null) {
                    vehicle.discard();
                }
            }
        }
        members.clear();
        bossEvent.removeAllPlayers();
    }

    private void awardWaveCompletion() {
        if (targetId == null) {
            return;
        }

        ServerPlayer target = level.getServer().getPlayerList().getPlayer(targetId);
        if (target != null) {
            ImmersivePillagersStats.awardWaveDefeated(target, type);
            PillagerManager.awardHordeConquerorProgress(target, type);
            target.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        }

        if (regionToLiberate != null) {
            HordeRegionData.get(level).enableSpawningAt(regionToLiberate);
            Component message = Component.translatable("message.immersive_pillagers.region_liberated");
            Vec3 center = regionToLiberate.getCenter();
            double range = 128.0 * 128.0;
            level.players().stream()
                    .filter(player -> player.distanceToSqr(center) <= range)
                    .forEach(player -> player.sendSystemMessage(message, true));
        }
    }
}
