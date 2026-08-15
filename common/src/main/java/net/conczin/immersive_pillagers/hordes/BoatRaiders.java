package net.conczin.immersive_pillagers.hordes;

import net.conczin.immersive_pillagers.PillagerManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class BoatRaiders {
    public static Optional<ActiveHorde> spawn(ServerLevel level, BlockPos pos, @Nullable ServerPlayer target, int difficulty) {
        List<Entity> members = new ArrayList<>();
        int groupCount = HordeSpawnUtil.getVehicleGroupCount(level, difficulty, HordeSpawnUtil.TWO_RIDER_GROUP_FACTOR);
        for (int i = 0; i < groupCount; i++) {
            Optional<Vec3> spawnPos = HordeSpawnUtil.findWaterSpawn(level, pos);
            if (spawnPos.isEmpty()) {
                continue;
            }

            Boat entity = new Boat(level, spawnPos.get().x, spawnPos.get().y, spawnPos.get().z);
            members.addAll(HordeSpawnUtil.spawnPillagerVehicleGroup(level, entity, spawnPos.get(), 2, target, PillagerManager.HORDE_BOAT));
        }
        if (members.isEmpty()) {
            return Optional.empty();
        }
        HordeSpawnUtil.soundAlarm(level);
        return Optional.of(new ActiveHorde(PillagerManager.HORDE_BOAT, level, members, target));
    }
}
