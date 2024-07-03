package net.conczin.immersive_pillagers;

import immersive_aircraft.Entities;
import immersive_aircraft.data.VehicleDataLoader;
import immersive_aircraft.entity.GyrodyneEntity;
import immersive_aircraft.entity.InventoryVehicleEntity;
import immersive_aircraft.entity.VehicleEntity;
import immersive_aircraft.entity.inventory.VehicleInventoryDescription;
import immersive_aircraft.entity.inventory.slots.SlotDescription;
import net.conczin.immersive_pillagers.combat.ImmersiveMelodiesCombat;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static immersive_aircraft.Items.ROTARY_CANNON;

public class PillagerManager {
    public static void spawnThemBois(ServerPlayer player) {
        GyrodyneEntity vehicle = spawnGyrodyne(player);

        if (vehicle != null) {
            int size = VehicleDataLoader.get(vehicle.identifier).getPassengerPositions().size();
            for (int i = 0; i < size; i++) {
                // Spawn crew
                Raider raider = spawnPillager((ServerLevel) player.level(), vehicle.position());
                raider.startRiding(vehicle);

                // Give the first pillager an instrument
                if (i == 0) {
                    ImmersiveMelodiesCombat.getInstrument(player.level())
                            .ifPresent(stack -> {
                                ImmersiveMelodiesCombat.playTrack(player.level(), stack);
                                raider.setItemInHand(InteractionHand.MAIN_HAND, stack);
                            });
                } else {
                    raider.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.CROSSBOW));
                }
            }

            // Sound the alarm
            player.sendSystemMessage(Component.literal("Pillagers are closing in!"));
            vehicle.playSound(SoundEvents.RAID_HORN.value(), 2.0f, 1.0f);
        }
    }

    private static Raider spawnPillager(ServerLevel level, Vec3 pos) {
        // EntityType.EVOKER
        // EntityType.VINDICATOR
        Pillager pillager = EntityType.PILLAGER.create(level);
        assert pillager != null;
        pillager.setPos(pos);
        level.addFreshEntityWithPassengers(pillager);
        return pillager;
    }

    public static GyrodyneEntity spawnGyrodyne(ServerPlayer player) {
        Level level = player.level();
        GyrodyneEntity entity = new GyrodyneEntity(Entities.GYRODYNE.get(), level);

        Vec3 vec3 = new Vec3(player.getX(), player.getY() + 8.0, player.getZ());

        entity.setPos(vec3);
        entity.setYRot(0.0f);

        if (!level.noCollision(entity, entity.getBoundingBox())) {
            return null;
        }

        // Equip weapon
        entity.getInventory().setItem(0, new ItemStack(ROTARY_CANNON.get()));

        level.addFreshEntity(entity);

        return entity;
    }

    public static void tickPilot(VehicleEntity vehicle) {
        if (vehicle instanceof GyrodyneEntity gyrodyne) {
            gyrodyne.setEngineTarget(1.0f);
        }

        LivingEntity pilot = vehicle.getControllingPassenger();
        if (pilot instanceof Pillager pillager && !vehicle.level().isClientSide()) {
            getClosestPlayer(pillager).ifPresent(player -> {
                Vec3 target = player.position().add(0.0, randomizeHeight(vehicle), 0.0);
                Vec3 dir = target.subtract(vehicle.position()).normalize();

                // Rotate towards
                float yaw = (float) Math.toDegrees(Math.atan2(dir.z, dir.x)) - vehicle.getYRot();
                float diffYaw = -Mth.wrapDegrees(yaw - 90);

                // Control
                vehicle.setInputs(Math.min(Math.max(diffYaw / 90.0f, -1.0f), 1.0f), (float) dir.y, randomizeSpeed(vehicle)); // todo randomize speed to allow hordes

                // Shoot
                if (vehicle instanceof InventoryVehicleEntity weaponizedVehicle && vehicle.level().getGameTime() % 20 == 0) {
                    ResourceLocation weapon = BuiltInRegistries.ITEM.getKey(vehicle.getSlot(0).get().getItem());
                    if (weapon.equals(new ResourceLocation("immersive_aircraft", "rotary_cannon"))) {
                        Vec3 aim = player.position().subtract(vehicle.position());
                        // todo randomness
                        aim.add(0.0, aim.y * 0.1, 0.0);
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
        return (float) ((vehicle.getId() * 0.7 % 1.0) * 4.0 + 2.0);
    }

    private static @NotNull Optional<? extends Player> getClosestPlayer(Pillager pillager) {
        return pillager.level().players().stream().min((a, b) -> (int) (a.distanceToSqr(pillager) - b.distanceToSqr(pillager)));
    }

    public static boolean canTurnOnEngine(VehicleEntity vehicleEntity) {
        return vehicleEntity.getControllingPassenger() instanceof Pillager;
    }
}
