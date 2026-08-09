package net.conczin.immersive_pillagers.hordes;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@FunctionalInterface
public interface HordeSpawner {
    Optional<ActiveHorde> spawn(ServerLevel level, BlockPos position, @Nullable ServerPlayer target);
}
