package net.conczin.immersive_pillagers.hordes;

import immersive_aircraft.Entities;
import immersive_aircraft.data.VehicleDataLoader;
import immersive_aircraft.entity.GyrodyneEntity;
import net.conczin.immersive_pillagers.PillagerManager;
import net.conczin.immersive_pillagers.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

import static immersive_aircraft.Items.ROTARY_CANNON;

public class AirborneRaiders {
    public static Optional<ActiveHorde> spawn(ServerLevel level, BlockPos pos, @Nullable ServerPlayer target, int difficulty) {
        List<Entity> members = new ArrayList<>();
        int groupCount = HordeSpawnUtil.getVehicleGroupCount(level, difficulty, 0.2);
        for (int i = 0; i < groupCount; i++) {
            GyrodyneEntity entity = new GyrodyneEntity(Entities.GYRODYNE.get(), level);
            var spawnPos = HordeSpawnUtil.findAirSpawn(level, pos, entity);
            if (spawnPos.isEmpty()) {
                continue;
            }

            if (level.random.nextDouble() < Config.getInstance().rotaryCannonChance) {
                entity.getInventory().setItem(0, new ItemStack(ROTARY_CANNON.get()));
            }

            int seats = VehicleDataLoader.get(entity.identifier).getPassengerPositions().size();
            members.addAll(HordeSpawnUtil.spawnPillagerVehicleGroup(level, entity, spawnPos.get(), seats, target, PillagerManager.HORDE_GYRODYNE));
        }
        if (members.isEmpty()) {
            return Optional.empty();
        }
        HordeSpawnUtil.soundAlarm(level);
        return Optional.of(new ActiveHorde(PillagerManager.HORDE_GYRODYNE, level, members, target));
    }
}
