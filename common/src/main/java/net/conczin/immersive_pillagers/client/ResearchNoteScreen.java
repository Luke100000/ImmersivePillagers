package net.conczin.immersive_pillagers.client;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.item.ResearchNoteItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Util;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ResearchNoteScreen extends Screen {
    private static final Identifier PAPER_TEXTURE = ImmersivePillagers.locate("textures/gui/poster.png");

    private static final Pattern WORD_PATTERN = Pattern.compile("[\\p{L}\\p{N}]+");
    private static final String LATIN_ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final String ILLAGER_ALPHABET = "ᚨᛒᚲᛞᛖᚠᚷᚺᛁᛃᚲᛚᛗᚾᛟᛈᛩᚱᛋᛏᚢᚡᚹᛪᛦᛉ";

    private static final int WIDTH = 150;
    private static final int HEIGHT = 180;
    private static final long TRANSLATION_FADE_MILLIS = 1000L;
    private static final int MINIMUM_VISIBLE_ALPHA = 4;

    private final ResearchNoteItem.NoteContents contents;
    private final int translationPercent;
    private final long openedAt = Util.getMillis();

    private int left;
    private int top;
    private List<FormattedCharSequence> unreadableLines = List.of();
    private List<FormattedCharSequence> translatedLines = List.of();
    private Button backButton;

    public ResearchNoteScreen(ResearchNoteItem.NoteContents contents, int translationPercent) {
        super(localized(contents.title()));

        this.contents = contents;
        this.translationPercent = translationPercent;
    }

    @Override
    protected void init() {
        left = (width - WIDTH) / 2;
        top = (height - HEIGHT) / 2;
        String text = localized(contents.text()).getString();
        unreadableLines = font.split(Component.literal(translate(text, 0)), WIDTH - 34);
        translatedLines = font.split(Component.literal(translate(text, translationPercent)), WIDTH - 34);

        backButton = addRenderableWidget(Button.builder(Component.translatable("gui.immersive_pillagers.back"), _ -> onClose()).bounds(left + 38, top + 180, 74, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, PAPER_TEXTURE, left, top, 0, 0, WIDTH, HEIGHT, 256, 256);
        contents.scribbleImage().ifPresent(image -> guiGraphics.blit(RenderPipelines.GUI_TEXTURED, image, left + 11, top + 25, 0, 0, 128, 128, 128, 128));

        // Title
        Component title = localized(contents.title()).copy().withStyle(ChatFormatting.BOLD);
        guiGraphics.text(font, title, left + WIDTH / 2 - font.width(title) / 2, top + 15, 0xFF38291F, false);

        float fadeProgress = translationFadeProgress();
        if (fadeProgress < 1.0f) renderText(guiGraphics, unreadableLines, 1.0F - fadeProgress);
        if (fadeProgress > 0.0f) renderText(guiGraphics, translatedLines, fadeProgress);
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderText(GuiGraphicsExtractor guiGraphics, List<FormattedCharSequence> lines, float opacity) {
        int alpha = Math.round(255.0F * opacity);
        if (alpha < MINIMUM_VISIBLE_ALPHA) {
            return;
        }
        int neededSpace = lines.size() * 9;
        int y = contents.scribbleImage().isPresent()
                ? top + HEIGHT - neededSpace - 11
                : top + 35 + (HEIGHT - 45 - neededSpace) / 2;
        int color = (alpha << 24) | 0x38291F;
        for (int line = 0; line < lines.size(); line++) {
            guiGraphics.text(font, lines.get(line), left + 18, y + line * 9, color, false);
        }
    }

    private float translationFadeProgress() {
        return Math.clamp((Util.getMillis() - openedAt) / (float) TRANSLATION_FADE_MILLIS, 0.0f, 1.0f);
    }

    private String translate(String text, int currentTranslationPercent) {
        Matcher matcher = WORD_PATTERN.matcher(text);
        StringBuilder translated = new StringBuilder(text.length());
        int cursor = 0;
        while (matcher.find()) {
            translated.append(text, cursor, matcher.start());
            String word = matcher.group();
            translated.append(isKnown(word, currentTranslationPercent) ? word : toIllager(word));
            cursor = matcher.end();
        }
        return translated.append(text, cursor, text.length()).toString();
    }

    private static boolean isKnown(String word, int translationPercent) {
        return Math.floorMod(word.toLowerCase(Locale.ROOT).hashCode(), 100) < translationPercent;
    }

    private static String toIllager(String word) {
        StringBuilder result = new StringBuilder(word.length());
        for (char character : word.toLowerCase(Locale.ROOT).toCharArray()) {
            int index = LATIN_ALPHABET.indexOf(character);
            result.append(index >= 0 ? ILLAGER_ALPHABET.charAt(index) : character);
        }
        return result.toString();
    }

    private static Component localized(String keyOrText) {
        return Component.translatableWithFallback(keyOrText, keyOrText);
    }
}
