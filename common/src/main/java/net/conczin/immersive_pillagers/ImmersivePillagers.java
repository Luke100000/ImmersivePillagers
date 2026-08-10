package net.conczin.immersive_pillagers;

import net.conczin.immersive_pillagers.config.Config;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImmersivePillagers {
    public static final String MOD_ID = "immersive_pillagers";
    public static final String HORDE_ENTITY_TAG = "IsImmersivePillager";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        Config.getInstance();
    }

    public static ResourceLocation locate(String rideOfTheValkyries) {
        return new ResourceLocation(MOD_ID, rideOfTheValkyries);
    }
}
