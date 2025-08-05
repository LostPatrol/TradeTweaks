package net.lostpatrol.tradetweaks.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;


@OnlyIn(Dist.CLIENT)
public class TradeListWidget extends ObjectSelectionList<TradeListWidget.TradeEntry> {

    private static final ResourceLocation VILLAGER_LOCATION = new ResourceLocation("textures/gui/container/villager2.png");
    private final TradeSelectionScreen parent;

    public TradeListWidget(Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight) {
        super(minecraft, width, height, y0, y1, itemHeight);
        this.parent = (TradeSelectionScreen) minecraft.screen;
        this.setRenderBackground(false);
        this.setRenderTopAndBottom(false);
        this.setRenderSelection(true);
    }

    @Override
    public int getRowWidth(){
        return this.width - 4 * TradeSelectionScreen.PANEL_SPACING;
//        return this.width;
    }

    @Override
    protected void enableScissor(@Nonnull GuiGraphics guiGraphics) {
        guiGraphics.enableScissor(0,this.y0,parent.width,parent.height);
    }

    @Override
    protected void renderSelection(GuiGraphics guiGraphics, int top, int width, int height, int outerColor, int innerColor) {
        int i = this.x0 + (this.width - width) / 2;
        int j = this.x0 + (this.width + width) / 2;
        guiGraphics.fill(i, top - 2, j, top + height + 2, outerColor);
        guiGraphics.fill(i + 1, top - 1, j - 1, top + height + 1, innerColor);
    }

    @Override
    protected int getScrollbarPosition() {
        return this.x0 + this.width - 6;
    }

    class TradeEntry extends ObjectSelectionList.Entry<TradeEntry> {
        private final MerchantOffer offer;
        private final int index;
        private static final int ITEM_SPACING = 25;

        public TradeEntry(MerchantOffer offer, int index) {
            this.offer = offer;
            this.index = index;
        }

        @Override
        public void render(@Nonnull GuiGraphics guiGraphics, int index, int top, int left, int width, int height,
                           int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            Font font = Minecraft.getInstance().font;
            int panelWidth = TradeListWidget.this.width;

//            int xPos = left + (panelWidth / 2) - ITEM_SPACING * 2;;
            int xPos = TradeListWidget.this.x0 + (TradeListWidget.this.width - width) / 2 + (int) (TradeSelectionScreen.PANEL_SPACING * 1.5);
            int yPos = top;

            renderTradeItem(guiGraphics, font, offer.getCostA(), xPos, yPos, mouseX, mouseY, 16);
            renderTradeItem(guiGraphics, font, offer.getCostB(), xPos + ITEM_SPACING, yPos, mouseX, mouseY, 16);
            renderTradeItem(guiGraphics, font, offer.getResult(), xPos + ITEM_SPACING*3, yPos, mouseX, mouseY, 16);

            renderButtonArrows(guiGraphics, xPos + ITEM_SPACING * 2, yPos);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
                if (parent == null) return false;
                if (TradeListWidget.this == parent.leftPanel) {
                    parent.selectExistingTrade(this.index);
                } else {
                    parent.selectReplacement(this.index);
                }
                return true;
            }
            return false;
        }

        @Override
        public void renderBack(@Nonnull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            if (isMouseOver)
                guiGraphics.fill(left, top, left-1 + width-2, top + height+1, 0x33FFFFFF);
        }

        @Override
        @Nonnull
        public Component getNarration() {
            return Component.translatable("tradetweaks.gui.narration.trade_entry",
                    offer.getCostA().getHoverName(),
                    offer.getResult().getHoverName());
        }
    }

    protected void addTrade(MerchantOffer offer, int index) {
        this.addEntry(new TradeEntry(offer, index));
    }

    protected MerchantOffer getTrade(int index){
        if (index >= 0 && index < this.children().size()) {
            return this.children().get(index).offer;
        }
        return null;
    }

    protected void clearTrades() {
        this.clearEntries();
    }

    private void renderTradeItem(GuiGraphics guiGraphics, Font font, ItemStack itemStack, int x, int y, int mouseX, int mouseY, int iconSize){
        if (itemStack == null || itemStack.isEmpty())
            return;

        guiGraphics.renderItem(itemStack, x, y);
        guiGraphics.renderItemDecorations(font, itemStack, x, y);
        if (mouseOnItem(mouseX, mouseY, x, y, iconSize)) {
            guiGraphics.fill(x, y, x + iconSize, y + iconSize, 0x33FF0000);
            guiGraphics.renderTooltip(font, itemStack, mouseX, mouseY);
        }

    }

    private boolean mouseOnItem(int mouseX, int mouseY, int x, int y, int iconSize) {
        return mouseX >= x && mouseX < x + iconSize && mouseY >= y && mouseY < y+iconSize;
    }

    private void renderButtonArrows(GuiGraphics guiGraphics, int posX, int posY) {
        RenderSystem.enableBlend();
        guiGraphics.blit(VILLAGER_LOCATION, posX, posY + 3, 0, 15.0F, 171.0F, 10, 9, 512, 256);
    }
}