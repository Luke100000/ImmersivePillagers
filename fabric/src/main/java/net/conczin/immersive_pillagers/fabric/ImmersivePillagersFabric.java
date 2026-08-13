package net.conczin.immersive_pillagers.fabric;

import net.conczin.immersive_pillagers.*;
import net.conczin.immersive_pillagers.compat.AircraftCompat;
import net.conczin.immersive_pillagers.entity.UndeadEvoker;
import net.conczin.immersive_pillagers.entity.UndeadPillager;
import net.conczin.immersive_pillagers.entity.UndeadVindicator;
import net.conczin.immersive_pillagers.network.Networking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class ImmersivePillagersFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        new NetworkingImpl();

        ImmersivePillagers.init();
        if (FabricLoader.getInstance().isModLoaded("immersive_aircraft")) {
            AircraftCompat.register();
        }
        Networking.initialize();
        ImmersivePillagersStats.init();

        ImmersivePillagersItems.register((id, item) -> Registry.register(BuiltInRegistries.ITEM, id, item));
        ImmersivePillagersEntities.register((id, type) -> Registry.register(BuiltInRegistries.ENTITY_TYPE, id, type));
        ImmersivePillagersSounds.register((id, sound) -> Registry.register(BuiltInRegistries.SOUND_EVENT, id, sound));

        FabricDefaultAttributeRegistry.register(ImmersivePillagersEntities.UNDEAD_PILLAGER.get(), UndeadPillager.createAttributes());
        FabricDefaultAttributeRegistry.register(ImmersivePillagersEntities.UNDEAD_EVOKER.get(), UndeadEvoker.createAttributes());
        FabricDefaultAttributeRegistry.register(ImmersivePillagersEntities.UNDEAD_VINDICATOR.get(), UndeadVindicator.createAttributes());

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ImmersivePillagers.locate("main"), ImmersivePillagersItems.CREATIVE_TAB.get());

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ImmersivePillagersCommands.register(dispatcher));
        ServerTickEvents.END_SERVER_TICK.register(PillagerManager::tick);
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, killer, killedEntity) -> PillagerManager.onPillagerKilled(killedEntity, killer));
    }
}
