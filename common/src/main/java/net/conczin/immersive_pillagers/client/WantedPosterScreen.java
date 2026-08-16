package net.conczin.immersive_pillagers.client;

import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.conczin.immersive_pillagers.network.Handler;
import net.conczin.immersive_pillagers.network.packet.OpenWantedPosterPacket;
import net.conczin.immersive_pillagers.network.packet.WantedPosterActionPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;

import java.util.List;
import java.util.Locale;

public final class WantedPosterScreen extends Screen {
    private static final ResourceLocation POSTER_TEXTURE = ImmersivePillagers.locate("textures/gui/poster.png");
    private static final int WIDTH = 150;
    private static final int HEIGHT = 180;
    private static final int TEXTURE_SIZE = 256;

    private final List<OpenWantedPosterPacket.Entry> players;
    private final InteractionHand hand;
    private OpenWantedPosterPacket.Entry selectedPlayer;
    private int left;
    private int top;
    private EditBox searchBox;
    private PlayerListWidget playerList;
    private Button bountyButton;
    private Button pardonButton;
    private Button backButton;

    public WantedPosterScreen(List<OpenWantedPosterPacket.Entry> players, InteractionHand hand) {
        super(Component.translatable("gui.immersive_pillagers.wanted_poster"));
        this.players = players;
        this.hand = hand;
    }

    @Override
    protected void init() {
        left = (width - WIDTH) / 2;
        top = (height - HEIGHT) / 2;

        searchBox = addRenderableWidget(new EditBox(font, left + 20, top + 18, WIDTH - 40, 12,
                Component.translatable("gui.immersive_pillagers.search")));
        searchBox.setResponder(query -> playerList.setPlayers(filteredPlayers()));

        playerList = new PlayerListWidget(font, left + 14, top + 35, WIDTH - 36,
                top + 142, height, player -> {
            selectedPlayer = player;
            updateView();
        });
        playerList.setPlayers(filteredPlayers());
        addWidget(playerList);

        bountyButton = addRenderableWidget(Button.builder(Component.translatable("gui.immersive_pillagers.add_bounty"), button -> sendAction(WantedPosterActionPacket.BOUNTY))
                .bounds(left + 19, top + 120, 112, 20).build());
        pardonButton = addRenderableWidget(Button.builder(Component.translatable("gui.immersive_pillagers.pardon"), button -> sendAction(WantedPosterActionPacket.PARDON))
                .bounds(left + 19, top + 120, 112, 20).build());
        backButton = addRenderableWidget(Button.builder(Component.translatable("gui.immersive_pillagers.back"), button -> {
                    if (selectedPlayer == null) {
                        onClose();
                    } else {
                        selectedPlayer = null;
                        updateView();
                    }
                })
                .bounds(left + 38, top + 147, 74, 20).build());

        updateView();
    }

    private List<OpenWantedPosterPacket.Entry> filteredPlayers() {
        String query = searchBox.getValue().toLowerCase(Locale.ROOT);
        return players.stream().filter(player -> player.name().toLowerCase(Locale.ROOT).contains(query)).toList();
    }

    private void updateView() {
        boolean showingDetails = selectedPlayer != null;
        searchBox.visible = !showingDetails;
        playerList.setActive(!showingDetails);
        bountyButton.visible = showingDetails && !isSelectedPlayerSelf();
        bountyButton.active = bountyButton.visible && selectedPlayer.bountyAllowed();
        pardonButton.visible = showingDetails && isSelectedPlayerSelf();
        pardonButton.active = pardonButton.visible && selectedPlayer.wanted();
        backButton.visible = true;
    }

    private boolean isSelectedPlayerSelf() {
        if (selectedPlayer == null) return false;
        assert minecraft != null;
        return minecraft.player != null && selectedPlayer.id().equals(minecraft.player.getUUID());
    }

    private void sendAction(byte action) {
        if (selectedPlayer != null) {
            Handler.sendToServer(new WantedPosterActionPacket(action, selectedPlayer.id(), hand));
            onClose();
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        guiGraphics.blit(POSTER_TEXTURE, left, top, 0, 0, WIDTH, HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);
        if (selectedPlayer == null) {
            playerList.render(guiGraphics, mouseX, mouseY, partialTick);
        } else {
            renderPlayerDetails(guiGraphics);
        }
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderPlayerDetails(GuiGraphics guiGraphics) {
        guiGraphics.drawString(font, selectedPlayer.name(), left + WIDTH / 2 - font.width(selectedPlayer.name()) / 2, top + 28, 0xFF3B2B1F, true);

        assert minecraft != null;
        PlayerInfo info = minecraft.getConnection() == null ? null : minecraft.getConnection().getPlayerInfo(selectedPlayer.id());
        if (info != null) {
            guiGraphics.fill(left + 42, top + 43, left + 108, top + 109, 0xDD000000);
            PlayerFaceRenderer.draw(guiGraphics, info.getSkinLocation(), left + 43, top + 44, 64);
        }
    }
}
