package net.conczin.immersive_pillagers.controllers;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.PillagerManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

public class BoatController {
    private static final double MAX_SPEED = 0.15;
    private static final double STOP_DISTANCE = 1.0;
    private static final float TURN_SPEED = 2.5f;

    public static void tick(Boat boat) {
        if (boat.level().isClientSide() || !boat.getTags().contains(ImmersivePillagers.HORDE_ENTITY_TAG) || !(boat.getControllingPassenger() instanceof Pillager pillager)) {
            return;
        }

        PillagerManager.getClosestPlayer(pillager).ifPresent(target -> {
            Vec3 direction = target.position().subtract(boat.position()).multiply(1.0, 0.0, 1.0);
            double distance = direction.length();
            if (distance <= STOP_DISTANCE) {
                boat.setDeltaMovement(0.0, boat.getDeltaMovement().y, 0.0);
                return;
            }

            Vec3 normalizedDirection = direction.normalize();
            double speed = Math.min(distance, MAX_SPEED);
            float targetYaw = (float) (Math.toDegrees(Math.atan2(normalizedDirection.z, normalizedDirection.x)) - 90.0);
            float yaw = Mth.approachDegrees(boat.getYRot(), targetYaw, TURN_SPEED);
            boat.setYRot(yaw);

            double heading = Math.toRadians(yaw + 90.0f);
            boat.setDeltaMovement(Math.cos(heading) * speed, boat.getDeltaMovement().y, Math.sin(heading) * speed);
        });
    }
}
