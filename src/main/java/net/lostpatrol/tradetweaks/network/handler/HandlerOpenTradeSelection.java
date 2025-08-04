package net.lostpatrol.tradetweaks.network.handler;

import net.lostpatrol.tradetweaks.client.gui.TradeSelectionScreen;
import net.lostpatrol.tradetweaks.common.dummy.DummyVillager;
import net.lostpatrol.tradetweaks.network.packet.PacketOpenTradeSelection;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class HandlerOpenTradeSelection {
    public static void handle(PacketOpenTradeSelection packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleClient(packet));
            }
        });
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(PacketOpenTradeSelection packet) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        mc.setScreen(new TradeSelectionScreen(new DummyVillager(
                packet.getVillagerType(),
                packet.getProfessionName(),
                packet.getProfessionLevel(),
                packet.getOffers(),
                packet.getVillagerId(),
                mc.level
        )));
        }
    }
