package net.lostpatrol.tradetweaks.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;


// this is shit and buggy

@OnlyIn(Dist.CLIENT)
@Deprecated
public class TradeSelectionMerchantScreen extends Screen {
    private static final ResourceLocation VILLAGER_LOCATION = new ResourceLocation("textures/gui/container/villager2.png");
    private static final int TEXTURE_WIDTH = 512;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int MERCHANT_MENU_PART_X = 99;
    private static final int SELL_ITEM_1_X = 5;
    private static final int SELL_ITEM_2_X = 35;
    private static final int BUY_ITEM_X = 68;
    private static final int LABEL_Y = 6;
    private static final int NUMBER_OF_OFFER_BUTTONS = 7;
    private static final int TRADE_BUTTON_X = 5;
    private static final int TRADE_BUTTON_HEIGHT = 20;
    private static final int TRADE_BUTTON_WIDTH = 88;
    private static final int SCROLLER_HEIGHT = 27;
    private static final int SCROLLER_WIDTH = 6;
    private static final int SCROLL_BAR_HEIGHT = 139;
    private static final int SCROLL_BAR_TOP_POS_Y = 18;
    private static final int SCROLL_BAR_START_X = 94;
    private static final Component TRADES_LABEL = Component.translatable("merchant.trades");
    private int shopItem;
    private final TradeOfferButton[] tradeOfferButtons = new TradeOfferButton[7];
    private final TradeOfferButton[] rightTradeOfferButtons = new TradeOfferButton[7];
    int scrollOff;
    private boolean isDragging;

    private int rightScrollOff;
    private boolean isRightDragging;

    private int imageWidth = 176;
    private int imageHeight = 166;
    private int leftPos;
    private int topPos;
    private MerchantOffers offers; 

    private static final int RIGHT_PANEL_WIDTH = TRADE_BUTTON_WIDTH * 2 + 2 ;
    private static final int PANEL_GAP = 16;


    public TradeSelectionMerchantScreen(MerchantOffers offers) {
        super(Component.translatable("tradetweaks.gui.trade_selection").withStyle(ChatFormatting.BOLD));
        this.imageWidth = 276;
        this.offers = offers;
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
    }

    private void postButtonClick() {

    }

