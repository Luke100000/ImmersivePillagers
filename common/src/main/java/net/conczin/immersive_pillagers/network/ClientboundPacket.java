package net.conczin.immersive_pillagers.network;

public abstract class ClientboundPacket extends Packet {
    public abstract void receive();
}
