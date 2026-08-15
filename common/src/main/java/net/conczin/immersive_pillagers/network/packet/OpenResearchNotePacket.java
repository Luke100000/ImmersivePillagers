package net.conczin.immersive_pillagers.network.packet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.conczin.immersive_pillagers.item.ResearchNoteItem;
import net.conczin.immersive_pillagers.network.ClientHandler;
import net.conczin.immersive_pillagers.network.ClientboundPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public final class OpenResearchNotePacket extends ClientboundPacket {
    private static final Codec<ResourceLocation> RESOURCE_LOCATION_CODEC = Codec.STRING.xmap(ResourceLocation::new, ResourceLocation::toString);
    public static final Codec<OpenResearchNotePacket> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RESOURCE_LOCATION_CODEC.fieldOf("scribble_image").forGetter(packet -> packet.contents.scribbleImage()),
            Codec.STRING.fieldOf("title").forGetter(packet -> packet.contents.title()),
            Codec.STRING.fieldOf("text").forGetter(packet -> packet.contents.text())
    ).apply(instance, (image, title, text) -> new OpenResearchNotePacket(new ResearchNoteItem.NoteContents(image, title, text))));

    private final ResearchNoteItem.NoteContents contents;

    public OpenResearchNotePacket(ResearchNoteItem.NoteContents contents) {
        this.contents = contents;
    }

    public OpenResearchNotePacket(FriendlyByteBuf buffer) {
        this(decode(buffer, CODEC));
    }

    private OpenResearchNotePacket(OpenResearchNotePacket packet) {
        this(packet.contents);
    }

    public ResearchNoteItem.NoteContents contents() {
        return contents;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        encode(buffer, CODEC, this);
    }

    @Override
    public void receive() {
        ClientHandler.getInstance().openResearchNote(this);
    }
}
