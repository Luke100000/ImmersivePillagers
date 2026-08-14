package net.conczin.immersive_pillagers.forge;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.ImmersivePillagersBlockEntities;
import net.conczin.immersive_pillagers.ImmersivePillagersEntities;
import net.conczin.immersive_pillagers.client.*;
import net.conczin.immersive_pillagers.network.ClientHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ImmersivePillagers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ImmersivePillagersForgeClient {
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
