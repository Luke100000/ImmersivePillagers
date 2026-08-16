package net.conczin.immersive_pillagers.client;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.item.ResearchNoteItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ResearchNoteScreen extends Screen {
    private static final ResourceLocation PAPER_TEXTURE = ImmersivePillagers.locate("textures/gui/poster.png");

    private static final Pattern WORD_PATTERN = Pattern.compile("[\\p{L}\\p{N}]+");
    private static final String LATIN_ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final String ILLAGER_ALPHABET = "ᚨᛒᚲᛞᛖᚠᚷᚺᛁᛃᚲᛚᛗᚾᛟᛈᛩᚱᛋᛏᚢᚡᚹᛪᛦᛉ";

    private static final int WIDTH = 150;
    private static final int HEIGHT = 180;

    private final ResearchNoteItem.NoteContents contents;
    private final int translationPercent;
    private int left;
    private int top;
    private List<FormattedCharSequence> translatedLines = List.of();

    public ResearchNoteScreen(ResearchNoteItem.NoteContents contents, int translationPercent) {
        super(localized(contents.title()));
        this.contents = contents;
        this.translationPercent = translationPercent;
    }

    @Override
    protected void init() {
        left = (width - WIDTH) / 2;
        top = (height - HEIGHT) / 2;

        translatedLines = font.split(Component.literal(translate(localized(contents.text()).getString())), WIDTH - 36);

        addRenderableWidget(Button.builder(Component.translatable("gui.immersive_pillagers.back"), button -> onClose()).bounds(left + 38, top + 180, 74, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);

        guiGraphics.blit(PAPER_TEXTURE, left, top, 0, 0, WIDTH, HEIGHT);
        guiGraphics.blit(contents.scribbleImage(), left + 11, top + 25, 0, 0, 128, 128, 128, 128);

        // Title
        Component title = localized(contents.title()).copy().withStyle(ChatFormatting.BOLD);
        guiGraphics.drawString(font, title, left + WIDTH / 2 - font.width(title) / 2, top + 15, 0xFF38291F, false);

        // Text
        int neededSpace = translatedLines.size() * 11;
        int y = top + HEIGHT - neededSpace - 10;
        for (int line = 0; line < translatedLines.size(); line++) {
            guiGraphics.drawString(font, translatedLines.get(line), left + 18, y + line * 11, 0xFF38291F, false);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private String translate(String text) {
        Matcher matcher = WORD_PATTERN.matcher(text);
        StringBuilder translated = new StringBuilder(text.length());
        int cursor = 0;
        while (matcher.find()) {
            translated.append(text, cursor, matcher.start());
            String word = matcher.group();
            translated.append(isKnown(word) ? word : toIllager(word));
            cursor = matcher.end();
        }
        return translated.append(text, cursor, text.length()).toString();
    }

    private boolean isKnown(String word) {
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
