package net.conczin.immersive_pillagers.controllers;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.PillagerManager;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Pillager;

public class HorseController {
    private static final double MAX_SPEED = 0.36;
    private static final double ORBIT_RADIUS = 8.0;
    private static final double ORBIT_SPEED = 0.01;
    private static final double DISTANCE_THRESHOLD = 1.0;

    public static void tick(AbstractHorse horse) {
        if (horse.level().isClientSide() || !horse.getTags().contains(ImmersivePillagers.HORDE_ENTITY_TAG) || !(horse.getControllingPassenger() instanceof Pillager pillager)) {
            return;
        }

        PillagerManager.getClosestPlayer(pillager).ifPresent(target -> HordeNavigation.orbit(
                horse, target, ORBIT_RADIUS, ORBIT_SPEED, MAX_SPEED, DISTANCE_THRESHOLD,
                1.0 / horse.getAttributeValue(Attributes.MOVEMENT_SPEED)
        ));
    }
}
