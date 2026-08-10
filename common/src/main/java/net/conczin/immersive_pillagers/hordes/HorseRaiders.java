package net.conczin.immersive_pillagers.hordes;

import net.conczin.immersive_pillagers.PillagerManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Horse;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HorseRaiders {
    public static Optional<ActiveHorde> spawn(ServerLevel level, BlockPos pos, @Nullable ServerPlayer target, int difficulty) {
        List<Entity> members = new ArrayList<>();
        int groupCount = HordeSpawnUtil.getVehicleGroupCount(level, difficulty);
        for (int i = 0; i < groupCount; i++) {
            Horse entity = new Horse(EntityType.HORSE, level);
            entity.equipSaddle(null);
            var spawnPos = HordeSpawnUtil.findGroundSpawn(level, pos, entity);
            if (spawnPos.isEmpty()) {
                continue;
            }

            members.addAll(HordeSpawnUtil.spawnPillagerVehicleGroup(level, entity, spawnPos.get(), 1, target));
        }
        if (members.isEmpty()) {
            return Optional.empty();
        }
        HordeSpawnUtil.soundAlarm(level);
        return Optional.of(new ActiveHorde(PillagerManager.HORDE_HORSE, level, members, target));
    }
}
