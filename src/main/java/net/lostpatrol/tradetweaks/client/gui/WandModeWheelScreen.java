package net.lostpatrol.tradetweaks.client.gui;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.lostpatrol.tradetweaks.client.events.ClientKeyMappings;
import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.lostpatrol.tradetweaks.network.NetworkHandler;
import net.lostpatrol.tradetweaks.network.packet.PacketWandModeSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class WandModeWheelScreen extends Screen {
    private static final int WHEEL_RADIUS = 58;
    private static final int SLOT_SIZE = 30;
    private static final int ICON_SIZE = 24;
    private static final int ICON_TEXTURE_SIZE = 64;
    private static final double DEAD_ZONE = 15.0D;
    private static final int SEPARATOR_INNER_RADIUS = 22;
    private static final int SEPARATOR_OUTER_RADIUS = 88;

    private static final WandModeOption[] OPTIONS = new WandModeOption[]{
            new WandModeOption(EmeraldWand.WandMode.RESET_MODE, "reset", "icon_reset.png"),
            new WandModeOption(EmeraldWand.WandMode.TRACKING_BLOCK_MODE, "tracking_block", "icon_block_track.png"),
            new WandModeOption(EmeraldWand.WandMode.TRACKING_VILLAGER_MODE, "tracking_villager", "icon_villager_track.png"),
            new WandModeOption(EmeraldWand.WandMode.REFRESH_MODE, "refresh", "iocn_refresh.png"),
            new WandModeOption(EmeraldWand.WandMode.UPGRADE_MODE, "upgrade", "icon_upgrade.png"),
            new WandModeOption(EmeraldWand.WandMode.SELECT_MODE, "select", "icon_select.png")
    };

    private final InteractionHand hand;
    private EmeraldWand.WandMode selectedMode;

    public WandModeWheelScreen(InteractionHand hand) {
        super(Component.translatable("tradetweaks.gui.wand_mode_wheel"));
        this.hand = hand;
    }

    @Override
    protected void init() {
        super.init();
        this.selectedMode = getCurrentMode();
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.selectedMode = getSelectedMode(mouseX, mouseY);
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        guiGraphics.fill(0, 0, this.width, this.height, 0x88000000);
        drawSeparators(guiGraphics, centerX, centerY);

        for (int i = 0; i < OPTIONS.length; i++) {
            WandModeOption option = OPTIONS[i];
            double angle = getOptionAngle(i);
            int slotX = centerX + (int) Math.round(Math.cos(angle) * WHEEL_RADIUS) - SLOT_SIZE / 2;
            int slotY = centerY + (int) Math.round(Math.sin(angle) * WHEEL_RADIUS) - SLOT_SIZE / 2;
            renderOption(guiGraphics, option, slotX, slotY, option.mode() == selectedMode);
        }

        Component selectedLabel = Component.translatable("tradetweaks.emerald_wand.wheel." + selectedMode.getTranslateKey());
        guiGraphics.drawCenteredString(this.font, selectedLabel, centerX, centerY + WHEEL_RADIUS + 34, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, this.title, centerX, centerY - WHEEL_RADIUS - 44, 0xD7F7D0);
    }

    private void drawSeparators(GuiGraphics guiGraphics, int centerX, int centerY) {
        double slice = Math.PI * 2.0D / OPTIONS.length;
        for (int i = 0; i < OPTIONS.length; i++) {
            double angle = getOptionAngle(i) - slice / 2.0D;
            int innerX = centerX + (int) Math.round(Math.cos(angle) * SEPARATOR_INNER_RADIUS);
            int innerY = centerY + (int) Math.round(Math.sin(angle) * SEPARATOR_INNER_RADIUS);
            int outerX = centerX + (int) Math.round(Math.cos(angle) * SEPARATOR_OUTER_RADIUS);
            int outerY = centerY + (int) Math.round(Math.sin(angle) * SEPARATOR_OUTER_RADIUS);
            drawLine(guiGraphics, innerX, innerY, outerX, outerY, 0x44FFFFFF);
        }
    }

    private void drawLine(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int color) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        if (steps == 0) {
            guiGraphics.fill(x1, y1, x1 + 1, y1 + 1, color);
            return;
        }

        for (int i = 0; i <= steps; i++) {
            int x = x1 + Math.round(dx * (float) i / steps);
            int y = y1 + Math.round(dy * (float) i / steps);
            guiGraphics.fill(x, y, x + 1, y + 1, color);
        }
    }

    private void renderOption(GuiGraphics guiGraphics, WandModeOption option, int x, int y, boolean selected) {
        int background = selected ? 0xD0BFE9A8 : 0xD0D5D5D5;
        int border = selected ? 0xFF3F7F3B : 0xFF777777;

        guiGraphics.fill(x - 2, y - 2, x + SLOT_SIZE + 2, y + SLOT_SIZE + 2, border);
        guiGraphics.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, background);

        int iconX = x + (SLOT_SIZE - ICON_SIZE) / 2;
        int iconY = y + (SLOT_SIZE - ICON_SIZE) / 2;
        renderPixelIcon(guiGraphics, option.icon(), iconX, iconY);
    }

    private void renderPixelIcon(GuiGraphics guiGraphics, ResourceLocation icon, int x, int y) {
        AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(icon);
        texture.setFilter(false, false);
        guiGraphics.blit(icon, x, y, ICON_SIZE, ICON_SIZE, 0.0F, 0.0F, ICON_TEXTURE_SIZE, ICON_TEXTURE_SIZE, ICON_TEXTURE_SIZE, ICON_TEXTURE_SIZE);
    }

    private EmeraldWand.WandMode getSelectedMode(double mouseX, double mouseY) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        double dx = mouseX - centerX;
        double dy = mouseY - centerY;
        if (dx * dx + dy * dy < DEAD_ZONE * DEAD_ZONE) {
            return getCurrentMode();
        }

        double angle = Math.atan2(dy, dx);
        double slice = Math.PI * 2.0D / OPTIONS.length;
        int index = (int) Math.round((angle - getOptionAngle(0)) / slice);
        index = Math.floorMod(index, OPTIONS.length);
        return OPTIONS[index].mode();
    }

    private double getOptionAngle(int index) {
        return -Math.PI / 2.0D + index * (Math.PI * 2.0D / OPTIONS.length);
    }

    private EmeraldWand.WandMode getCurrentMode() {
        if (this.minecraft == null || this.minecraft.player == null) {
            return EmeraldWand.WandMode.RESET_MODE;
        }
        ItemStack stack = this.minecraft.player.getItemInHand(hand);
        if (!(stack.getItem() instanceof EmeraldWand)) {
            return EmeraldWand.WandMode.RESET_MODE;
        }
        return EmeraldWand.getMode(stack);
    }

    private void applySelection() {
        if (this.minecraft == null || this.minecraft.player == null) {
            return;
        }

        ItemStack stack = this.minecraft.player.getItemInHand(hand);
        if (!(stack.getItem() instanceof EmeraldWand)) {
            return;
        }

        EmeraldWand.setMode(stack, selectedMode);
        this.minecraft.player.displayClientMessage(
                Component.translatable("tradetweaks.emerald_wand.mode." + selectedMode.getTranslateKey()),
                true
        );
        NetworkHandler.sendWandModeSetToServer(new PacketWandModeSet(selectedMode, hand));
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (ClientKeyMappings.WAND_MODE_WHEEL.matches(keyCode, scanCode)) {
            applySelection();
            this.onClose();
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private record WandModeOption(EmeraldWand.WandMode mode, String key, ResourceLocation icon) {
        private WandModeOption(EmeraldWand.WandMode mode, String key, String iconName) {
            this(mode, key, new ResourceLocation(TradeTweaks.MODID, "textures/icon/" + iconName));
        }
    }
}
