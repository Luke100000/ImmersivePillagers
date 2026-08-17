package net.conczin.immersive_pillagers.network;

import net.conczin.immersive_pillagers.network.packet.OpenResearchNotePacket;
import net.conczin.immersive_pillagers.network.packet.OpenWantedPosterPacket;
import net.conczin.immersive_pillagers.network.packet.WantedPosterActionPacket;

public final class Networking {
    public static void initialize() {
        Handler.registerClientbound(OpenWantedPosterPacket.TYPE, OpenWantedPosterPacket.STREAM_CODEC);
        Handler.registerClientbound(OpenResearchNotePacket.TYPE, OpenResearchNotePacket.STREAM_CODEC);
        Handler.registerServerbound(WantedPosterActionPacket.TYPE, WantedPosterActionPacket.STREAM_CODEC);
    }
}
