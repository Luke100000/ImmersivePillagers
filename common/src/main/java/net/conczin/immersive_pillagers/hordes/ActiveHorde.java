package net.conczin.immersive_pillagers.hordes;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ActiveHorde {
    private final UUID id = UUID.randomUUID();
    private final String type;
    private final ServerLevel level;
    private final Set<UUID> members = new HashSet<>();
    private final int initialMembers;
    private final ServerBossEvent bossEvent;

    public ActiveHorde(String type, ServerLevel level, Iterable<? extends Entity> members) {
        this.type = type;
        this.level = level;

        for (Entity member : members) {
            this.members.add(member.getUUID());
        }
        this.initialMembers = Math.max(1, this.members.size());

        MutableComponent name = Component.translatable("horde." + ImmersivePillagers.MOD_ID + "." + type);
        this.bossEvent = new ServerBossEvent(name, BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);
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

    public boolean tick() {
        members.removeIf(uuid -> {
            Entity entity = level.getEntity(uuid);
            return entity == null || entity.isRemoved() || !entity.isAlive();
        });

        if (members.isEmpty()) {
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

    public void discard() {
        for (UUID uuid : members) {
            Entity entity = level.getEntity(uuid);
            if (entity != null) {
                entity.discard();
            }
        }
        members.clear();
        bossEvent.removeAllPlayers();
    }
}
