package net.conczin.immersive_pillagers.network;

import net.conczin.immersive_pillagers.network.packet.OpenResearchNotePacket;
import net.conczin.immersive_pillagers.network.packet.OpenWantedPosterPacket;
import net.conczin.immersive_pillagers.network.packet.WantedPosterActionPacket;

public final class Networking {
    public static void initialize() {
        Handler.registerClientbound(OpenWantedPosterPacket.class, OpenWantedPosterPacket::new);
        Handler.registerClientbound(OpenResearchNotePacket.class, OpenResearchNotePacket::new);
        Handler.registerServerbound(WantedPosterActionPacket.class, WantedPosterActionPacket::new);
    }
}
