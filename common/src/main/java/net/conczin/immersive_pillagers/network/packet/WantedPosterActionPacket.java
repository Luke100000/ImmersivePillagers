package net.conczin.immersive_pillagers.network.packet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.conczin.immersive_pillagers.PillagerManager;
import net.conczin.immersive_pillagers.network.ServerboundPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

import java.util.UUID;

public final class WantedPosterActionPacket extends ServerboundPacket {
    public static final byte BOUNTY = 0;
    public static final byte PARDON = 1;

    private static final Codec<UUID> UUID_CODEC = Codec.STRING.xmap(UUID::fromString, UUID::toString);
    public static final Codec<WantedPosterActionPacket> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BYTE.fieldOf("action").forGetter(WantedPosterActionPacket::action),
            UUID_CODEC.fieldOf("target").forGetter(WantedPosterActionPacket::target),
            Codec.STRING.xmap(InteractionHand::valueOf, InteractionHand::name).fieldOf("hand").forGetter(WantedPosterActionPacket::hand)
    ).apply(instance, WantedPosterActionPacket::new));

    private final byte action;
    private final UUID target;
    private final InteractionHand hand;

    public WantedPosterActionPacket(byte action, UUID target, InteractionHand hand) {
        this.action = action;
        this.target = target;
        this.hand = hand;
    }

    public WantedPosterActionPacket(FriendlyByteBuf buffer) {
        WantedPosterActionPacket packet = decode(buffer, CODEC);
        action = packet.action;
        target = packet.target;
        hand = packet.hand;
    }

    public byte action() {
        return action;
    }

    public UUID target() {
        return target;
    }

    public InteractionHand hand() {
        return hand;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        encode(buffer, CODEC, this);
    }

    @Override
    public void receive(ServerPlayer player) {
        PillagerManager.handleWantedPosterAction(player, this);
    }
}
