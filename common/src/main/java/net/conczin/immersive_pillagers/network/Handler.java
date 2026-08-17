package net.conczin.immersive_pillagers.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public abstract class Handler {
    private static Impl instance;

    public static <T extends ServerboundPacket> void registerServerbound(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        instance.registerServerbound(type, codec);
    }

    public static <T extends ClientboundPacket> void registerClientbound(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        instance.registerClientbound(type, codec);
    }

    public static void sendToServer(ServerboundPacket packet) {
        instance.sendToServer(packet);
    }

    public static void sendToPlayer(ClientboundPacket packet, ServerPlayer player) {
        instance.sendToPlayer(packet, player);
    }

    public abstract static class Impl {
        protected Impl() {
            instance = this;
        }

        public abstract <T extends ServerboundPacket> void registerServerbound(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec);

        public abstract <T extends ClientboundPacket> void registerClientbound(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec);

        public abstract void sendToServer(ServerboundPacket packet);

        public abstract void sendToPlayer(ClientboundPacket packet, ServerPlayer player);
    }
}
