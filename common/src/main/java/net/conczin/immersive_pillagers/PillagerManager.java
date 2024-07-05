package net.conczin.immersive_pillagers;

import immersive_aircraft.entity.VehicleEntity;
import net.conczin.immersive_pillagers.hordes.AirborneRaiders;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public class PillagerManager {
    private static final Map<String, BiConsumer<ServerLevel, BlockPos>> HORDES = new HashMap<>();

    public static String registerHorde(String name, BiConsumer<ServerLevel, BlockPos> horde) {
        HORDES.put(name, horde);
        return name;
    }

    public static final String HORDE_GYRODYNE = registerHorde("gyrodyne", AirborneRaiders::spawn);

    public static void spawnHorde(ServerPlayer player, String horde) {
        if (player.level() instanceof ServerLevel level) {
            player.sendSystemMessage(Component.literal("Pillagers are closing in!"));
            HORDES.get(horde).accept(level, player.blockPosition());
        }
    }

    public static Raider spawnPillager(ServerLevel level, Vec3 pos) {
        // EntityType.EVOKER
        // EntityType.VINDICATOR
        Pillager pillager = EntityType.PILLAGER.create(level);
        assert pillager != null;
        pillager.setPos(pos);
        level.addFreshEntityWithPassengers(pillager);
        return pillager;
    }

    public static @NotNull Optional<? extends Player> getClosestPlayer(Pillager pillager) {
        return pillager.level().players().stream().min((a, b) -> (int) (a.distanceToSqr(pillager) - b.distanceToSqr(pillager)));
    }

    public static boolean canTurnOnEngine(VehicleEntity vehicleEntity) {
        return vehicleEntity.getControllingPassenger() instanceof Pillager;
    }
}
