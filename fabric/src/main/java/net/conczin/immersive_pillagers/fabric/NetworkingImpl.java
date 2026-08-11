package net.conczin.immersive_pillagers.fabric;

import io.netty.buffer.Unpooled;
import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.network.ClientboundPacket;
import net.conczin.immersive_pillagers.network.Packet;
import net.conczin.immersive_pillagers.network.Handler;
import net.conczin.immersive_pillagers.network.ServerboundPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class NetworkingImpl extends Handler.Impl {
    private final Map<Class<?>, ResourceLocation> identifiers = new HashMap<>();
    private int id;

    private static ResourceLocation createPacketIdentifier(Class<?> packet, int id) {
        return new ResourceLocation(ImmersivePillagers.MOD_ID, packet.getSimpleName().toLowerCase(Locale.ROOT).substring(0, 8) + id);
    }

    private ResourceLocation getPacketIdentifier(Packet packet) {
        return Objects.requireNonNull(identifiers.get(packet.getClass()), "Used unregistered packet!");
    }

    @Override
    public <T extends ServerboundPacket> void registerServerbound(Class<T> packet, Function<FriendlyByteBuf, T> decoder) {
        ResourceLocation identifier = createPacketIdentifier(packet, id++);
        identifiers.put(packet, identifier);

        ServerPlayNetworking.registerGlobalReceiver(identifier, (server, player, handler, buffer, responder) -> {
            ServerboundPacket decoded = decoder.apply(buffer);
            server.execute(() -> decoded.receive(player));
        });
    }

    @Override
    public <T extends ClientboundPacket> void registerClientbound(Class<T> packet, Function<FriendlyByteBuf, T> decoder) {
        ResourceLocation identifier = createPacketIdentifier(packet, id++);
        identifiers.put(packet, identifier);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientProxy.register(identifier, decoder);
        }
    }

    @Override
    public void sendToServer(ServerboundPacket packet) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        packet.encode(buffer);
        ClientPlayNetworking.send(getPacketIdentifier(packet), buffer);
    }

    @Override
    public void sendToPlayer(ClientboundPacket packet, ServerPlayer player) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        packet.encode(buffer);
        ServerPlayNetworking.send(player, getPacketIdentifier(packet), buffer);
    }

    private static final class ClientProxy {
        private ClientProxy() {
        }

        private static <T extends ClientboundPacket> void register(ResourceLocation identifier, Function<FriendlyByteBuf, T> decoder) {
            ClientPlayNetworking.registerGlobalReceiver(identifier, (client, handler, buffer, responder) -> {
                ClientboundPacket decoded = decoder.apply(buffer);
                client.execute(decoded::receive);
            });
        }
    }
}
