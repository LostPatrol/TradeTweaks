package net.lostpatrol.tradetweaks.network.handler;

import net.lostpatrol.tradetweaks.config.ServerConfig;
import net.lostpatrol.tradetweaks.network.packet.PacketTradeReplace;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class HandlerTradeReplace {
    public static void handle(PacketTradeReplace packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                Entity entity = player.level().getEntity(packet.getVillagerId());
                if (entity instanceof Villager villager) {
                    MerchantOffer replacement = packet.getReplacementOffer();
                    if (!ServerConfig.isLibrarianEnchantedBookSelectionEnabled() && containsEnchantedBook(replacement)) {
                        return;
                    }

                    MerchantOffers offers = villager.getOffers();
                    int tradeIndex = packet.getTradeIndex();
                    if (tradeIndex >= 0 && tradeIndex < offers.size()) {
                        offers.set(tradeIndex, replacement);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private static boolean containsEnchantedBook(MerchantOffer offer) {
        return isEnchantedBookStack(offer.getCostA())
                || isEnchantedBookStack(offer.getCostB())
                || isEnchantedBookStack(offer.getResult());
    }

    private static boolean isEnchantedBookStack(ItemStack stack) {
        return stack != null && stack.is(Items.ENCHANTED_BOOK);
    }
}
