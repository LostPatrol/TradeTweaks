package net.lostpatrol.tradetweaks.network.handler;

import net.lostpatrol.tradetweaks.common.tradeselect.TradeSelectionSessionManager;
import net.lostpatrol.tradetweaks.network.packet.PacketCloseTradeSelection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class HandlerCloseTradeSelection {
    public static void handle(PacketCloseTradeSelection packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                TradeSelectionSessionManager.closeSession(player, packet.getSessionId());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
