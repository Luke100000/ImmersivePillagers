package net.conczin.immersive_pillagers.neoforge;

import net.conczin.immersive_pillagers.network.ClientboundPacket;
import net.conczin.immersive_pillagers.network.Handler;
import net.conczin.immersive_pillagers.network.ServerboundPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.ArrayList;
import java.util.List;

public final class NetworkingImpl extends Handler.Impl {
    private final List<Entry<?>> entries = new ArrayList<>();

    @Override
    public <T extends ServerboundPacket> void registerServerbound(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        entries.add(new Entry<>(type, codec, (payload, context) -> payload.receive((ServerPlayer) context.player()), null));
    }

    @Override
    public <T extends ClientboundPacket> void registerClientbound(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        entries.add(new Entry<>(type, codec, null, (payload, context) -> payload.receive()));
    }

    @Override
    public void sendToServer(ServerboundPacket packet) {
        PacketDistributor.sendToServer(packet);
    }

    @Override
    public void sendToPlayer(ClientboundPacket packet, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    public void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        entries.forEach(entry -> register(registrar, entry));
    }

    @SuppressWarnings("unchecked")
    private static <T extends CustomPacketPayload> void register(PayloadRegistrar registrar, Entry<T> entry) {
        if (entry.serverHandler != null) {
            registrar.playToServer(entry.type, entry.codec, entry.serverHandler);
        } else {
            registrar.playToClient(entry.type, entry.codec, entry.clientHandler);
        }
    }

    private record Entry<T extends CustomPacketPayload>(CustomPacketPayload.Type<T> type,
                                                         StreamCodec<RegistryFriendlyByteBuf, T> codec,
                                                         IPayloadHandler<T> serverHandler,
                                                         IPayloadHandler<T> clientHandler) {
    }
}
