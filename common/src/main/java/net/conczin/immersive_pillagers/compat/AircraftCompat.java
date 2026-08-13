package net.conczin.immersive_pillagers.compat;

import net.conczin.immersive_pillagers.PillagerManager;
import net.conczin.immersive_pillagers.hordes.AirborneRaiders;

public final class AircraftCompat {
    public static void register() {
        PillagerManager.registerHorde(PillagerManager.HORDE_GYRODYNE, AirborneRaiders::spawn);
    }
}
