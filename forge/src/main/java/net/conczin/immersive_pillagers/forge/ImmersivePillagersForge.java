package net.conczin.immersive_pillagers.forge;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.ImmersivePillagersStats;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

import static net.minecraft.core.registries.Registries.CUSTOM_STAT;

@Mod(ImmersivePillagers.MOD_ID)
public class ImmersivePillagersForge {
    public ImmersivePillagersForge() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ImmersivePillagersForge::register);
        ImmersivePillagers.init();
    }

    private static void register(RegisterEvent event) {
        if (event.getRegistryKey().equals(CUSTOM_STAT)) {
            event.register(CUSTOM_STAT, helper -> ImmersivePillagersStats.init(helper::register));
        }
    }
}
