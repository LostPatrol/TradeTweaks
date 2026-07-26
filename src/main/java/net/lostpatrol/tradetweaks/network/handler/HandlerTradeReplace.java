package net.lostpatrol.tradetweaks.network.handler;

import net.lostpatrol.tradetweaks.common.tradeselect.TradeSelectionSessionManager;
import net.lostpatrol.tradetweaks.network.packet.PacketTradeReplace;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class HandlerTradeReplace {
    public static void handle(PacketTradeReplace packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                TradeSelectionSessionManager.replaceTrade(
                        player,
                        packet.getSessionId(),
                        packet.getTradeIndex(),
                        packet.getCandidateIndex()
                );
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
