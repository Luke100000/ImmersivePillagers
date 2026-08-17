package net.conczin.immersive_pillagers.neoforge;

import net.conczin.immersive_pillagers.*;
import net.conczin.immersive_pillagers.compat.AircraftCompat;
import net.conczin.immersive_pillagers.entity.UndeadEvoker;
import net.conczin.immersive_pillagers.entity.UndeadPillager;
import net.conczin.immersive_pillagers.entity.UndeadVindicator;
import net.conczin.immersive_pillagers.network.Networking;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import static net.minecraft.core.registries.Registries.*;

@Mod(ImmersivePillagers.MOD_ID)
public final class ImmersivePillagersNeoForge {
    private static final NetworkingImpl NETWORKING = new NetworkingImpl();

    public ImmersivePillagersNeoForge(IEventBus modEventBus) {
        modEventBus.addListener(ImmersivePillagersNeoForge::register);
        modEventBus.addListener(ImmersivePillagersNeoForge::registerAttributes);
        modEventBus.addListener(ImmersivePillagersNeoForge::buildCreativeTabContents);
        modEventBus.addListener(ImmersivePillagersNeoForge::registerPayloadHandlers);
        ImmersivePillagers.init();
        if (ModList.get().isLoaded("immersive_aircraft")) {
            AircraftCompat.register();
        }
        Networking.initialize();
    }

    private static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        NETWORKING.register(event);
    }

    private static void register(RegisterEvent event) {
        if (event.getRegistryKey().equals(CUSTOM_STAT)) {
            event.register(CUSTOM_STAT, helper -> ImmersivePillagersStats.init(helper::register));
        } else if (event.getRegistryKey().equals(BLOCK)) {
            event.register(BLOCK, helper -> ImmersivePillagersBlocks.register(helper::register));
        } else if (event.getRegistryKey().equals(BLOCK_ENTITY_TYPE)) {
            event.register(BLOCK_ENTITY_TYPE, helper -> ImmersivePillagersBlockEntities.register(
                    helper::register,
                    (factory, blocks) -> new BlockEntityType<>(factory::create, blocks)
            ));
        } else if (event.getRegistryKey().equals(ITEM)) {
            event.register(ITEM, helper -> ImmersivePillagersItems.register(helper::register));
        } else if (event.getRegistryKey().equals(ENTITY_TYPE)) {
            event.register(ENTITY_TYPE, helper -> ImmersivePillagersEntities.register(helper::register));
        } else if (event.getRegistryKey().equals(SOUND_EVENT)) {
            event.register(SOUND_EVENT, helper -> ImmersivePillagersSounds.register(helper::register));
        } else if (event.getRegistryKey().equals(CREATIVE_MODE_TAB)) {
            event.register(CREATIVE_MODE_TAB, helper -> helper.register(
                    ImmersivePillagersItems.CREATIVE_TAB_KEY, ImmersivePillagersItems.CREATIVE_TAB.get()
            ));
        }
    }

    private static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ImmersivePillagersEntities.UNDEAD_PILLAGER.get(), UndeadPillager.createAttributes().build());
        event.put(ImmersivePillagersEntities.UNDEAD_EVOKER.get(), UndeadEvoker.createAttributes().build());
        event.put(ImmersivePillagersEntities.UNDEAD_VINDICATOR.get(), UndeadVindicator.createAttributes().build());
    }

    private static void buildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(ImmersivePillagersItems.CREATIVE_TAB_KEY)) {
            ImmersivePillagersItems.addCreativeTabItems(event::accept);
        }
    }
}