    protected void init() {
        super.init();
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        int k = j + 16 + 2;

        for(int l = 0; l < 7; ++l) {
            this.tradeOfferButtons[l] = (TradeOfferButton)this.addRenderableWidget(new TradeOfferButton(i + 5, k, l, (button) -> {
                if (button instanceof TradeOfferButton) {
                    this.shopItem = ((TradeOfferButton)button).getIndex() + this.scrollOff;
                    this.postButtonClick();
                }

            }));
            k += 20;
        }

        k = j + 16 + 2;
        int x = i + TRADE_BUTTON_WIDTH + SCROLLER_WIDTH + PANEL_GAP;
        for(int l = 0; l < 7; ++l) {
            this.rightTradeOfferButtons[l] = (TradeOfferButton)this.addRenderableWidget(new TradeOfferButton(x + 5, k, l, (button) -> {
                if (button instanceof TradeOfferButton) {
                    this.shopItem = ((TradeOfferButton)button).getIndex() + this.scrollOff;
                    this.postButtonClick();
                }
            }));
            k += 20;
        }

    }

    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int l = this.font.width(TRADES_LABEL);
        guiGraphics.drawString(this.font, TRADES_LABEL, 5 - l / 2 + 48, 6, 4210752, false);
        guiGraphics.drawString(this.font, TRADES_LABEL, 200 - l / 2 + 48, 6, 4210752, false);
    }

    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(VILLAGER_LOCATION, i, j, 0, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 512, 256);
        guiGraphics.blit(VILLAGER_LOCATION, i + 176, j, 0, 0.0F, 0.0F, 224, this.imageHeight, 512, 256);
        MerchantOffers merchantoffers = this.offers;
        if (!merchantoffers.isEmpty()) {
            int k = this.shopItem;
            if (k < 0 || k >= merchantoffers.size()) {
                return;
            }

            MerchantOffer merchantoffer = (MerchantOffer)merchantoffers.get(k);
            if (merchantoffer.isOutOfStock()) {
                guiGraphics.blit(VILLAGER_LOCATION, this.leftPos + 83 + 99, this.topPos + 35, 0, 311.0F, 0.0F, 28, 21, 512, 256);
            }
        }

    }

    
    private void renderScroller(GuiGraphics guiGraphics, int posX, int posY, MerchantOffers merchantOffers, int scrollOffset) {
        int i = merchantOffers.size() + 1 - 7;
        if (i > 1) {
            int j = 139 - (27 + (i - 1) * 139 / i);
            int k = 1 + j / i + 139 / i;
            int l = 113;
            int i1 = Math.min(113, scrollOffset * k);
            if (scrollOffset == i - 1) {
                i1 = 113;
            }

            guiGraphics.blit(VILLAGER_LOCATION, posX + 94, posY + 18 + i1, 0, 0.0F, 199.0F, 6, 27, 512, 256);
        } else {
            guiGraphics.blit(VILLAGER_LOCATION, posX + 94, posY + 18, 0, 6.0F, 199.0F, 6, 27, 512, 256);
        }

    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        MerchantOffers merchantoffers = this.offers;
        if (!merchantoffers.isEmpty()) {
            int i = (this.width - this.imageWidth) / 2;
            int j = (this.height - this.imageHeight) / 2;
            int k = j + 16 + 1;
            int l = i + 5 + 5;
            this.renderScroller(guiGraphics, i, j, merchantoffers, this.scrollOff);
//            this.renderScroller(guiGraphics, i, j, merchantoffers, this.scrollOff);
            int i1 = 0;

            for(MerchantOffer merchantoffer : merchantoffers) {
                if (!this.canScroll(merchantoffers.size()) || i1 >= this.scrollOff && i1 < 7 + this.scrollOff) {
                    ItemStack itemstack = merchantoffer.getBaseCostA();
                    ItemStack itemstack2 = merchantoffer.getCostB();
                    ItemStack itemstack3 = merchantoffer.getResult();
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
                    int j1 = k + 2;
                    this.renderAndDecorateCostA(guiGraphics, itemstack, l, j1);
                    if (!itemstack2.isEmpty()) {
                        guiGraphics.renderFakeItem(itemstack2, i + 5 + 35, j1);
                        guiGraphics.renderItemDecorations(this.font, itemstack2, i + 5 + 35, j1);
                    }

                    this.renderButtonArrows(guiGraphics, merchantoffer, i, j1);
                    guiGraphics.renderFakeItem(itemstack3, i + 5 + 68, j1);
                    guiGraphics.renderItemDecorations(this.font, itemstack3, i + 5 + 68, j1);
                    guiGraphics.pose().popPose();
                    k += 20;
                    ++i1;
                } else {
                    ++i1;
                }
            }

            // Right side
            MerchantOffers merchantoffers2 = this.offers;
            i = (this.width - this.imageWidth) / 2 + TRADE_BUTTON_WIDTH + SCROLLER_WIDTH + PANEL_GAP + 5;
            k = j + 16 + 1;
            this.renderScroller(guiGraphics, i + TRADE_BUTTON_WIDTH + SCROLLER_WIDTH + PANEL_GAP, j, merchantoffers2, this.rightScrollOff);
            i1 = 0;
            for(MerchantOffer merchantoffer : merchantoffers2) {
                if (!this.canScroll(merchantoffers2.size()) || i1 >= this.rightScrollOff && i1 < 7 + this.rightScrollOff) {
                    ItemStack itemstack = merchantoffer.getBaseCostA();
                    ItemStack itemstack2 = merchantoffer.getCostB();
                    ItemStack itemstack3 = merchantoffer.getResult();
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
                    int j1 = k + 2;
                    this.renderAndDecorateCostA(guiGraphics, itemstack, i + 5, j1);
                    if (!itemstack2.isEmpty()) {
                        guiGraphics.renderFakeItem(itemstack2, i + 35, j1);
                        guiGraphics.renderItemDecorations(this.font, itemstack2, i + 35, j1);
                    }
                    this.renderButtonArrows(guiGraphics, merchantoffer, i - 5, j1);
                    guiGraphics.renderFakeItem(itemstack3, i + 68, j1);
                    guiGraphics.renderItemDecorations(this.font, itemstack3, i + 68, j1);
                    guiGraphics.pose().popPose();
                    k += 20;
                    ++i1;
                } else {
                    ++i1;
                }
            }


            int k1 = this.shopItem;
            MerchantOffer merchantoffer1 = (MerchantOffer)merchantoffers.get(k1);
            
            for(TradeOfferButton merchantscreen$tradeofferbutton : this.tradeOfferButtons) {
                if (merchantscreen$tradeofferbutton.isHoveredOrFocused()) {
                    merchantscreen$tradeofferbutton.renderToolTip(guiGraphics, mouseX, mouseY);
                }
                merchantscreen$tradeofferbutton.visible = merchantscreen$tradeofferbutton.index < this.offers.size();
            }

            // Right button
            for(TradeOfferButton merchantscreen$tradeofferbutton : this.rightTradeOfferButtons) {
                if (merchantscreen$tradeofferbutton.isHoveredOrFocused()) {
                    merchantscreen$tradeofferbutton.renderToolTip(guiGraphics, mouseX, mouseY);
                }
                merchantscreen$tradeofferbutton.visible = merchantscreen$tradeofferbutton.index < this.offers.size();
            }

            RenderSystem.enableDepthTest();
        }
//        renderBg(guiGraphics, partialTick, mouseX, mouseY);
        renderLabels(guiGraphics, mouseX, mouseY);

//        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderButtonArrows(GuiGraphics guiGraphics, MerchantOffer merchantOffers, int posX, int posY) {
        RenderSystem.enableBlend();
        if (merchantOffers.isOutOfStock()) {
            guiGraphics.blit(VILLAGER_LOCATION, posX + 5 + 35 + 20, posY + 3, 0, 25.0F, 171.0F, 10, 9, 512, 256);
        } else {
            guiGraphics.blit(VILLAGER_LOCATION, posX + 5 + 35 + 20, posY + 3, 0, 15.0F, 171.0F, 10, 9, 512, 256);
        }

    }

    private void renderAndDecorateCostA(GuiGraphics guiGraphics, ItemStack baseCost, int x, int y) {
        guiGraphics.renderFakeItem(baseCost, x, y);
        guiGraphics.renderItemDecorations(this.font, baseCost, x, y);
    }

    private boolean canScroll(int numOffers) {
        return numOffers > 7;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int i = this.offers.size();
        if (this.canScroll(i)) {
            int j = i - 7;
            if (mouseX < this.leftPos + TRADE_BUTTON_WIDTH + SCROLLER_WIDTH) {
                this.scrollOff = Mth.clamp((int)((double)this.scrollOff - delta), 0, j);
            } else {
                this.rightScrollOff = Mth.clamp((int)((double)this.rightScrollOff - delta), 0, j);
            }
        }
        return true;
    }
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        int i = this.offers.size();
        if (this.isDragging || this.isRightDragging) {
            int j = this.topPos + 18;
            int k = j + 139;
            int l = i - 7;
            float f = ((float)mouseY - (float)j - 13.5F) / ((float)(k - j) - 27.0F);
            f = f * (float)l + 0.5F;

            if (this.isDragging) {
                this.scrollOff = Mth.clamp((int)f, 0, l);
            } else {
                this.rightScrollOff = Mth.clamp((int)f, 0, l);
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.isDragging = false;
        this.isRightDragging = false;
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        if (this.canScroll(this.offers.size()) && mouseX > (double)(i + 94) && mouseX < (double)(i + 94 + 6)
                && mouseY > (double)(j + 18) && mouseY <= (double)(j + 18 + 139 + 1)) {
            this.isDragging = true;
        }
        else if (this.canScroll(this.offers.size()) && mouseX > (double)(i + TRADE_BUTTON_WIDTH + SCROLLER_WIDTH + PANEL_GAP + 94)
                && mouseX < (double)(i + TRADE_BUTTON_WIDTH + SCROLLER_WIDTH + PANEL_GAP + 94 + 6)
                && mouseY > (double)(j + 18) && mouseY <= (double)(j + 18 + 139 + 1)) {
            this.isRightDragging = true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

//    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
//        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
//            ItemStack itemstack = this.hoveredSlot.getItem();
//            guiGraphics.renderTooltip(this.font, this.getTooltipFromContainerItem(itemstack), itemstack.getTooltipImage(), itemstack, x, y);
//        }
//    }

    protected List<Component> getTooltipFromContainerItem(ItemStack stack) {
        return getTooltipFromItem(this.minecraft, stack);
    }

    private boolean isHovering(Slot slot, double mouseX, double mouseY) {
        return this.isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY);
    }

    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        int i = this.leftPos;
        int j = this.topPos;
        mouseX -= (double)i;
        mouseY -= (double)j;
        return mouseX >= (double)(x - 1) && mouseX < (double)(x + width + 1) && mouseY >= (double)(y - 1) && mouseY < (double)(y + height + 1);
    }


    @OnlyIn(Dist.CLIENT)
    class TradeOfferButton extends Button {
        final int index;

        public TradeOfferButton(int x, int y, int index, Button.OnPress onPress) {
            super(x, y, 88, 20, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
            this.index = index;
            this.visible = false;
        }

        public int getIndex() {
            return this.index;
        }

        public void renderToolTip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            boolean isRightPanel = this.getX() > TradeSelectionMerchantScreen.this.leftPos + TRADE_BUTTON_WIDTH;
            int currentScroll = isRightPanel ? TradeSelectionMerchantScreen.this.rightScrollOff : TradeSelectionMerchantScreen.this.scrollOff;

            if (this.isHovered && (TradeSelectionMerchantScreen.this).offers.size() > this.index + currentScroll) {
                if (mouseX < this.getX() + 20) {
                    ItemStack itemstack = ((MerchantOffer)(TradeSelectionMerchantScreen.this).offers.get(this.index + currentScroll)).getCostA();
                    guiGraphics.renderTooltip(TradeSelectionMerchantScreen.this.font, itemstack, mouseX, mouseY);
                } else if (mouseX < this.getX() + 50 && mouseX > this.getX() + 30) {
                    ItemStack itemstack2 = ((MerchantOffer)(TradeSelectionMerchantScreen.this).offers.get(this.index + currentScroll)).getCostB();
                    if (!itemstack2.isEmpty()) {
                        guiGraphics.renderTooltip(TradeSelectionMerchantScreen.this.font, itemstack2, mouseX, mouseY);
                    }
                } else if (mouseX > this.getX() + 65) {
                    ItemStack itemstack1 = ((MerchantOffer)(TradeSelectionMerchantScreen.this).offers.get(this.index + currentScroll)).getResult();
                    guiGraphics.renderTooltip(TradeSelectionMerchantScreen.this.font, itemstack1, mouseX, mouseY);
                }
            }

        }
    }
}
