package net.lostpatrol.tradetweaks.network.handler;

import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.lostpatrol.tradetweaks.network.packet.PacketWandModeSet;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class HandlerWandModeSet {
    public static void handle(PacketWandModeSet packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            ItemStack stack = player.getItemInHand(packet.getHand());
            if (stack.getItem() instanceof EmeraldWand) {
                EmeraldWand.setMode(stack, packet.getMode());
                player.setItemInHand(packet.getHand(), stack);
            }
        });
    }
}
