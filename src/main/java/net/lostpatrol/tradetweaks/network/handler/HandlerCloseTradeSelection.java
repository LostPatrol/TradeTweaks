package net.lostpatrol.tradetweaks.network.handler;

import net.lostpatrol.tradetweaks.common.tradeselect.TradeSelectionSessionManager;
import net.lostpatrol.tradetweaks.network.packet.PacketCloseTradeSelection;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class HandlerCloseTradeSelection {
    public static void handle(PacketCloseTradeSelection packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player) {
                TradeSelectionSessionManager.closeSession(player, packet.getSessionId());
            }
        });
    }
}
