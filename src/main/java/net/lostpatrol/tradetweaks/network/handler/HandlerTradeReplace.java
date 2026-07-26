package net.lostpatrol.tradetweaks.network.handler;

import net.lostpatrol.tradetweaks.common.tradeselect.TradeSelectionSessionManager;
import net.lostpatrol.tradetweaks.network.packet.PacketTradeReplace;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class HandlerTradeReplace {
    public static void handle(PacketTradeReplace packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player) {
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

