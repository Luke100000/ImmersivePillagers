package net.conczin.immersive_pillagers.network;

import net.minecraft.server.level.ServerPlayer;

public abstract class ServerboundPacket extends Packet {
    public abstract void receive(ServerPlayer player);
}
