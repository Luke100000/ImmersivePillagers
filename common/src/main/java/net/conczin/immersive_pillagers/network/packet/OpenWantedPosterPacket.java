package net.conczin.immersive_pillagers.network.packet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.conczin.immersive_pillagers.network.ClientHandler;
import net.conczin.immersive_pillagers.network.ClientboundPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;

import java.util.List;
import java.util.UUID;

public final class OpenWantedPosterPacket extends ClientboundPacket {
    private static final Codec<UUID> UUID_CODEC = Codec.STRING.xmap(UUID::fromString, UUID::toString);
    public static final Codec<Entry> ENTRY_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUID_CODEC.fieldOf("id").forGetter(Entry::id),
            Codec.STRING.fieldOf("name").forGetter(Entry::name),
            Codec.BOOL.fieldOf("wanted").forGetter(Entry::wanted),
            Codec.BOOL.fieldOf("bounty_allowed").forGetter(Entry::bountyAllowed)
    ).apply(instance, Entry::new));
    private static final Codec<InteractionHand> HAND_CODEC = Codec.STRING.xmap(InteractionHand::valueOf, InteractionHand::name);
    public static final Codec<OpenWantedPosterPacket> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ENTRY_CODEC.listOf().fieldOf("players").forGetter(OpenWantedPosterPacket::players),
            HAND_CODEC.fieldOf("hand").forGetter(OpenWantedPosterPacket::hand)
    ).apply(instance, OpenWantedPosterPacket::new));

    private final List<Entry> players;
    private final InteractionHand hand;

    public OpenWantedPosterPacket(List<Entry> players, InteractionHand hand) {
        this.players = List.copyOf(players);
        this.hand = hand;
    }

    public OpenWantedPosterPacket(FriendlyByteBuf buffer) {
        this(decode(buffer, CODEC));
    }

    private OpenWantedPosterPacket(OpenWantedPosterPacket packet) {
        this(packet.players, packet.hand);
    }

    public List<Entry> players() {
        return players;
    }

    public InteractionHand hand() {
        return hand;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        encode(buffer, CODEC, this);
    }

    @Override
    public void receive() {
        ClientHandler.getInstance().openWantedPoster(this);
    }

    public record Entry(UUID id, String name, boolean wanted, boolean bountyAllowed) {
    }
}
