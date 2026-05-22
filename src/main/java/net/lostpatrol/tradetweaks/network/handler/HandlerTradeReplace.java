package net.lostpatrol.tradetweaks.network.handler;

import net.lostpatrol.tradetweaks.config.ServerConfig;
import net.lostpatrol.tradetweaks.network.packet.PacketTradeReplace;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class HandlerTradeReplace {
    public static void handle(PacketTradeReplace packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Entity entity = ctx.player().level().getEntity(packet.getVillagerId());
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
        });
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

