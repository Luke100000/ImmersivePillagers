package net.conczin.immersive_pillagers.compat;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class ImmersiveMelodiesCompat {
    public static void playTrack(Level level, ItemStack stack) {
        // Restored when Immersive Melodies is ported to 26.1.2
    }

    public static void playTrack(Level level, ItemStack stack, String hordeType) {
        // Restored when Immersive Melodies is ported to 26.1.2
    }

    public static Optional<ItemStack> getInstrument(Level level) {
        return Optional.empty();
    }
}
