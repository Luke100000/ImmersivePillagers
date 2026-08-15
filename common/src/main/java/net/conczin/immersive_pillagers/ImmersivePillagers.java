package net.conczin.immersive_pillagers;

import net.conczin.immersive_pillagers.config.Config;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImmersivePillagers {
    public static final String MOD_ID = "immersive_pillagers";
    public static final String HORDE_ENTITY_TAG = "IsImmersivePillager";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final TagKey<EntityType<?>> HUMANOID_ENTITY_TYPES = TagKey.create(Registries.ENTITY_TYPE, locate("humanoid"));

    public static void init() {
        //noinspection ResultOfMethodCallIgnored
        Config.getInstance();
    }

    public static ResourceLocation locate(String name) {
        return new ResourceLocation(MOD_ID, name);
    }
}
