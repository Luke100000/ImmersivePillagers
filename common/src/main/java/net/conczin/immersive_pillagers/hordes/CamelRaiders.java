package net.conczin.immersive_pillagers.hordes;

import net.conczin.immersive_pillagers.PillagerManager;
import net.conczin.immersive_pillagers.compat.ImmersiveMelodiesCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Random;

import static net.minecraft.world.entity.EntityType.CAMEL;
import static net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;

public class CamelRaiders {
    private static final Random RANDOM = new Random();
    private static final int ATTEMPTS = 10;

    public static void spawn(ServerLevel level, BlockPos pos) {
        Camel vehicle = spawnCamel(level, pos);

        if (vehicle != null) {
            for (int i = 0; i < 2; i++) {
                // Spawn crew
                Raider raider = PillagerManager.spawnPillager(level, vehicle.position());
                raider.startRiding(vehicle);

                // Give the first pillager an instrument
                // todo duplicate
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

    public static Camel spawnCamel(ServerLevel level, BlockPos pos) {
        Camel entity = new Camel(CAMEL, level);

        int x = pos.getX() + RANDOM.nextInt(16) - 8;
        int z = pos.getZ() + RANDOM.nextInt(16) - 8;
        int y = level.getHeight(MOTION_BLOCKING_NO_LEAVES, x, z);

        for (int i = 0; i < ATTEMPTS; i++) {
            entity.setPos(x, y, z);
            entity.setYRot(RANDOM.nextFloat() * 360.0f);

            if (level.noCollision(entity, entity.getBoundingBox())) {
                break;
            }
        }

        if (!level.noCollision(entity, entity.getBoundingBox())) {
            return null;
        }

        // Equip weapon
        entity.equipSaddle(null);

        level.addFreshEntity(entity);

        return entity;
    }
}
