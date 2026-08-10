package net.conczin.immersive_pillagers.hordes;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.compat.ImmersiveMelodiesCompat;
import net.conczin.immersive_pillagers.controllers.PillagerCombat;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HordeSpawnUtil {
    private static final int ATTEMPTS = 16;
    private static final int MIN_DISTANCE = 16;
    private static final int RANGE = 24;

    public static Optional<Vec3> findGroundSpawn(ServerLevel level, BlockPos origin, Entity entity) {
        for (int i = 0; i < ATTEMPTS; i++) {
            Vec3 offset = randomHorizontalOffset(level);
            int x = origin.getX() + (int) Math.round(offset.x);
            int z = origin.getZ() + (int) Math.round(offset.z);
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            Vec3 pos = new Vec3(x + 0.5, y, z + 0.5);
            if (!level.getFluidState(new BlockPos(x, y - 1, z)).is(FluidTags.WATER) && canFit(level, entity, pos)) {
                return Optional.of(pos);
            }
        }
        return Optional.empty();
    }

    public static Optional<Vec3> findAirSpawn(ServerLevel level, BlockPos origin, Entity entity) {
        for (int i = 0; i < ATTEMPTS; i++) {
            Vec3 offset = randomHorizontalOffset(level);
            double x = origin.getX() + 0.5 + offset.x;
            double z = origin.getZ() + 0.5 + offset.z;
            double y = origin.getY() + 8.0 + level.random.nextInt(12);
            Vec3 pos = new Vec3(x, y, z);
            if (canFit(level, entity, pos)) {
                return Optional.of(pos);
            }
        }
        return Optional.empty();
    }

    public static Optional<Vec3> findWaterSpawn(ServerLevel level, BlockPos origin) {
        for (int i = 0; i < ATTEMPTS; i++) {
            Vec3 offset = randomHorizontalOffset(level);
            int x = origin.getX() + (int) Math.round(offset.x);
            int z = origin.getZ() + (int) Math.round(offset.z);
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            if (level.getFluidState(new BlockPos(x, y - 1, z)).is(FluidTags.WATER)) {
                return Optional.of(new Vec3(x + 0.5, y, z + 0.5));
            }
        }
        return Optional.empty();
    }

    public static void placeRandomly(ServerLevel level, Entity entity, Vec3 pos) {
        entity.setPos(pos);
        entity.setYRot(level.random.nextFloat() * 360.0f);
    }

    public static List<Raider> addPillagerCrew(ServerLevel level, Entity vehicle, int seats) {
        List<Raider> crew = new ArrayList<>();
        for (int i = 0; i < seats; i++) {
            Pillager pillager = EntityType.PILLAGER.create(level);
            if (pillager == null) {
                continue;
            }

            addRaiderToVehicle(level, vehicle, pillager);
            PillagerCombat.setCrossbowAttackRange(pillager, 16.0f);

            if (i == 1) {
                ImmersiveMelodiesCompat.getInstrument(level).ifPresentOrElse(stack -> {
                    ImmersiveMelodiesCompat.playTrack(level, stack);
                    pillager.setItemInHand(InteractionHand.MAIN_HAND, stack);
                }, () -> pillager.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.CROSSBOW)));
            } else {
                pillager.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.CROSSBOW));
            }
            crew.add(pillager);
        }
        return crew;
    }

    public static List<Raider> addVindicatorCrew(ServerLevel level, Entity vehicle, int seats) {
        List<Raider> crew = new ArrayList<>();
        for (int i = 0; i < seats; i++) {
            Vindicator vindicator = EntityType.VINDICATOR.create(level);
            if (vindicator == null) {
                continue;
            }

            addRaiderToVehicle(level, vehicle, vindicator);
            vindicator.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_AXE));
            crew.add(vindicator);
        }
        return crew;
    }

    public static void soundAlarm(ServerLevel level) {
        for (ServerPlayer player : level.players()) {
            player.playNotifySound(SoundEvents.RAID_HORN.value(), SoundSource.NEUTRAL, 64, 1.0f);
        }
    }

    public static void markTransient(Entity entity) {
        entity.addTag(ImmersivePillagers.HORDE_ENTITY_TAG);
    }

    public static Camel createSaddledCamel(ServerLevel level) {
        Camel camel = new Camel(EntityType.CAMEL, level);
        camel.equipSaddle(null);
        return camel;
    }

    private static Vec3 randomHorizontalOffset(ServerLevel level) {
        double angle = level.random.nextDouble() * Math.PI * 2.0;
        int distance = MIN_DISTANCE + level.random.nextInt(RANGE);
        return new Vec3(Math.cos(angle) * distance, 0.0, Math.sin(angle) * distance);
    }

    private static void addRaiderToVehicle(ServerLevel level, Entity vehicle, Raider raider) {
        raider.setPos(vehicle.position());
        raider.addTag(ImmersivePillagers.MOD_ID);
        markTransient(raider);
        level.addFreshEntity(raider);
        raider.startRiding(vehicle, true);
    }

    private static boolean canFit(ServerLevel level, Entity entity, Vec3 pos) {
        entity.setPos(pos);
        return level.noCollision(entity, entity.getBoundingBox());
    }
}
