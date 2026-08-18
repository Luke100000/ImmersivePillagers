package net.conczin.immersive_pillagers.hordes;

import net.conczin.immersive_pillagers.PillagerManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HorseRaiders {
    public static Optional<ActiveHorde> spawn(ServerLevel level, BlockPos pos, @Nullable ServerPlayer target, int difficulty) {
        List<Entity> members = new ArrayList<>();
        int groupCount = HordeSpawnUtil.getVehicleGroupCount(level, difficulty, HordeSpawnUtil.SINGLE_RIDER_GROUP_FACTOR);
        for (int i = 0; i < groupCount; i++) {
            Horse entity = new Horse(EntityType.HORSE, level);
            entity.setItemSlot(EquipmentSlot.SADDLE, new ItemStack(Items.SADDLE));
            var spawnPos = HordeSpawnUtil.findGroundSpawn(level, pos, entity);
            if (spawnPos.isEmpty()) {
                continue;
            }

            members.addAll(HordeSpawnUtil.spawnPillagerVehicleGroup(level, entity, spawnPos.get(), 1, target, PillagerManager.HORDE_HORSE));
        }
        if (members.isEmpty()) {
            return Optional.empty();
        }
        HordeSpawnUtil.soundAlarm(level, members.get(0));
        return Optional.of(new ActiveHorde(PillagerManager.HORDE_HORSE, level, members, target));
    }
}
