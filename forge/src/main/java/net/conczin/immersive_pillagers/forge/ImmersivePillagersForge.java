package net.conczin.immersive_pillagers.forge;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ImmersivePillagers.MOD_ID)
public class ImmersivePillagersForge {
    public ImmersivePillagersForge() {
        ImmersivePillagers.init();
    }
}