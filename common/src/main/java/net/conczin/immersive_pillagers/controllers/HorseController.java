package net.conczin.immersive_pillagers.controllers;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.PillagerManager;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.phys.Vec3;

public class HorseController {
    private static final double MAX_SPEED_MODIFIER = 4.0;
    private static final double ORBIT_RADIUS = 8.0;
    private static final double ORBIT_SPEED = 0.01;
    private static final double ENTITY_ID_PHASE = 1.7;

    public static void tick(AbstractHorse horse) {
        if (horse.level().isClientSide() || !horse.getTags().contains(ImmersivePillagers.HORDE_ENTITY_TAG) || !(horse.getControllingPassenger() instanceof Pillager pillager)) {
            return;
        }

        PillagerManager.getClosestPlayer(pillager).ifPresent(target -> {
            double angle = horse.getId() * ENTITY_ID_PHASE + horse.level().getGameTime() * ORBIT_SPEED;
            Vec3 destination = target.position().add(Math.cos(angle) * ORBIT_RADIUS, 0.0, Math.sin(angle) * ORBIT_RADIUS);
            double speedModifier = Math.min(horse.position().distanceTo(destination), MAX_SPEED_MODIFIER);
            if (speedModifier > 0.1) {
                horse.getNavigation().moveTo(destination.x, destination.y, destination.z, speedModifier);
            } else {
                horse.getNavigation().stop();
            }
        });
    }
}
