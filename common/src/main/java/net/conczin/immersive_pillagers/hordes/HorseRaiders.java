package net.conczin.immersive_pillagers.hordes;

import net.conczin.immersive_pillagers.PillagerManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.raid.Raider;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HorseRaiders {
    public static Optional<ActiveHorde> spawn(ServerLevel level, BlockPos pos, @Nullable ServerPlayer target) {
        Horse entity = new Horse(EntityType.HORSE, level);
        entity.equipSaddle(null);
        var spawnPos = HordeSpawnUtil.findGroundSpawn(level, pos, entity);
        if (spawnPos.isEmpty()) {
            return Optional.empty();
        }

        HordeSpawnUtil.placeRandomly(level, entity, spawnPos.get());
        HordeSpawnUtil.markTransient(entity);
        level.addFreshEntity(entity);

        List<Raider> crew = HordeSpawnUtil.addPillagerCrew(level, entity, 1);
        if (target != null) {
            crew.forEach(raider -> raider.setTarget(target));
        }
        HordeSpawnUtil.soundAlarm(level);

        List<Entity> members = new ArrayList<>();
        members.add(entity);
        members.addAll(crew);
        return Optional.of(new ActiveHorde(PillagerManager.HORDE_HORSE, level, members));
    }
}
