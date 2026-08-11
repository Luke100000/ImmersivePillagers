package net.conczin.immersive_pillagers.fabric;

import net.conczin.immersive_pillagers.*;
import net.conczin.immersive_pillagers.network.Networking;
import net.conczin.immersive_pillagers.player.PlayerHordeData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Pillager;

public class ImmersivePillagersFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        new NetworkingImpl();

        ImmersivePillagers.init();
        Networking.initialize();
        ImmersivePillagersStats.init();

        ImmersivePillagersItems.register((id, item) -> Registry.register(BuiltInRegistries.ITEM, id, item));
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ImmersivePillagers.locate("main"), ImmersivePillagersItems.CREATIVE_TAB.get());

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ImmersivePillagersCommands.register(dispatcher));
        ServerTickEvents.END_SERVER_TICK.register(PillagerManager::tick);
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, killer, killedEntity) -> {
            if (killer instanceof ServerPlayer player && killedEntity instanceof Pillager) {
                if (PlayerHordeData.get(player).markPillagerKilled()) {
                    player.displayClientMessage(Component.translatable("message.immersive_pillagers.player_wanted"), true);
                }
            }
        });
    }
}
