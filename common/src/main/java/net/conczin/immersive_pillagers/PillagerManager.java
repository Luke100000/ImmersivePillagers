package net.conczin.immersive_pillagers;

import immersive_aircraft.entity.VehicleEntity;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.conczin.immersive_pillagers.hordes.ActiveHorde;
import net.conczin.immersive_pillagers.hordes.AirborneRaiders;
import net.conczin.immersive_pillagers.hordes.CamelRaiders;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class PillagerManager {
    private static final Map<String, BiFunction<ServerLevel, BlockPos, Optional<ActiveHorde>>> HORDES = new HashMap<>();
    private static final Map<UUID, ActiveHorde> ACTIVE_HORDES = new HashMap<>();

    public static String registerHorde(String name, BiFunction<ServerLevel, BlockPos, Optional<ActiveHorde>> horde) {
        HORDES.put(name, horde);
        return name;
    }

    public static final String HORDE_GYRODYNE = registerHorde("gyrodyne", AirborneRaiders::spawn);
    public static final String HORDE_CAMEL = registerHorde("camel", CamelRaiders::spawn);

    public static int spawnHorde(CommandSourceStack source, String horde) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        if (!(player.level() instanceof ServerLevel level)) {
            source.sendFailure(Component.literal("Hordes can only spawn on the server."));
            return 0;
        }

        BiFunction<ServerLevel, BlockPos, Optional<ActiveHorde>> factory = HORDES.get(horde);
        if (factory == null) {
            source.sendFailure(Component.literal("Unknown horde '" + horde + "'. Available: " + availableHordes()));
            return 0;
        }

        Optional<ActiveHorde> activeHorde = factory.apply(level, player.blockPosition());
        if (activeHorde.isEmpty()) {
            source.sendFailure(Component.literal("Could not find a safe spawn position for " + horde + "."));
            return 0;
        }

        ActiveHorde spawned = activeHorde.get();
        ACTIVE_HORDES.put(spawned.id(), spawned);
        source.sendSuccess(() -> Component.literal("Pillagers are closing in: " + spawned.type() + " (" + spawned.memberCount() + " members)."), true);
        return 1;
    }

    public static int listHordes(CommandSourceStack source) {
        if (ACTIVE_HORDES.isEmpty()) {
            source.sendSuccess(() -> Component.literal("No active hordes."), false);
            return 0;
        }

        String hordes = ACTIVE_HORDES.values().stream()
                .map(horde -> horde.type() + " " + horde.id().toString().substring(0, 8) + " (" + horde.memberCount() + " members)")
                .collect(Collectors.joining(", "));
        source.sendSuccess(() -> Component.literal("Active hordes: " + hordes), false);
        return ACTIVE_HORDES.size();
    }

    public static int clearHordes(CommandSourceStack source) {
        List<ActiveHorde> hordes = new ArrayList<>(ACTIVE_HORDES.values());
        for (ActiveHorde horde : hordes) {
            horde.discard();
        }
        ACTIVE_HORDES.clear();
        source.sendSuccess(() -> Component.literal("Cleared " + hordes.size() + " active hordes."), true);
        return hordes.size();
    }

    public static void tick(MinecraftServer server) {
        ACTIVE_HORDES.values().removeIf(horde -> !horde.tick());
    }

    private static String availableHordes() {
        return String.join(", ", HORDES.keySet());
    }

    public static @NotNull Optional<? extends Player> getClosestPlayer(Pillager pillager) {
        return pillager.level().players().stream().min((a, b) -> (int) (a.distanceToSqr(pillager) - b.distanceToSqr(pillager)));
    }

    public static boolean canTurnOnEngine(VehicleEntity vehicleEntity) {
        return vehicleEntity.getControllingPassenger() instanceof Pillager;
    }
}
