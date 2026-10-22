package net.lostpatrol.tradetweaks.client;

import net.lostpatrol.tradetweaks.client.gui.TradeSelectionScreen;
import net.lostpatrol.tradetweaks.network.packet.PacketOpenTradeSelection;
import net.minecraft.client.Minecraft;

public final class ClientTradeSelectionOpener {
    private ClientTradeSelectionOpener() {}

    public static void open(PacketOpenTradeSelection packet) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }

        mc.setScreen(new TradeSelectionScreen(packet));
    }
}
