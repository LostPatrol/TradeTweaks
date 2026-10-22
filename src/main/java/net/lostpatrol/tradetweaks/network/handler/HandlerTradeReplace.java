/** Routes authenticated trade replacements to the session manager. */
package net.lostpatrol.tradetweaks.network.handler;

import net.lostpatrol.tradetweaks.common.tradeselect.TradeSelectionSessionManager;
import net.lostpatrol.tradetweaks.network.packet.PacketTradeReplace;
import net.minecraft.server.level.ServerPlayer;
import net.lostpatrol.tradetweaks.network.PayloadContext;

import java.util.function.Supplier;

public class HandlerTradeReplace {
    /** Applies an authenticated replacement request on the server thread. */
    public static void handle(PacketTradeReplace packet, PayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.player();
            if (player != null) {
                TradeSelectionSessionManager.replaceTrade(
                        player,
                        packet.getSessionId(),
                        packet.getTradeIndex(),
                        packet.getCandidateIndex()
                );
            }
        });
    }
}
