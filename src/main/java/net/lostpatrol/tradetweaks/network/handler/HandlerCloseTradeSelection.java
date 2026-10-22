/** Releases the server trade session requested by the client. */
package net.lostpatrol.tradetweaks.network.handler;

import net.lostpatrol.tradetweaks.common.tradeselect.TradeSelectionSessionManager;
import net.lostpatrol.tradetweaks.network.packet.PacketCloseTradeSelection;
import net.minecraft.server.level.ServerPlayer;
import net.lostpatrol.tradetweaks.network.PayloadContext;

import java.util.function.Supplier;

public class HandlerCloseTradeSelection {
    /** Releases the matching session for the authenticated packet sender. */
    public static void handle(PacketCloseTradeSelection packet, PayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.player();
            if (player != null) {
                TradeSelectionSessionManager.closeSession(player, packet.getSessionId());
            }
        });
    }
}
