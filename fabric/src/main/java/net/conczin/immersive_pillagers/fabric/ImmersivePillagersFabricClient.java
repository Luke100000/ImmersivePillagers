package net.conczin.immersive_pillagers.fabric;

import net.conczin.immersive_pillagers.client.ClientHandlerImpl;
import net.conczin.immersive_pillagers.network.ClientHandler;
import net.fabricmc.api.ClientModInitializer;

public final class ImmersivePillagersFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientHandler.setInstance(new ClientHandlerImpl());
    }
}
