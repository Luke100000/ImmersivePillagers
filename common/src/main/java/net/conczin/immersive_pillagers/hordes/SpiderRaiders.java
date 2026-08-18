package net.conczin.immersive_pillagers.hordes;

import net.conczin.immersive_pillagers.PillagerManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.raid.Raider;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpiderRaiders {
    public static Optional<ActiveHorde> spawn(ServerLevel level, BlockPos pos, @Nullable ServerPlayer target, int difficulty) {
        List<Entity> members = new ArrayList<>();
        int groupCount = HordeSpawnUtil.getVehicleGroupCount(level, difficulty, HordeSpawnUtil.SINGLE_RIDER_GROUP_FACTOR);
        for (int i = 0; i < groupCount; i++) {
            Spider entity = new Spider(EntityType.SPIDER, level);
            var spawnPos = HordeSpawnUtil.findGroundSpawn(level, pos, entity);
            if (spawnPos.isEmpty()) {
                continue;
            }

            HordeSpawnUtil.placeRandomly(level, entity, spawnPos.get());
            HordeSpawnUtil.markTransient(entity);
            level.addFreshEntity(entity);

            List<Raider> crew = level.getRandom().nextBoolean()
                    ? HordeSpawnUtil.addPillagerCrew(level, entity, 1, PillagerManager.HORDE_SPIDER)
                    : HordeSpawnUtil.addVindicatorCrew(level, entity, 1);
            if (target != null) {
                crew.forEach(raider -> raider.setTarget(target));
            }
            members.addAll(crew);
        }
        if (members.isEmpty()) {
            return Optional.empty();
        }
        HordeSpawnUtil.soundAlarm(level, members.get(0));
        return Optional.of(new ActiveHorde(PillagerManager.HORDE_SPIDER, level, members, target));
    }
}
