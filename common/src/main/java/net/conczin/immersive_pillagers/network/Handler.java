package net.conczin.immersive_pillagers.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Function;

public abstract class Handler {
    private static Impl instance;

    public static <T extends ServerboundPacket> void registerServerbound(Class<T> packet, Function<FriendlyByteBuf, T> decoder) {
        instance.registerServerbound(packet, decoder);
    }

    public static <T extends ClientboundPacket> void registerClientbound(Class<T> packet, Function<FriendlyByteBuf, T> decoder) {
        instance.registerClientbound(packet, decoder);
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

        public abstract <T extends ServerboundPacket> void registerServerbound(Class<T> packet, Function<FriendlyByteBuf, T> decoder);

        public abstract <T extends ClientboundPacket> void registerClientbound(Class<T> packet, Function<FriendlyByteBuf, T> decoder);

        public abstract void sendToServer(ServerboundPacket packet);

        public abstract void sendToPlayer(ClientboundPacket packet, ServerPlayer player);
    }
}
