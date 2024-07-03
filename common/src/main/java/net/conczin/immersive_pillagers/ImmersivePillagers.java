package net.conczin.immersive_pillagers;

import net.minecraft.resources.ResourceLocation;

public class ImmersivePillagers {
    public static final String MOD_ID = "immersive_pillagers";

    public static void init() {
		// nop
    }

    public static ResourceLocation locate(String rideOfTheValkyries) {
        return new ResourceLocation(MOD_ID, rideOfTheValkyries);
    }
}
