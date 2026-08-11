package net.conczin.immersive_pillagers.forge;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.ImmersivePillagersItems;
import net.conczin.immersive_pillagers.ImmersivePillagersStats;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

import static net.minecraft.core.registries.Registries.CUSTOM_STAT;
import static net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB;
import static net.minecraft.core.registries.Registries.ITEM;

@Mod(ImmersivePillagers.MOD_ID)
public class ImmersivePillagersForge {
    public ImmersivePillagersForge() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ImmersivePillagersForge::register);
        ImmersivePillagers.init();
    }

    private static void register(RegisterEvent event) {
        if (event.getRegistryKey().equals(CUSTOM_STAT)) {
            event.register(CUSTOM_STAT, helper -> ImmersivePillagersStats.init(helper::register));
        } else if (event.getRegistryKey().equals(ITEM)) {
            event.register(ITEM, helper -> ImmersivePillagersItems.register(helper::register));
        } else if (event.getRegistryKey().equals(CREATIVE_MODE_TAB)) {
            event.register(CREATIVE_MODE_TAB, helper -> helper.register(
                    ImmersivePillagers.locate("main"), ImmersivePillagersItems.CREATIVE_TAB.get()
            ));
        }
    }
}
