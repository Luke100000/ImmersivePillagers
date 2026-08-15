package net.conczin.immersive_pillagers.client;

import net.conczin.immersive_pillagers.network.ClientHandler;
import net.conczin.immersive_pillagers.network.packet.OpenResearchNotePacket;
import net.conczin.immersive_pillagers.network.packet.OpenWantedPosterPacket;
import net.minecraft.client.Minecraft;

public final class ClientHandlerImpl implements ClientHandler {
    @Override
    public void openWantedPoster(OpenWantedPosterPacket packet) {
        Minecraft.getInstance().setScreen(new WantedPosterScreen(packet.players(), packet.hand()));
    }

    @Override
    public void openResearchNote(OpenResearchNotePacket packet) {
        Minecraft.getInstance().setScreen(new ResearchNoteScreen(packet.contents(), packet.translationPercent()));
    }
}
