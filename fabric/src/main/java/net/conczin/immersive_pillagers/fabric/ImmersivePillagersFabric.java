package net.conczin.immersive_pillagers.fabric;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.fabricmc.api.ModInitializer;

public class ImmersivePillagersFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ImmersivePillagers.init();
    }
}