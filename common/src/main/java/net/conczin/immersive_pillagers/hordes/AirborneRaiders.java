package net.conczin.immersive_pillagers.hordes;

import immersive_aircraft.Entities;
import immersive_aircraft.data.VehicleDataLoader;
import immersive_aircraft.entity.GyrodyneEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

import static immersive_aircraft.Items.ROTARY_CANNON;

public class AirborneRaiders {
    public static Optional<ActiveHorde> spawn(ServerLevel level, BlockPos pos, @Nullable ServerPlayer target) {
        GyrodyneEntity entity = new GyrodyneEntity(Entities.GYRODYNE.get(), level);
        var spawnPos = HordeSpawnUtil.findAirSpawn(level, pos, entity);
        if (spawnPos.isEmpty()) {
            return Optional.empty();
        }

        HordeSpawnUtil.placeRandomly(level, entity, spawnPos.get());
        HordeSpawnUtil.markTransient(entity);
        entity.getInventory().setItem(0, new ItemStack(ROTARY_CANNON.get()));
        level.addFreshEntity(entity);

        int seats = VehicleDataLoader.get(entity.identifier).getPassengerPositions().size();
        List<Raider> crew = HordeSpawnUtil.addPillagerCrew(level, entity, seats);
        HordeSpawnUtil.soundAlarm(level);

        List<Entity> members = new ArrayList<>();
        members.add(entity);
        members.addAll(crew);
        return Optional.of(new ActiveHorde("gyrodyne", Component.translatable("horde.immersive_pillagers.gyrodyne_raiders"), level, members));
    }
}
