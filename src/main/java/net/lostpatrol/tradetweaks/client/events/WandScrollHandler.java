/** Handles sneak scrolling for wands independently of loader input events. */
package net.lostpatrol.tradetweaks.client.events;

import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class WandScrollHandler {

    /** Handles sneak scrolling and reports whether hotbar scrolling must stop. */
    public static boolean onMouseScroll(double scroll) {
        Player player = Minecraft.getInstance().player;
        if (player == null || !player.isShiftKeyDown()) return false;
        if (scroll == (double)0.0F) return false;

        InteractionHand hand = null;
        ItemStack heldItem = player.getMainHandItem();

        if (heldItem.getItem() instanceof EmeraldWand) {
            hand = InteractionHand.MAIN_HAND;
        } else {
            ItemStack offhandItem = player.getOffhandItem();
            if (offhandItem.getItem() instanceof EmeraldWand) {
                heldItem = offhandItem;
                hand = InteractionHand.OFF_HAND;
            }
        }

        if (hand != null && heldItem.getItem() instanceof EmeraldWand wand) {
            wand.switchMode(heldItem, player, (scroll > (double) 0.0F), hand);
            return true;
        }
        return false;
    }
}


