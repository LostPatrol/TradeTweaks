/** Applies explicit wand modes requested by the client wheel. */
package net.lostpatrol.tradetweaks.network.handler;

import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.lostpatrol.tradetweaks.network.packet.PacketWandModeSet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.lostpatrol.tradetweaks.network.PayloadContext;

import java.util.function.Supplier;

public class HandlerWandModeSet {
    /** Applies a selected mode to the sender's currently held wand. */
    public static void handle(PacketWandModeSet packet, PayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.player();
            if (player == null) {
                return;
            }

            ItemStack stack = player.getItemInHand(packet.getHand());
            if (stack.getItem() instanceof EmeraldWand) {
                EmeraldWand.setMode(stack, packet.getMode());
                player.setItemInHand(packet.getHand(), stack);
            }
        });
    }
}
