package net.lostpatrol.tradetweaks.network.handler;

import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.lostpatrol.tradetweaks.network.packet.PacketWandModeSet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class HandlerWandModeSet {
    public static void handle(PacketWandModeSet packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) {
                return;
            }

            ItemStack stack = player.getItemInHand(packet.getHand());
            if (stack.getItem() instanceof EmeraldWand) {
                EmeraldWand.setMode(stack, packet.getMode());
                player.setItemInHand(packet.getHand(), stack);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
