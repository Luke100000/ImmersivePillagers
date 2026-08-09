package net.conczin.immersive_pillagers;

import immersive_aircraft.entity.VehicleEntity;
import net.conczin.immersive_pillagers.hordes.ActiveHorde;
import net.conczin.immersive_pillagers.hordes.AirborneRaiders;
import net.conczin.immersive_pillagers.hordes.CamelRaiders;
import net.conczin.immersive_pillagers.hordes.HordeSpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PillagerManager {
    private static final Map<String, HordeSpawner> HORDES = new HashMap<>();
    private static final Map<UUID, ActiveHorde> ACTIVE_HORDES = new HashMap<>();

    public static String registerHorde(String name, HordeSpawner horde) {
        HORDES.put(name, horde);
        return name;
    }

    public static final String HORDE_GYRODYNE = registerHorde("gyrodyne", AirborneRaiders::spawn);
    public static final String HORDE_CAMEL = registerHorde("camel", CamelRaiders::spawn);

    public static Optional<ActiveHorde> spawnHorde(String horde, ServerLevel level, BlockPos position, @Nullable ServerPlayer target) {
        HordeSpawner spawner = HORDES.get(horde);
        return spawner == null ? Optional.empty() : spawner.spawn(level, position, target);
    }

    public static boolean isHordeRegistered(String horde) {
        return HORDES.containsKey(horde);
    }

    public static List<String> getHordeNames() {
        return List.copyOf(HORDES.keySet());
    }

    public static void addActiveHorde(ActiveHorde horde) {
        ACTIVE_HORDES.put(horde.id(), horde);
    }

    public static List<ActiveHorde> getActiveHordes() {
        return List.copyOf(ACTIVE_HORDES.values());
    }

    public static int clearHordes() {
        int clearedHordes = ACTIVE_HORDES.size();
        for (ActiveHorde horde : ACTIVE_HORDES.values()) {
            horde.discard();
        }
        ACTIVE_HORDES.clear();
        return clearedHordes;
    }

    public static void tick(MinecraftServer server) {
        ACTIVE_HORDES.values().removeIf(horde -> !horde.tick());
    }

    public static @NotNull Optional<? extends Player> getClosestPlayer(Pillager pillager) {
        return pillager.level().players().stream().min((a, b) -> (int) (a.distanceToSqr(pillager) - b.distanceToSqr(pillager)));
    }

    public static boolean canTurnOnEngine(VehicleEntity vehicleEntity) {
        return vehicleEntity.getControllingPassenger() instanceof Pillager;
    }
}
