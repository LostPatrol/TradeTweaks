package net.lostpatrol.tradetweaks.network.handler;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.lostpatrol.tradetweaks.network.packet.PacketOpenTradeSelection;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.lang.reflect.Method;

public class HandlerOpenTradeSelection {
    public static void handle(PacketOpenTradeSelection packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            try {
                Class<?> clazz = Class.forName("net.lostpatrol.tradetweaks.client.ClientTradeSelectionOpener");
                Method method = clazz.getDeclaredMethod("open", PacketOpenTradeSelection.class);
                method.invoke(null, packet);
            } catch (Exception e) {
                TradeTweaks.LOGGER.error("Failed to open trade selection screen", e);
            }
        });
    }
}
