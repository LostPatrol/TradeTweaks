package net.lostpatrol.tradetweaks.network.handler;

import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.lostpatrol.tradetweaks.network.packet.PacketWandModeSwitch;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static net.lostpatrol.tradetweaks.common.wand.EmeraldWand.*;

public class HandlerWandModeSwitch {
    public static void handle(PacketWandModeSwitch packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof EmeraldWand wand) {
                    handleModeSwitchServer(stack, player, packet.isForward());
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void handleModeSwitchServer(ItemStack stack, Player player, boolean forward) {
        EmeraldWand.WandMode current = getMode(stack);
        EmeraldWand.WandMode newMode = forward ? current.next() : current.previous();
        setMode(stack, newMode);
        player.setItemInHand(player.getUsedItemHand(), stack);
    }
}
