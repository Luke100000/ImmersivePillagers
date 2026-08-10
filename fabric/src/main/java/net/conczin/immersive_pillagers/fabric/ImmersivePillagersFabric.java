package net.conczin.immersive_pillagers.fabric;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.ImmersivePillagersCommands;
import net.conczin.immersive_pillagers.PillagerManager;
import net.conczin.immersive_pillagers.player.PlayerHordeData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Pillager;

public class ImmersivePillagersFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ImmersivePillagers.init();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ImmersivePillagersCommands.register(dispatcher));
        ServerTickEvents.END_SERVER_TICK.register(PillagerManager::tick);
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, killer, killedEntity) -> {
            if (killer instanceof ServerPlayer player && killedEntity instanceof Pillager) {
                PlayerHordeData.get(player).markPillagerKilled();
            }
        });
    }
}