package net.conczin.immersive_pillagers.network;

import net.conczin.immersive_pillagers.network.packet.OpenResearchNotePacket;
import net.conczin.immersive_pillagers.network.packet.OpenWantedPosterPacket;

public interface ClientHandler {
    default void openWantedPoster(OpenWantedPosterPacket packet) {
    }

    default void openResearchNote(OpenResearchNotePacket packet) {
    }

    static void setInstance(ClientHandler handler) {
        ClientHandlerInstance.INSTANCE = handler;
    }

    static ClientHandler getInstance() {
        return ClientHandlerInstance.INSTANCE;
    }
}

final class ClientHandlerInstance {
    static ClientHandler INSTANCE = new ClientHandler() {
    };

    private ClientHandlerInstance() {
    }
}
