package net.conczin.immersive_pillagers.controllers;

import immersive_aircraft.entity.GyrodyneEntity;
import immersive_aircraft.entity.InventoryVehicleEntity;
import immersive_aircraft.entity.VehicleEntity;
import immersive_aircraft.entity.inventory.VehicleInventoryDescription;
import immersive_aircraft.entity.inventory.slots.SlotDescription;
import net.conczin.immersive_pillagers.PillagerManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Random;

public class AircraftController {
    private static final Random RANDOM = new Random();
    private static final float INACCURACY = 1.0f;
    public static final ResourceLocation WEAPON = new ResourceLocation("immersive_aircraft", "rotary_cannon");

    private static float random(float scale) {
        return (RANDOM.nextFloat() - 0.5f) * scale;
    }

    public static void tickPilot(Object vehicleObject) {
        VehicleEntity vehicle = (VehicleEntity) vehicleObject;
        if (vehicle instanceof GyrodyneEntity gyrodyne) {
            gyrodyne.setEngineTarget(1.0f);
        }

        LivingEntity pilot = vehicle.getControllingPassenger();
        if (pilot instanceof Pillager pillager && !vehicle.level().isClientSide()) {
            PillagerManager.getClosestPlayer(pillager).ifPresent(player -> {
                Vec3 target = player.position().add(0.0, randomizeHeight(vehicle), 0.0);
                Vec3 dir = target.subtract(vehicle.position()).normalize();

                // Rotate towards
                float yaw = (float) Math.toDegrees(Math.atan2(dir.z, dir.x)) - vehicle.getYRot();
                float diffYaw = -Mth.wrapDegrees(yaw - 90);

                // Control
                vehicle.setInputs(Math.min(Math.max(diffYaw / 90.0f, -1.0f), 1.0f), (float) dir.y, randomizeSpeed(vehicle));

                // Shoot
                if (vehicle instanceof InventoryVehicleEntity weaponizedVehicle && vehicle.level().getGameTime() % 20 == 0) {
                    ResourceLocation weapon = BuiltInRegistries.ITEM.getKey(vehicle.getSlot(0).get().getItem());
                    if (weapon.equals(WEAPON)) {
                        Vec3 aim = player.position().subtract(vehicle.position());
                        float randomness = (float) (INACCURACY * aim.length());
                        aim.add(random(randomness), aim.y * 0.1 + random(randomness), random(randomness));
                        aim = aim.normalize();

                        List<SlotDescription> slots = weaponizedVehicle.getInventoryDescription().getSlots(VehicleInventoryDescription.INVENTORY);
                        weaponizedVehicle.getInventory().setItem(slots.get(0).index(), new ItemStack(Items.GUNPOWDER, 1));
                        weaponizedVehicle.fireWeapon(0, 0, aim.toVector3f());
                    }
                }
            });
        }
    }

    private static float randomizeSpeed(VehicleEntity vehicle) {
        return (float) ((vehicle.getId() * 1.7 % 1.0) * 0.5 + 0.5);
    }

    private static float randomizeHeight(VehicleEntity vehicle) {
        return (float) ((vehicle.getId() * 0.7 % 1.0) * 4.0 + 1.0);
    }
}
