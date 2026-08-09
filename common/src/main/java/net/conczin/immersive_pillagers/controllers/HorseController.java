package net.conczin.immersive_pillagers.controllers;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.PillagerManager;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.phys.Vec3;

public class HorseController {
    private static final double SPEED_MODIFIER = 4.0;
    private static final double MIN_DISTANCE = 5.0;
    private static final double MAX_DISTANCE = 10.0;

    public static void tick(AbstractHorse horse) {
        if (horse.level().isClientSide() || !horse.getTags().contains(ImmersivePillagers.HORDE_ENTITY_TAG) || !(horse.getControllingPassenger() instanceof Pillager pillager)) {
            return;
        }

        PillagerManager.getClosestPlayer(pillager).ifPresent(target -> {
            double distance = horse.distanceToSqr(target);
            if (distance >= MIN_DISTANCE * MIN_DISTANCE && distance <= MAX_DISTANCE * MAX_DISTANCE) {
                horse.getNavigation().stop();
                return;
            }

            Vec3 direction = horse.position().subtract(target.position()).normalize();
            Vec3 destination = target.position().add(direction.scale((MIN_DISTANCE + MAX_DISTANCE) / 2.0));
            horse.getNavigation().moveTo(destination.x, destination.y, destination.z, SPEED_MODIFIER);
        });
    }
}
