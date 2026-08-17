package net.conczin.immersive_pillagers.network;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;

import java.util.Objects;

public abstract class Packet implements CustomPacketPayload {
    public abstract void encode(RegistryFriendlyByteBuf buffer);

    protected static <T> T decode(RegistryFriendlyByteBuf buffer, Codec<T> codec) {
        CompoundTag tag = Objects.requireNonNull(buffer.readNbt(), "Missing payload");
        return codec.parse(NbtOps.INSTANCE, tag.get("payload"))
                .resultOrPartial(error -> {
                    throw new IllegalArgumentException("Invalid payload: " + error);
                })
                .orElseThrow();
    }

    protected static <T> void encode(RegistryFriendlyByteBuf buffer, Codec<T> codec, T packet) {
        CompoundTag tag = new CompoundTag();
        tag.put("payload", codec.encodeStart(NbtOps.INSTANCE, packet)
                .resultOrPartial(error -> {
                    throw new IllegalStateException("Could not encode payload: " + error);
                })
                .orElseThrow());
        buffer.writeNbt(tag);
    }
}
