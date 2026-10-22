/** Opens the wand mode wheel from shared client tick callbacks. */
package net.lostpatrol.tradetweaks.client.events;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.lostpatrol.tradetweaks.client.gui.WandModeWheelScreen;
import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class WandModeWheelHandler {
    private WandModeWheelHandler() {
    }

    /** Opens the mode wheel when its key binding is pressed. */
    public static void onClientTick() {

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.screen != null) {
            return;
        }

        while (ClientKeyMappings.WAND_MODE_WHEEL.consumeClick()) {
            InteractionHand hand = findHeldWandHand(mc.player);
            if (hand != null) {
                mc.setScreen(new WandModeWheelScreen(hand));
            }
        }
    }

    private static InteractionHand findHeldWandHand(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof EmeraldWand) {
            return InteractionHand.MAIN_HAND;
        }

        ItemStack offHand = player.getOffhandItem();
        if (offHand.getItem() instanceof EmeraldWand) {
            return InteractionHand.OFF_HAND;
        }

        return null;
    }
}
