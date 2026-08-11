package net.conczin.immersive_pillagers.forge;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.network.ClientboundPacket;
import net.conczin.immersive_pillagers.network.Packet;
import net.conczin.immersive_pillagers.network.Handler;
import net.conczin.immersive_pillagers.network.ServerboundPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.Function;

public final class NetworkingImpl extends Handler.Impl {
    private static final String PROTOCOL_VERSION = "1";

    private final SimpleChannel channel = NetworkRegistry.newSimpleChannel(
            ImmersivePillagers.locate("main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    private int id;

    @Override
    public <T extends ServerboundPacket> void registerServerbound(Class<T> packet, Function<FriendlyByteBuf, T> decoder) {
        registerServerbound(packet, decoder, NetworkDirection.PLAY_TO_SERVER);
    }

    @Override
    public <T extends ClientboundPacket> void registerClientbound(Class<T> packet, Function<FriendlyByteBuf, T> decoder) {
        registerClientbound(packet, decoder, NetworkDirection.PLAY_TO_CLIENT);
    }

    private <T extends ServerboundPacket> void registerServerbound(Class<T> packet, Function<FriendlyByteBuf, T> decoder, NetworkDirection direction) {
        channel.registerMessage(id++, packet,
                Packet::encode,
                decoder,
                (decoded, context) -> {
                    context.get().enqueueWork(() -> decoded.receive(context.get().getSender()));
                    context.get().setPacketHandled(true);
                }, Optional.of(direction));
    }

    private <T extends ClientboundPacket> void registerClientbound(Class<T> packet, Function<FriendlyByteBuf, T> decoder, NetworkDirection direction) {
        channel.registerMessage(id++, packet,
                Packet::encode,
                decoder,
                (decoded, context) -> {
                    context.get().enqueueWork(decoded::receive);
                    context.get().setPacketHandled(true);
                }, Optional.of(direction));
    }

    @Override
    public void sendToServer(ServerboundPacket packet) {
        channel.sendToServer(packet);
    }

    @Override
    public void sendToPlayer(ClientboundPacket packet, ServerPlayer player) {
        channel.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}
