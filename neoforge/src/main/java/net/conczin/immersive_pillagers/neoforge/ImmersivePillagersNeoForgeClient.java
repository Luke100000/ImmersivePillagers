package net.conczin.immersive_pillagers.neoforge;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.ImmersivePillagersBlockEntities;
import net.conczin.immersive_pillagers.ImmersivePillagersEntities;
import net.conczin.immersive_pillagers.client.*;
import net.conczin.immersive_pillagers.network.ClientHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = ImmersivePillagers.MOD_ID, value = Dist.CLIENT)
public final class ImmersivePillagersNeoForgeClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ClientHandler.setInstance(new ClientHandlerImpl());
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ImmersivePillagersEntities.UNDEAD_PILLAGER.get(), UndeadPillagerRenderer::new);
        event.registerEntityRenderer(ImmersivePillagersEntities.UNDEAD_EVOKER.get(), UndeadEvokerRenderer::new);
        event.registerEntityRenderer(ImmersivePillagersEntities.UNDEAD_VINDICATOR.get(), UndeadVindicatorRenderer::new);
        event.registerBlockEntityRenderer(ImmersivePillagersBlockEntities.REINFORCED_CHEST, ReinforcedChestRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(UndeadModelLayers.UNDEAD_ILLAGER, UndeadIllagerModel::createBodyLayer);
    }
}
