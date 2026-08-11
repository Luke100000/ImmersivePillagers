package net.conczin.immersive_pillagers.client;

import net.conczin.immersive_pillagers.network.ClientHandler;
import net.conczin.immersive_pillagers.network.packet.OpenWantedPosterPacket;
import net.minecraft.client.Minecraft;

public final class ClientHandlerImpl implements ClientHandler {
    @Override
    public void openWantedPoster(OpenWantedPosterPacket packet) {
        Minecraft.getInstance().setScreen(new WantedPosterScreen(packet.players(), packet.hand()));
    }
}
