package net.lostpatrol.tradetweaks.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.lostpatrol.tradetweaks.network.NetworkHandler;
import net.lostpatrol.tradetweaks.network.packet.PacketCloseTradeSelection;
import net.lostpatrol.tradetweaks.network.packet.PacketOpenTradeSelection;
import net.lostpatrol.tradetweaks.network.packet.PacketTradeReplace;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class TradeSelectionScreen extends Screen {
    private static final ResourceLocation VILLAGER_LOCATION =
            ResourceLocation.withDefaultNamespace("textures/gui/container/villager.png");
    private static final ResourceLocation SCROLLER_SPRITE =
            ResourceLocation.withDefaultNamespace("container/villager/scroller");
    private static final ResourceLocation SCROLLER_DISABLED_SPRITE =
            ResourceLocation.withDefaultNamespace("container/villager/scroller_disabled");
    private static final ResourceLocation TRADE_ARROW_OUT_OF_STOCK_SPRITE =
            ResourceLocation.withDefaultNamespace("container/villager/trade_arrow_out_of_stock");
    private static final ResourceLocation TRADE_ARROW_SPRITE =
            ResourceLocation.withDefaultNamespace("container/villager/trade_arrow");
    private static final ResourceLocation DISCOUNT_STRIKETHROUGH_SPRITE =
            ResourceLocation.withDefaultNamespace("container/villager/discount_strikethrough");
    private static final double MAX_INTERACTION_DISTANCE_SQUARED = 64.0D;
    private static final int PANEL_WIDTH = 103;
    private static final int PANEL_HEIGHT = 166;
    private static final int PANEL_GAP = 4;
    private static final int PANEL_TEXTURE_WIDTH = 512;
    private static final int PANEL_TEXTURE_HEIGHT = 256;
    private static final int OFFER_COUNT = 7;
    private static final int OFFER_BUTTON_X = 5;
    private static final int OFFER_BUTTON_Y = 18;
    private static final int OFFER_BUTTON_WIDTH = 88;
    private static final int OFFER_BUTTON_HEIGHT = 20;
    private static final int SCROLLER_X = 94;
    private static final int SCROLLER_Y = 18;
    private static final int SCROLLER_WIDTH = 6;
    private static final int SCROLLER_HEIGHT = 27;
    private static final int SCROLL_BAR_HEIGHT = 139;
    private static final int SCREEN_HEIGHT = PANEL_HEIGHT + 25;
    private static final Component LEFT_TITLE = Component.translatable("tradetweaks.gui.existing_trades");
    private static final Component RIGHT_TITLE = Component.translatable("tradetweaks.gui.replacement_options");

    private final UUID sessionId;
    private final int villagerId;
    private final MerchantOffers offers;
    private final int[] candidatePoolIndices;
    private final List<MerchantOffers> candidatePools;
    private final TradeOfferButton[] existingTradeButtons = new TradeOfferButton[OFFER_COUNT];
    private final TradeOfferButton[] replacementTradeButtons = new TradeOfferButton[OFFER_COUNT];

    private MerchantOffers replacementOffers = new MerchantOffers();
    private Button confirmButton;
    private int leftPos;
    private int rightPos;
    private int topPos;
    private int existingScrollOff;
    private int replacementScrollOff;
    private int selectedTradeIndex = -1;
    private int selectedReplacementIndex = -1;
    private boolean draggingExistingScroller;
    private boolean draggingReplacementScroller;
    private boolean replacementSubmitted;
    private boolean closePacketSent;

    public TradeSelectionScreen(PacketOpenTradeSelection packet) {
        super(Component.translatable("tradetweaks.gui.trade_selection"));
        this.sessionId = packet.getSessionId();
        this.villagerId = packet.getVillagerId();
        this.offers = packet.getOffers();
        this.candidatePoolIndices = packet.getCandidatePoolIndices();
        this.candidatePools = packet.getCandidatePools();
    }

    @Override
    protected void init() {
        super.init();
        int screenWidth = PANEL_WIDTH * 2 + PANEL_GAP;
        this.leftPos = (this.width - screenWidth) / 2;
        this.rightPos = this.leftPos + PANEL_WIDTH + PANEL_GAP;
        this.topPos = (this.height - SCREEN_HEIGHT) / 2;

        for (int row = 0; row < OFFER_COUNT; row++) {
            int buttonY = this.topPos + OFFER_BUTTON_Y + row * OFFER_BUTTON_HEIGHT;
            this.existingTradeButtons[row] = this.addRenderableWidget(
                    new TradeOfferButton(this.leftPos + OFFER_BUTTON_X, buttonY, row, false)
            );
            this.replacementTradeButtons[row] = this.addRenderableWidget(
                    new TradeOfferButton(this.rightPos + OFFER_BUTTON_X, buttonY, row, true)
            );
        }

        this.confirmButton = this.addRenderableWidget(
                Button.builder(Component.translatable("tradetweaks.gui.confirm"), button -> confirmReplacement())
                        .bounds(this.width / 2 - 50, this.topPos + PANEL_HEIGHT + 5, 100, 20)
                        .build()
        );
        updateButtonStates();
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        renderPanelBackground(guiGraphics, this.leftPos);
        renderPanelBackground(guiGraphics, this.rightPos);
        updateButtonStates();
        for (Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        renderOffers(guiGraphics, this.offers, this.leftPos, this.existingScrollOff);
        renderOffers(guiGraphics, this.replacementOffers, this.rightPos, this.replacementScrollOff);
        renderScroller(guiGraphics, this.leftPos, this.offers, this.existingScrollOff);
        renderScroller(guiGraphics, this.rightPos, this.replacementOffers, this.replacementScrollOff);
        renderPanelTitle(guiGraphics, LEFT_TITLE, this.leftPos);
        renderPanelTitle(guiGraphics, RIGHT_TITLE, this.rightPos);
        renderButtonTooltips(guiGraphics, this.existingTradeButtons, mouseX, mouseY);
        renderButtonTooltips(guiGraphics, this.replacementTradeButtons, mouseX, mouseY);
        RenderSystem.enableDepthTest();
    }

    private void renderPanelBackground(GuiGraphics guiGraphics, int panelX) {
        guiGraphics.blit(
                VILLAGER_LOCATION,
                panelX,
                this.topPos,
                0,
                0.0F,
                0.0F,
                PANEL_WIDTH,
                PANEL_HEIGHT,
                PANEL_TEXTURE_WIDTH,
                PANEL_TEXTURE_HEIGHT
        );
    }

    private void renderPanelTitle(GuiGraphics guiGraphics, Component title, int panelX) {
        guiGraphics.drawString(this.font, title, panelX + 53 - this.font.width(title) / 2, this.topPos + 6, 4210752, false);
    }

    private void renderOffers(GuiGraphics guiGraphics, MerchantOffers panelOffers, int panelX, int scrollOff) {
        int end = Math.min(panelOffers.size(), scrollOff + OFFER_COUNT);
        for (int offerIndex = scrollOff; offerIndex < end; offerIndex++) {
            MerchantOffer offer = panelOffers.get(offerIndex);
            int row = offerIndex - scrollOff;
            int itemY = this.topPos + 19 + row * OFFER_BUTTON_HEIGHT;
            ItemStack baseCost = offer.getBaseCostA();
            ItemStack costA = offer.getCostA();
            ItemStack costB = offer.getCostB();
            ItemStack result = offer.getResult();

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
            renderAndDecorateCostA(guiGraphics, costA, baseCost, panelX + 10, itemY);
            if (!costB.isEmpty()) {
                guiGraphics.renderFakeItem(costB, panelX + 40, itemY);
                guiGraphics.renderItemDecorations(this.font, costB, panelX + 40, itemY);
            }
            renderTradeArrow(guiGraphics, offer, panelX, itemY);
            guiGraphics.renderFakeItem(result, panelX + 73, itemY);
            guiGraphics.renderItemDecorations(this.font, result, panelX + 73, itemY);
            guiGraphics.pose().popPose();
        }
    }

    private void renderTradeArrow(GuiGraphics guiGraphics, MerchantOffer offer, int panelX, int itemY) {
        RenderSystem.enableBlend();
        ResourceLocation sprite = offer.isOutOfStock()
                ? TRADE_ARROW_OUT_OF_STOCK_SPRITE
                : TRADE_ARROW_SPRITE;
        guiGraphics.blitSprite(sprite, panelX + 60, itemY + 3, 0, 10, 9);
    }

    private void renderAndDecorateCostA(
            GuiGraphics guiGraphics,
            ItemStack realCost,
            ItemStack baseCost,
            int x,
            int y
    ) {
        guiGraphics.renderFakeItem(realCost, x, y);
        if (baseCost.getCount() == realCost.getCount()) {
            guiGraphics.renderItemDecorations(this.font, realCost, x, y);
            return;
        }

        guiGraphics.renderItemDecorations(this.font, baseCost, x, y, baseCost.getCount() == 1 ? "1" : null);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
        String count = realCost.getCount() == 1 ? "1" : String.valueOf(realCost.getCount());
        this.font.drawInBatch(
                count,
                (float) (x + 14) + 17 - this.font.width(count),
                (float) y + 9,
                0xFFFFFF,
                true,
                guiGraphics.pose().last().pose(),
                guiGraphics.bufferSource(),
                net.minecraft.client.gui.Font.DisplayMode.NORMAL,
                0,
                15728880,
                false
        );
        guiGraphics.pose().popPose();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 300.0F);
        guiGraphics.blitSprite(DISCOUNT_STRIKETHROUGH_SPRITE, x + 7, y + 12, 0, 9, 2);
        guiGraphics.pose().popPose();
    }

    private void renderScroller(GuiGraphics guiGraphics, int panelX, MerchantOffers panelOffers, int scrollOff) {
        int scrollRange = panelOffers.size() + 1 - OFFER_COUNT;
        if (scrollRange > 1) {
            int remainder = SCROLL_BAR_HEIGHT
                    - (SCROLLER_HEIGHT + (scrollRange - 1) * SCROLL_BAR_HEIGHT / scrollRange);
            int step = 1 + remainder / scrollRange + SCROLL_BAR_HEIGHT / scrollRange;
            int scrollY = Math.min(113, scrollOff * step);
            if (scrollOff == scrollRange - 1) {
                scrollY = 113;
            }
            guiGraphics.blitSprite(
                    SCROLLER_SPRITE,
                    panelX + SCROLLER_X,
                    this.topPos + SCROLLER_Y + scrollY,
                    0,
                    SCROLLER_WIDTH,
                    SCROLLER_HEIGHT
            );
        } else {
            guiGraphics.blitSprite(
                    SCROLLER_DISABLED_SPRITE,
                    panelX + SCROLLER_X,
                    this.topPos + SCROLLER_Y,
                    0,
                    SCROLLER_WIDTH,
                    SCROLLER_HEIGHT
            );
        }
    }

    private void renderButtonTooltips(
            GuiGraphics guiGraphics,
            TradeOfferButton[] buttons,
            int mouseX,
            int mouseY
    ) {
        for (TradeOfferButton button : buttons) {
            if (button != null && button.visible) {
                button.renderTradeTooltip(guiGraphics, mouseX, mouseY);
            }
        }
    }

    private void updateButtonStates() {
        updatePanelButtons(this.existingTradeButtons, this.offers, this.existingScrollOff, this.selectedTradeIndex);
        updatePanelButtons(
                this.replacementTradeButtons,
                this.replacementOffers,
                this.replacementScrollOff,
                this.selectedReplacementIndex
        );
        if (this.confirmButton != null) {
            this.confirmButton.active = this.selectedTradeIndex >= 0 && this.selectedReplacementIndex >= 0;
        }
    }

    private void updatePanelButtons(
            TradeOfferButton[] buttons,
            MerchantOffers panelOffers,
            int scrollOff,
            int selectedIndex
    ) {
        for (int row = 0; row < buttons.length; row++) {
            TradeOfferButton button = buttons[row];
            if (button == null) {
                continue;
            }
            int offerIndex = row + scrollOff;
            button.visible = offerIndex < panelOffers.size();
            button.active = button.visible;
            button.setFocused(button.visible && offerIndex == selectedIndex);
        }
    }

    private void selectExistingTrade(int index) {
        if (index == this.selectedTradeIndex) {
            return;
        }
        this.selectedTradeIndex = index;
        this.selectedReplacementIndex = -1;
        this.replacementScrollOff = 0;
        this.replacementOffers = getReplacementOffers(index);
        updateButtonStates();
    }

    private MerchantOffers getReplacementOffers(int index) {
        if (index < 0 || index >= this.offers.size() || index >= this.candidatePoolIndices.length) {
            return new MerchantOffers();
        }
        int poolIndex = this.candidatePoolIndices[index];
        if (poolIndex < 0 || poolIndex >= this.candidatePools.size()) {
            return new MerchantOffers();
        }
        return this.candidatePools.get(poolIndex);
    }

    private void selectReplacement(int index) {
        if (index >= 0 && index < this.replacementOffers.size()) {
            this.selectedReplacementIndex = index;
            updateButtonStates();
        }
    }

    private void confirmReplacement() {
        if (this.selectedTradeIndex < 0
                || this.selectedReplacementIndex < 0
                || this.selectedReplacementIndex >= this.replacementOffers.size()) {
            return;
        }
        this.replacementSubmitted = true;
        NetworkHandler.sendTradeReplaceToServer(
                new PacketTradeReplace(this.sessionId, this.selectedTradeIndex, this.selectedReplacementIndex)
        );
        this.onClose();
    }

    @Override
    public void onClose() {
        sendClosePacket();
        super.onClose();
    }

    @Override
    public void removed() {
        sendClosePacket();
        super.removed();
    }

    private void sendClosePacket() {
        if (this.replacementSubmitted || this.closePacketSent) {
            return;
        }
        this.closePacketSent = true;
        NetworkHandler.sendCloseTradeSelectionToServer(new PacketCloseTradeSelection(this.sessionId));
    }

    private boolean canScroll(MerchantOffers panelOffers) {
        return panelOffers.size() > OFFER_COUNT;
    }

    private boolean isInsidePanel(double mouseX, double mouseY, int panelX) {
        return mouseX >= panelX
                && mouseX < panelX + PANEL_WIDTH
                && mouseY >= this.topPos
                && mouseY < this.topPos + PANEL_HEIGHT;
    }

    private boolean isInsideScroller(double mouseX, double mouseY, int panelX, MerchantOffers panelOffers) {
        return canScroll(panelOffers)
                && mouseX > panelX + SCROLLER_X
                && mouseX < panelX + SCROLLER_X + SCROLLER_WIDTH
                && mouseY > this.topPos + SCROLLER_Y
                && mouseY <= this.topPos + SCROLLER_Y + SCROLL_BAR_HEIGHT + 1;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isInsidePanel(mouseX, mouseY, this.leftPos)) {
            this.existingScrollOff = scroll(this.offers, this.existingScrollOff, scrollY);
            return true;
        }
        if (isInsidePanel(mouseX, mouseY, this.rightPos)) {
            this.replacementScrollOff = scroll(this.replacementOffers, this.replacementScrollOff, scrollY);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private int scroll(MerchantOffers panelOffers, int currentOffset, double delta) {
        if (!canScroll(panelOffers)) {
            return 0;
        }
        return Mth.clamp((int) (currentOffset - delta), 0, panelOffers.size() - OFFER_COUNT);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.draggingExistingScroller = false;
        this.draggingReplacementScroller = false;
        if (isInsideScroller(mouseX, mouseY, this.leftPos, this.offers)) {
            this.draggingExistingScroller = true;
            return true;
        }
        if (isInsideScroller(mouseX, mouseY, this.rightPos, this.replacementOffers)) {
            this.draggingReplacementScroller = true;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.draggingExistingScroller) {
            this.existingScrollOff = dragScroller(mouseY, this.offers);
            return true;
        }
        if (this.draggingReplacementScroller) {
            this.replacementScrollOff = dragScroller(mouseY, this.replacementOffers);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    private int dragScroller(double mouseY, MerchantOffers panelOffers) {
        int maxOffset = panelOffers.size() - OFFER_COUNT;
        float progress = ((float) mouseY - (this.topPos + SCROLLER_Y) - 13.5F)
                / (SCROLL_BAR_HEIGHT - SCROLLER_HEIGHT);
        return Mth.clamp((int) (progress * maxOffset + 0.5F), 0, maxOffset);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.draggingExistingScroller = false;
        this.draggingReplacementScroller = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.minecraft == null) {
            return;
        }
        if (this.minecraft.level == null || this.minecraft.player == null) {
            this.onClose();
            return;
        }

        Entity entity = this.minecraft.level.getEntity(this.villagerId);
        if (!(entity instanceof Villager villager)
                || !villager.isAlive()
                || this.minecraft.player.distanceToSqr(villager) > MAX_INTERACTION_DISTANCE_SQUARED) {
            this.onClose();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private class TradeOfferButton extends Button {
        private final int row;
        private final boolean replacementPanel;

        private TradeOfferButton(int x, int y, int row, boolean replacementPanel) {
            super(
                    x,
                    y,
                    OFFER_BUTTON_WIDTH,
                    OFFER_BUTTON_HEIGHT,
                    CommonComponents.EMPTY,
                    button -> {
                        TradeOfferButton tradeButton = (TradeOfferButton) button;
                        int offerIndex = tradeButton.row + tradeButton.getScrollOff();
                        if (tradeButton.replacementPanel) {
                            selectReplacement(offerIndex);
                        } else {
                            selectExistingTrade(offerIndex);
                        }
                    },
                    DEFAULT_NARRATION
            );
            this.row = row;
            this.replacementPanel = replacementPanel;
            this.visible = false;
        }

        private MerchantOffers getPanelOffers() {
            return this.replacementPanel ? replacementOffers : offers;
        }

        private int getScrollOff() {
            return this.replacementPanel ? replacementScrollOff : existingScrollOff;
        }

        private MerchantOffer getOffer() {
            int offerIndex = this.row + getScrollOff();
            MerchantOffers panelOffers = getPanelOffers();
            return offerIndex >= 0 && offerIndex < panelOffers.size() ? panelOffers.get(offerIndex) : null;
        }

        @Override
        protected MutableComponent createNarrationMessage() {
            MerchantOffer offer = getOffer();
            if (offer == null) {
                return Component.empty();
            }
            return Component.translatable(
                    "tradetweaks.gui.narration.trade_entry",
                    offer.getCostA().getHoverName(),
                    offer.getResult().getHoverName()
            );
        }

        private void renderTradeTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            if (!this.isHovered) {
                return;
            }
            MerchantOffer offer = getOffer();
            if (offer == null) {
                return;
            }
            if (mouseX < this.getX() + 20) {
                guiGraphics.renderTooltip(TradeSelectionScreen.this.font, offer.getCostA(), mouseX, mouseY);
            } else if (mouseX > this.getX() + 30 && mouseX < this.getX() + 50 && !offer.getCostB().isEmpty()) {
                guiGraphics.renderTooltip(TradeSelectionScreen.this.font, offer.getCostB(), mouseX, mouseY);
            } else if (mouseX > this.getX() + 65) {
                guiGraphics.renderTooltip(TradeSelectionScreen.this.font, offer.getResult(), mouseX, mouseY);
            }
        }
    }
}
