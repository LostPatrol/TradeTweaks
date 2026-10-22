/** Opens the client trade screen without linking client classes on servers. */
package net.lostpatrol.tradetweaks.network.handler;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.lostpatrol.tradetweaks.network.PayloadContext;
import net.lostpatrol.tradetweaks.network.packet.PacketOpenTradeSelection;

import java.util.function.Supplier;

public class HandlerOpenTradeSelection {
    /** Opens the client trade screen without linking client classes on dedicated servers. */
    public static void handle(PacketOpenTradeSelection packet, PayloadContext context) {
        context.enqueueWork(() -> {
            try {
                Class.forName("net.lostpatrol.tradetweaks.client.ClientTradeSelectionOpener")
                        .getDeclaredMethod("open", PacketOpenTradeSelection.class).invoke(null, packet);
            } catch (ReflectiveOperationException error) {
                TradeTweaks.LOGGER.error("Failed to open trade selection screen", error);
            }
        });
    }
}
