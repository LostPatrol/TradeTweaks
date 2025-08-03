package net.lostpatrol.tradetweaks.network.handler;

import net.lostpatrol.tradetweaks.client.gui.TradeSelectionMerchantScreen;
import net.lostpatrol.tradetweaks.client.gui.TradeSelectionScreen;
import net.lostpatrol.tradetweaks.common.dummy.DummyVillager;
import net.lostpatrol.tradetweaks.network.packet.PacketOpenTradeSelection;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffers;
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

        DummyVillager dummyVillager = new DummyVillager(
                packet.getVillagerType(),
                packet.getProfessionName(),
                packet.getProfessionLevel(),
                packet.getOffers(),
                packet.getVillagerId(),
                mc.level
        );
        MerchantMenu dummyMenu = new MerchantMenu(
                -1,
                mc.player.getInventory(),
                dummyVillager.getDummyVillager()
        );
        dummyMenu.setOffers(new MerchantOffers(dummyVillager.getOffers().createTag()));

        if (dummyVillager.getOffers() == null || dummyVillager.getOffers().isEmpty()) {
            mc.gui.setOverlayMessage(Component.literal("something went wrong").withStyle(ChatFormatting.RED), true);
            return;
        }

//        mc.setScreen(new TradeSelectionMerchantScreen(dummyVillager.getOffers()));

        mc.setScreen(new TradeSelectionScreen(
                packet.getVillagerId(),
                packet.getOffers(),
                packet.getProfessionLevel(),
                packet.getProfessionName(),
                packet.getVillagerType()
                ));
        }
    }
