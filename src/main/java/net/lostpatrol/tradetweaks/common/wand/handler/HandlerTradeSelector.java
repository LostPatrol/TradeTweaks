package net.lostpatrol.tradetweaks.common.wand.handler;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.lostpatrol.tradetweaks.TradeTweaks;
import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.lostpatrol.tradetweaks.network.NetworkHandler;
import net.lostpatrol.tradetweaks.network.packet.PacketOpenTradeSelection;
import net.lostpatrol.tradetweaks.util.CompareTrades;
import net.lostpatrol.tradetweaks.util.VillagerUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class HandlerTradeSelector {

    public static final Component SELECT_FAIL_NOT_UPGRADED = Component.translatable("tradetweaks.emerald_wand.fail.wand_not_upgraded").withStyle(ChatFormatting.RED);
    public static final Component SELECT_FAIL_NOT_OCCUPIED = Component.translatable("tradetweaks.emerald_wand.fail.villager_not_occupied").withStyle(ChatFormatting.RED);

    public static InteractionResult handle(ItemStack stack, Player player, Villager villager) {
        if (!EmeraldWand.isUpgraded(stack)){
            player.displayClientMessage(SELECT_FAIL_NOT_UPGRADED, true);
            return InteractionResult.SUCCESS;
        }
        if (VillagerUtil.isUnemployed(villager) || VillagerUtil.isNitwit(villager)) {
            player.displayClientMessage(SELECT_FAIL_NOT_OCCUPIED, true);
            return InteractionResult.SUCCESS;
        }

        if (player instanceof ServerPlayer serverPlayer){
            NetworkHandler.sendOpenTradeSelectionToPlayer(serverPlayer, new PacketOpenTradeSelection(
                    villager.getId(),
                    villager.getOffers(),
                    villager.getVillagerData().getLevel(),
                    villager.getVillagerData().getProfession().name(),
                    villager.getVillagerData().getType().toString()
            ));
        }
        return InteractionResult.CONSUME;
    }

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

    private static List<MerchantOffer> getEnchantedBookOffers(Entity trader, RandomSource random, int villagerXp) {
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
