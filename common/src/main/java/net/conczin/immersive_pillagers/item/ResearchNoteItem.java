package net.conczin.immersive_pillagers.item;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.network.Handler;
import net.conczin.immersive_pillagers.network.packet.OpenResearchNotePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class ResearchNoteItem extends Item {
    public static final String SCRIBBLE_IMAGE_TAG = "ScribbleImage";
    public static final String TITLE_TAG = "Title";
    public static final String TEXT_TAG = "Text";

    private static final ResourceLocation DEFAULT_SCRIBBLE_IMAGE = ImmersivePillagers.locate("textures/gui/scribbles/default.png");
    private static final String DEFAULT_TITLE = "Illager Research Note";
    private static final String DEFAULT_TEXT = "The ink has faded beyond recognition.";

    public ResearchNoteItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            Handler.sendToPlayer(new OpenResearchNotePacket(contents(stack)), serverPlayer);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal(contents(stack).title());
    }

    public static NoteContents contents(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        String imagePath = string(tag, SCRIBBLE_IMAGE_TAG, DEFAULT_SCRIBBLE_IMAGE.toString());
        ResourceLocation image = ResourceLocation.tryParse(imagePath);
        return new NoteContents(
                image == null ? DEFAULT_SCRIBBLE_IMAGE : image,
                string(tag, TITLE_TAG, DEFAULT_TITLE),
                string(tag, TEXT_TAG, DEFAULT_TEXT)
        );
    }

    private static String string(CompoundTag tag, String key, String fallback) {
        if (tag == null || !tag.contains(key, Tag.TAG_STRING)) {
            return fallback;
        }
        String value = tag.getString(key);
        return value.isBlank() ? fallback : value;
    }

    public record NoteContents(ResourceLocation scribbleImage, String title, String text) {
    }
}
