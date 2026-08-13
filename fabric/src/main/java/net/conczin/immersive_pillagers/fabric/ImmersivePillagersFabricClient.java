package net.conczin.immersive_pillagers.fabric;

import net.conczin.immersive_pillagers.ImmersivePillagersEntities;
import net.conczin.immersive_pillagers.client.*;
import net.conczin.immersive_pillagers.network.ClientHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public final class ImmersivePillagersFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientHandler.setInstance(new ClientHandlerImpl());

        EntityRendererRegistry.register(ImmersivePillagersEntities.UNDEAD_PILLAGER.get(), UndeadPillagerRenderer::new);
        EntityRendererRegistry.register(ImmersivePillagersEntities.UNDEAD_EVOKER.get(), UndeadEvokerRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(UndeadModelLayers.UNDEAD_ILLAGER, UndeadIllagerModel::createBodyLayer);
    }
}
