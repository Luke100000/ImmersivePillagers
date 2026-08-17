package net.conczin.immersive_pillagers.fabric;

import net.conczin.immersive_pillagers.network.ClientboundPacket;
import net.conczin.immersive_pillagers.network.Handler;
import net.conczin.immersive_pillagers.network.ServerboundPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public final class NetworkingImpl extends Handler.Impl {
    @Override
    public <T extends ServerboundPacket> void registerServerbound(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.playC2S().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> payload.receive(context.player()));
    }

    @Override
    public <T extends ClientboundPacket> void registerClientbound(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.playS2C().register(type, codec);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientProxy.register(type);
        }
    }

    @Override
    public void sendToServer(ServerboundPacket packet) {
        ClientPlayNetworking.send(packet);
    }

    @Override
    public void sendToPlayer(ClientboundPacket packet, ServerPlayer player) {
        ServerPlayNetworking.send(player, packet);
    }

    private static final class ClientProxy {
        private ClientProxy() {
        }

        private static <T extends ClientboundPacket> void register(CustomPacketPayload.Type<T> type) {
            ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> payload.receive());
        }
    }
}
