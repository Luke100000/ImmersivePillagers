package net.conczin.immersive_pillagers.hordes;

import immersive_aircraft.Entities;
import immersive_aircraft.data.VehicleDataLoader;
import immersive_aircraft.entity.GyrodyneEntity;
import net.conczin.immersive_pillagers.PillagerManager;
import net.conczin.immersive_pillagers.compat.ImmersiveMelodiesCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Random;

import static immersive_aircraft.Items.ROTARY_CANNON;

public class AirborneRaiders {
    private static final Random RANDOM = new Random();
    private static final int ATTEMPTS = 10;

    public static void spawn(ServerLevel level, BlockPos pos) {
        GyrodyneEntity vehicle = spawnGyrodyne(level, pos);

        if (vehicle != null) {
            int size = VehicleDataLoader.get(vehicle.identifier).getPassengerPositions().size();
            for (int i = 0; i < size; i++) {
                // Spawn crew
                Raider raider = PillagerManager.spawnPillager(level, vehicle.position());
                raider.startRiding(vehicle);

                // Give the first pillager an instrument
                if (i == 0) {
                    ImmersiveMelodiesCompat.getInstrument(level)
                            .ifPresent(stack -> {
                                ImmersiveMelodiesCompat.playTrack(level, stack);
                                raider.setItemInHand(InteractionHand.MAIN_HAND, stack);
                            });
                } else {
                    raider.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.CROSSBOW));
                }
            }

            // Sound the alarm
            vehicle.playSound(SoundEvents.RAID_HORN.value(), 2.0f, 1.0f);
        }
    }

    public static GyrodyneEntity spawnGyrodyne(ServerLevel level, BlockPos pos) {
        GyrodyneEntity entity = new GyrodyneEntity(Entities.GYRODYNE.get(), level);

        for (int i = 0; i < ATTEMPTS; i++) {
            entity.setPos(
                    pos.getX() + RANDOM.nextInt(16) - 8,
                    pos.getY() + RANDOM.nextInt(12) + 2.0,
                    pos.getZ() + RANDOM.nextInt(16) - 8
            );
            entity.setYRot(RANDOM.nextFloat() * 360.0f);

            if (level.noCollision(entity, entity.getBoundingBox())) {
                break;
            }
        }

        if (!level.noCollision(entity, entity.getBoundingBox())) {
            return null;
        }

        // Equip weapon
        entity.getInventory().setItem(0, new ItemStack(ROTARY_CANNON.get()));

        level.addFreshEntity(entity);

        return entity;
    }
}
