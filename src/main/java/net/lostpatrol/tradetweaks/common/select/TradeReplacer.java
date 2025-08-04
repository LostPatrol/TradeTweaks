package net.lostpatrol.tradetweaks.common.select;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.lostpatrol.tradetweaks.TradeTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

import net.lostpatrol.tradetweaks.util.CompareTrades;

public class TradeReplacer {

    @OnlyIn(Dist.CLIENT)
    public static List<MerchantOffer> getPossibleTrades(MerchantOffer selectedOffer, int professionLevel, VillagerProfession profession, Villager dummyVillager) {
        if (Minecraft.getInstance().level == null)
            return null;

        if (profession == null || profession == VillagerProfession.NONE) {
            return null;
        }

        List<MerchantOffer> possibleTrades = new ArrayList<>();
        Int2ObjectMap<VillagerTrades.ItemListing[]> tradesMap = VillagerTrades.TRADES.get(profession);

        if (tradesMap != null && !tradesMap.isEmpty()) {
            int levelOfTrade = getLevelOfTrade(selectedOffer, tradesMap, dummyVillager);
            if (levelOfTrade ==-1 || levelOfTrade > professionLevel)
                return null;

            VillagerTrades.ItemListing[] listings = tradesMap.get(levelOfTrade);
            if (listings != null) {
                for (VillagerTrades.ItemListing listing : listings) {
                    try {
                        MerchantOffer offer = listing.getOffer(dummyVillager, dummyVillager.getRandom());
                        if (offer != null && isValidReplacement(offer)) {
                            possibleTrades.add(offer);
                        }
                    } catch (Exception e) {
                        TradeTweaks.LOGGER.error("Failed to generate trade offer", e);
                    }
                }
            }
        }
        return possibleTrades;
    }

    private static boolean isValidReplacement(MerchantOffer replacement) {
        // TODO
        return true;
    }

    private static int getLevelOfTrade(MerchantOffer selectedOffer, Int2ObjectMap<VillagerTrades.ItemListing[]> tradesMap, Villager dummyVillager){
        // find trade level of selected offer
        for (int lvl = 1; lvl <= 5; lvl++) {
            VillagerTrades.ItemListing[] listings = tradesMap.get(lvl);
            if (listings != null) {
                for (VillagerTrades.ItemListing listing : listings) {
                    MerchantOffer offer = listing.getOffer(dummyVillager, dummyVillager.getRandom());
                    if (offer != null && CompareTrades.isCounterpartTrade(offer, selectedOffer)) {
                        return lvl;
                    }
                }
            }
        }
        return -1;
    }
}

