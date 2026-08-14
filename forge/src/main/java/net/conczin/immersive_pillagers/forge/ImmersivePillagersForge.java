package net.conczin.immersive_pillagers.forge;

import net.conczin.immersive_pillagers.*;
import net.conczin.immersive_pillagers.compat.AircraftCompat;
import net.conczin.immersive_pillagers.entity.UndeadEvoker;
import net.conczin.immersive_pillagers.entity.UndeadPillager;
import net.conczin.immersive_pillagers.entity.UndeadVindicator;
import net.conczin.immersive_pillagers.network.Networking;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

import static net.minecraft.core.registries.Registries.*;

@Mod(ImmersivePillagers.MOD_ID)
public class ImmersivePillagersForge {
    public ImmersivePillagersForge() {
        new NetworkingImpl();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(ImmersivePillagersForge::register);
        modEventBus.addListener(ImmersivePillagersForge::registerAttributes);
        ImmersivePillagers.init();
        if (ModList.get().isLoaded("immersive_aircraft")) {
            AircraftCompat.register();
        }
        Networking.initialize();
    }

    private static void register(RegisterEvent event) {
        if (event.getRegistryKey().equals(CUSTOM_STAT)) {
            event.register(CUSTOM_STAT, helper -> ImmersivePillagersStats.init(helper::register));
        } else if (event.getRegistryKey().equals(BLOCK)) {
            event.register(BLOCK, helper -> ImmersivePillagersBlocks.register(helper::register));
        } else if (event.getRegistryKey().equals(BLOCK_ENTITY_TYPE)) {
            event.register(BLOCK_ENTITY_TYPE, helper -> ImmersivePillagersBlockEntities.register(
                    helper::register,
                    (factory, blocks) -> BlockEntityType.Builder.of(factory::create, blocks).build(null)
            ));
        } else if (event.getRegistryKey().equals(ITEM)) {
            event.register(ITEM, helper -> ImmersivePillagersItems.register(helper::register));
        } else if (event.getRegistryKey().equals(ENTITY_TYPE)) {
            event.register(ENTITY_TYPE, helper -> ImmersivePillagersEntities.register(helper::register));
        } else if (event.getRegistryKey().equals(SOUND_EVENT)) {
            event.register(SOUND_EVENT, helper -> ImmersivePillagersSounds.register(helper::register));
        } else if (event.getRegistryKey().equals(CREATIVE_MODE_TAB)) {
            event.register(CREATIVE_MODE_TAB, helper -> helper.register(
                    ImmersivePillagers.locate("main"), ImmersivePillagersItems.CREATIVE_TAB.get()
            ));
        }
    }

    private static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ImmersivePillagersEntities.UNDEAD_PILLAGER.get(), UndeadPillager.createAttributes().build());
        event.put(ImmersivePillagersEntities.UNDEAD_EVOKER.get(), UndeadEvoker.createAttributes().build());
        event.put(ImmersivePillagersEntities.UNDEAD_VINDICATOR.get(), UndeadVindicator.createAttributes().build());
    }
}
