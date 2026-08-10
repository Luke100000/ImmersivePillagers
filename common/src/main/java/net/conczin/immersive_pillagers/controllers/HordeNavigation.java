package net.conczin.immersive_pillagers.controllers;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

public final class HordeNavigation {
    private static final double ENTITY_ID_PHASE = 1.7;

    public static void orbit(Mob entity, Entity center, double radius, double orbitSpeed, double maxSpeed, double stopDistance, double speedScale) {
        double angle = entity.getId() * ENTITY_ID_PHASE + entity.level().getGameTime() * orbitSpeed;
        Vec3 destination = center.position().add(Math.cos(angle) * radius, 0.0, Math.sin(angle) * radius);
        moveTo(entity, destination, maxSpeed, stopDistance, speedScale);
    }

    public static void moveTo(Mob entity, Vec3 destination, double maxSpeed, double stopDistance, double speedScale) {
        double speed = Math.min(entity.position().distanceTo(destination) - stopDistance, maxSpeed);
        if (speed > 0.01) {
            entity.getNavigation().moveTo(destination.x, destination.y, destination.z, speed * speedScale);
        } else {
            entity.getNavigation().stop();
        }
    }
}
