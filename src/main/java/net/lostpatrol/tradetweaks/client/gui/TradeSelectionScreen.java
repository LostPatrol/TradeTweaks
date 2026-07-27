package net.lostpatrol.tradetweaks.client.gui;

import net.lostpatrol.tradetweaks.network.NetworkHandler;
import net.lostpatrol.tradetweaks.network.packet.PacketOpenTradeSelection;
import net.lostpatrol.tradetweaks.network.packet.PacketTradeReplace;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.trading.MerchantOffers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;


@OnlyIn(Dist.CLIENT)
public class TradeSelectionScreen extends Screen {
    private static final int PANEL_WIDTH = 160;
    private static final int PANEL_MARGIN = 10;
    private static final int ITEM_HEIGHT = 20;

    protected static final int PANEL_SPACING = 10; // Space between panels
    private static final int CONTAINER_PADDING = 10; // Space around entire container
    private static final int CONTAINER_WIDTH = (PANEL_WIDTH * 2) + PANEL_SPACING;

    private final UUID sessionId;
    private final MerchantOffers offers;
    private final int[] candidatePoolIndices;
    private final List<MerchantOffers> candidatePools;

    TradeListWidget leftPanel;
    private TradeListWidget rightPanel;
    private Button confirmButton;
    private int selectedTradeIndex = -1;
    private int selectedReplacementIndex = -1;

    private final Component LEFT_TITLE = Component.translatable("tradetweaks.gui.existing_trades");
    private final Component RIGHT_TITLE = Component.translatable("tradetweaks.gui.replacement_options").withStyle(ChatFormatting.GREEN);

    @OnlyIn(Dist.CLIENT)
    public TradeSelectionScreen(PacketOpenTradeSelection packet) {
        super(Component.translatable("tradetweaks.gui.trade_selection").withStyle(ChatFormatting.BOLD));
        this.sessionId = packet.getSessionId();
        this.offers = packet.getOffers();
        this.candidatePoolIndices = packet.getCandidatePoolIndices();
        this.candidatePools = packet.getCandidatePools();
    }

    @Override
    protected void init() {
        super.init();

        int containerX = (this.width - CONTAINER_WIDTH) / 2;
        int containerY = 30;
        int panelHeight = this.height - containerY - 50; // Leave space for title and button

        leftPanel = new TradeListWidget(this.minecraft, PANEL_WIDTH, panelHeight, containerY, ITEM_HEIGHT);
        leftPanel.setPosition(containerX, containerY);
        addRenderableWidget(leftPanel);

        rightPanel = new TradeListWidget(this.minecraft, PANEL_WIDTH, panelHeight, containerY, ITEM_HEIGHT);
        rightPanel.setPosition(containerX + PANEL_WIDTH + PANEL_SPACING, containerY);
        addRenderableWidget(rightPanel);

        confirmButton = new Button.Builder(
                Component.translatable("tradetweaks.gui.confirm"),
                button -> confirmReplacement()
        )
                .pos(this.width / 2 - 50, this.height - 30)
                .size(100, 20)
                .build();
        confirmButton.active = false;
        confirmButton.visible = false;
        addRenderableWidget(confirmButton);

        refreshTrades();
    }

    private void refreshTrades() {
        leftPanel.clearTrades();
        for (int i = 0; i < offers.size(); i++) {
            leftPanel.addTrade(offers.get(i), i);
        }
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int containerX = (this.width - CONTAINER_WIDTH) / 2;
        int containerY = 30;

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, LEFT_TITLE, containerX + PANEL_WIDTH / 2, 20, 0xFFFFFF);
        if (selectedTradeIndex >= 0) {
            guiGraphics.drawCenteredString(this.font, RIGHT_TITLE, containerX + PANEL_WIDTH + PANEL_SPACING + PANEL_WIDTH / 2, 20, 0xFFFFFF);
        }

        // Defer tooltip rendering to avoid clipping by scissoring
        if (!leftPanel.getHoveredStack().isEmpty()) {
            guiGraphics.renderTooltip(this.font, leftPanel.getHoveredStack(), mouseX, mouseY);
        } else if (!rightPanel.getHoveredStack().isEmpty()) {
            guiGraphics.renderTooltip(this.font, rightPanel.getHoveredStack(), mouseX, mouseY);
        }

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void selectExistingTrade(int index) {
        if (this.selectedTradeIndex == index)
            return;

        this.selectedTradeIndex = index;
        this.selectedReplacementIndex = -1;
        rightPanel.clearTrades();
        updateConfirmButton();

        if (index >= 0 && index < offers.size() && index < candidatePoolIndices.length) {
            int poolIndex = candidatePoolIndices[index];
            if (poolIndex >= 0 && poolIndex < candidatePools.size()) {
                MerchantOffers possibleTrades = candidatePools.get(poolIndex);
                for (int i = 0; i < possibleTrades.size(); i++) {
                    rightPanel.addTrade(possibleTrades.get(i), i);
                }
            }
        }
    }

    public void selectReplacement(int index) {
        this.selectedReplacementIndex = index;
        updateConfirmButton();
    }

    private void updateConfirmButton() {
        confirmButton.active = (selectedTradeIndex >= 0 && selectedReplacementIndex >= 0);
        confirmButton.visible = (selectedTradeIndex >= 0);
    }

    private void confirmReplacement() {
        if (selectedTradeIndex >= 0 && selectedReplacementIndex >= 0) {
            if (rightPanel.getTrade(selectedReplacementIndex) != null) {
                NetworkHandler.sendTradeReplaceToServer(new PacketTradeReplace(
                        sessionId,
                        selectedTradeIndex,
                        selectedReplacementIndex
                ));
                this.onClose();
            }
        }
    }
}

