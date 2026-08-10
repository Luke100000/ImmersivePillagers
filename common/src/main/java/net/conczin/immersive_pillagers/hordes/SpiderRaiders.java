package net.conczin.immersive_pillagers.hordes;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.raid.Raider;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpiderRaiders {
    public static Optional<ActiveHorde> spawn(ServerLevel level, BlockPos pos, @Nullable ServerPlayer target) {
        Spider entity = new Spider(EntityType.SPIDER, level);
        var spawnPos = HordeSpawnUtil.findGroundSpawn(level, pos, entity);
        if (spawnPos.isEmpty()) {
            return Optional.empty();
        }

        HordeSpawnUtil.placeRandomly(level, entity, spawnPos.get());
        HordeSpawnUtil.markTransient(entity);
        level.addFreshEntity(entity);

        List<Raider> crew = level.random.nextBoolean()
                ? HordeSpawnUtil.addPillagerCrew(level, entity, 1)
                : HordeSpawnUtil.addVindicatorCrew(level, entity, 1);
        if (target != null) {
            crew.forEach(raider -> raider.setTarget(target));
        }
        HordeSpawnUtil.soundAlarm(level);

        List<Entity> members = new ArrayList<>();
        members.add(entity);
        members.addAll(crew);
        return Optional.of(new ActiveHorde("spider", Component.translatable("horde.immersive_pillagers.spider_raiders"), level, members));
    }
}
