package net.conczin.immersive_pillagers.client;

import net.conczin.immersive_pillagers.network.packet.OpenWantedPosterPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;

final class PlayerListWidget extends ObjectSelectionList<PlayerListWidget.Entry> {
    private final Font font;
    private final Consumer<OpenWantedPosterPacket.Entry> onPlayerSelected;
    private boolean active = true;

    PlayerListWidget(Font font, int left, int top, int width, int bottom,
                     Consumer<OpenWantedPosterPacket.Entry> onPlayerSelected) {
        super(Minecraft.getInstance(), width, bottom - top, top, 20);

        this.font = font;
        this.onPlayerSelected = onPlayerSelected;
        setX(left);
    }

    void setPlayers(List<OpenWantedPosterPacket.Entry> players) {
        replaceEntries(players.stream().map(Entry::new).toList());
        setScrollAmount(0);
    }

    void setActive(boolean active) {
        this.active = active;
    }

    void clearSelection() {
        setSelected(null);
    }

    @Override
    public int getRowWidth() {
        return width - 8;
    }

    @Override
    protected int getScrollbarPosition() {
        return getRowLeft() + getRowWidth() - 4;
    }

    @Override
    protected void renderListBackground(GuiGraphics guiGraphics) {
    }

    @Override
    protected void renderListSeparators(GuiGraphics guiGraphics) {
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return active && super.isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return active && super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    final class Entry extends ObjectSelectionList.Entry<Entry> {
        private final OpenWantedPosterPacket.Entry player;

        private Entry(OpenWantedPosterPacket.Entry player) {
            this.player = player;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight,
                           int mouseX, int mouseY, boolean hovered, float partialTick) {
            if (hovered) {
                guiGraphics.fill(rowLeft, rowTop, rowLeft + rowWidth - 3, rowTop + rowHeight + 2, 0x22000000);
            }
            PlayerInfo info = minecraft.getConnection() == null ? null : minecraft.getConnection().getPlayerInfo(player.id());
            if (info != null) {
                PlayerFaceRenderer.draw(guiGraphics, info.getSkin().texture(), rowLeft + 1, rowTop + 1, 16);
            }
            guiGraphics.drawString(font, player.name(), rowLeft + 20, rowTop + 6, 0xFF3B2B1F, false);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
                onPlayerSelected.accept(player);
                return true;
            }
            return false;
        }

        @Override
        public Component getNarration() {
            return Component.literal(player.name());
        }
    }
}
