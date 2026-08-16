package net.conczin.immersive_pillagers.item;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.network.Handler;
import net.conczin.immersive_pillagers.network.packet.OpenResearchNotePacket;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Set;

public final class ResearchNoteItem extends Item {
    public static final String SCRIBBLE_IMAGE_TAG = "ScribbleImage";
    public static final String TITLE_TAG = "Title";
    public static final String TEXT_TAG = "Text";
    public static final String RESEARCH_ID_TAG = "ResearchId";

    private static final ResourceLocation RESEARCH_ADVANCEMENT = ImmersivePillagers.locate("research/illager_literacy");
    private static final Set<String> RESEARCH_IDS = Set.of(
            "death", "military", "experimentation", "undeath", "totem"
    );

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
            Handler.sendToPlayer(new OpenResearchNotePacket(contents(stack), getAndCheckTranslationPercentage(serverPlayer, stack)), serverPlayer);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatableWithFallback(contents(stack).title(), contents(stack).title());
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

    private static int getAndCheckTranslationPercentage(ServerPlayer player, ItemStack stack) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return 0;
        }
        Advancement advancement = server.getAdvancements().getAdvancement(RESEARCH_ADVANCEMENT);
        if (advancement == null) {
            return 0;
        }

        String researchId = string(stack.getTag(), RESEARCH_ID_TAG, "");
        if (RESEARCH_IDS.contains(researchId)) {
            player.getAdvancements().award(advancement, researchId);
        }

        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        int discovered = 0;
        for (String criterion : progress.getCompletedCriteria()) {
            if (RESEARCH_IDS.contains(criterion)) {
                discovered++;
            }
        }
        return Math.round(Math.max(0, discovered - 1) * 100.0F / (RESEARCH_IDS.size() - 1));
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
