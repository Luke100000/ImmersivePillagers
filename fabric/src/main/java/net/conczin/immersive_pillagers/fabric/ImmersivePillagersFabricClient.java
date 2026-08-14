package net.conczin.immersive_pillagers.fabric;

import net.conczin.immersive_pillagers.ImmersivePillagersBlockEntities;
import net.conczin.immersive_pillagers.ImmersivePillagersEntities;
import net.conczin.immersive_pillagers.client.*;
import net.conczin.immersive_pillagers.network.ClientHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public final class ImmersivePillagersFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientHandler.setInstance(new ClientHandlerImpl());

        EntityRendererRegistry.register(ImmersivePillagersEntities.UNDEAD_PILLAGER.get(), UndeadPillagerRenderer::new);
        EntityRendererRegistry.register(ImmersivePillagersEntities.UNDEAD_EVOKER.get(), UndeadEvokerRenderer::new);
        EntityRendererRegistry.register(ImmersivePillagersEntities.UNDEAD_VINDICATOR.get(), UndeadVindicatorRenderer::new);
        BlockEntityRenderers.register(ImmersivePillagersBlockEntities.REINFORCED_CHEST, ReinforcedChestRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(UndeadModelLayers.UNDEAD_ILLAGER, UndeadIllagerModel::createBodyLayer);
    }
}
