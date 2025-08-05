package net.lostpatrol.tradetweaks.common.select;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.lostpatrol.tradetweaks.TradeTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
                        if (listing instanceof VillagerTrades.EnchantBookForEmeralds) {
                            return getEnchantedBookOffers(dummyVillager, dummyVillager.getRandom(), selectedOffer.getXp());
                        }

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

    public static List<MerchantOffer> getEnchantedBookOffers(Entity trader, RandomSource random, int villagerXp) {
        List<Enchantment> tradableEnchantments = BuiltInRegistries.ENCHANTMENT.stream().filter(Enchantment::isTradeable).collect(Collectors.toList());

        List<MerchantOffer> offers = new ArrayList<>();

        for (Enchantment enchantment : tradableEnchantments) {
            int maxLevel = enchantment.getMaxLevel();
            ItemStack enchantedBook = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, maxLevel));

            int basePrice = 2 + 3 * maxLevel;
            int minPrice = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                int price = basePrice + random.nextInt(5 + maxLevel * 10);
                if (enchantment.isTreasureOnly()) {
                    price *= 2;
                }
                price = Math.min(price, 64);
                if (price < minPrice) {
                    minPrice = price;
                }
            }

            MerchantOffer offer = new MerchantOffer(new ItemStack(Items.EMERALD, minPrice), new ItemStack(Items.BOOK), enchantedBook, 12, villagerXp, 0.2F);
            offers.add(offer);
        }

        return offers;
    }
}

