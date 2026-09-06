package net.conczin.immersive_pillagers.network.packet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.conczin.immersive_pillagers.item.ResearchNoteItem;
import net.conczin.immersive_pillagers.network.ClientHandler;
import net.conczin.immersive_pillagers.network.ClientboundPacket;
import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public final class OpenResearchNotePacket extends ClientboundPacket {
    public static final CustomPacketPayload.Type<OpenResearchNotePacket> TYPE = new CustomPacketPayload.Type<>(ImmersivePillagers.locate("open_research_note"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenResearchNotePacket> STREAM_CODEC = StreamCodec.ofMember(OpenResearchNotePacket::encode, OpenResearchNotePacket::new);
    private static final Codec<Identifier> RESOURCE_LOCATION_CODEC = Codec.STRING.xmap(Identifier::parse, Identifier::toString);
    public static final Codec<OpenResearchNotePacket> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RESOURCE_LOCATION_CODEC.optionalFieldOf("scribble_image").forGetter(packet -> packet.contents.scribbleImage()),
            Codec.STRING.fieldOf("title").forGetter(packet -> packet.contents.title()),
            Codec.STRING.fieldOf("text").forGetter(packet -> packet.contents.text()),
            Codec.INT.fieldOf("translation_percent").forGetter(OpenResearchNotePacket::translationPercent)
    ).apply(instance, (image, title, text, translationPercent) -> new OpenResearchNotePacket(new ResearchNoteItem.NoteContents(image, title, text), translationPercent)));

    private final ResearchNoteItem.NoteContents contents;
    private final int translationPercent;

    public OpenResearchNotePacket(ResearchNoteItem.NoteContents contents, int translationPercent) {
        this.contents = contents;
        this.translationPercent = Math.max(0, Math.min(100, translationPercent));
    }

    public OpenResearchNotePacket(RegistryFriendlyByteBuf buffer) {
        this(decode(buffer, CODEC));
    }

    private OpenResearchNotePacket(OpenResearchNotePacket packet) {
        this(packet.contents, packet.translationPercent);
    }

    public ResearchNoteItem.NoteContents contents() {
        return contents;
    }

    public int translationPercent() {
        return translationPercent;
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buffer) {
        encode(buffer, CODEC, this);
    }

    @Override
    public void receive() {
        ClientHandler.getInstance().openResearchNote(this);
    }

    @Override
    public CustomPacketPayload.Type<OpenResearchNotePacket> type() {
        return TYPE;
    }
}
