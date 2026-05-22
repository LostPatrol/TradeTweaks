package net.lostpatrol.tradetweaks.common.wand.handler;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.lostpatrol.tradetweaks.TradeTweaks;
import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.lostpatrol.tradetweaks.config.ServerConfig;
import net.lostpatrol.tradetweaks.integrations.QuarkCompat;
import net.lostpatrol.tradetweaks.network.NetworkHandler;
import net.lostpatrol.tradetweaks.network.packet.PacketOpenTradeSelection;
import net.lostpatrol.tradetweaks.util.CompareTrades;
import net.lostpatrol.tradetweaks.util.VillagerUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EnchantmentTags;
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
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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
                    villager.getVillagerData().getType().toString(),
                    ServerConfig.isLibrarianEnchantedBookSelectionEnabled()
            ));
        }
        return InteractionResult.CONSUME;
    }

    @OnlyIn(Dist.CLIENT)
    public static List<MerchantOffer> getPossibleTrades(MerchantOffer selectedOffer, int professionLevel, VillagerProfession profession,
                                                        Villager dummyVillager, boolean allowEnchantedBookTrades) {
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
                    boolean isEnchantedBook = false;
                    try {
                        if (listing instanceof VillagerTrades.EnchantBookForEmeralds) {
                            if (allowEnchantedBookTrades) {
                                possibleTrades.addAll(getEnchantedBookOffers(dummyVillager, dummyVillager.getRandom(), selectedOffer.getXp()));
                            }
                            isEnchantedBook = true;
                        }

                        MerchantOffer offer = listing.getOffer(dummyVillager, dummyVillager.getRandom());
                        if (offer != null && !isEnchantedBook && isValidReplacement(offer, allowEnchantedBookTrades)) {
                            possibleTrades.add(offer);
                        }

                    } catch (Exception e) {
                        TradeTweaks.LOGGER.error("Failed to generate trade offer", e);
                    }
                }
                try {
                    // Quark's Ancient Tome
                    if (allowEnchantedBookTrades && QuarkCompat.isQuarkLoaded() && levelOfTrade == 5  && profession == VillagerProfession.LIBRARIAN){
                        TradeTweaks.LOGGER.info("Detected Quark loaded. Try to get Ancient Tome trades");
                        possibleTrades.addAll(QuarkCompat.getAncientTomeOffers(dummyVillager.level().registryAccess()));
                    }
                } catch (Exception e) {
                    TradeTweaks.LOGGER.error("Failed to generate Quark's Ancient Tome trades: ", e);
                }
            }
        }
        return possibleTrades;
    }

    private static boolean isValidReplacement(MerchantOffer replacement, boolean allowEnchantedBookTrades) {
        return allowEnchantedBookTrades
                || (!isEnchantedBookStack(replacement.getCostA())
                && !isEnchantedBookStack(replacement.getCostB())
                && !isEnchantedBookStack(replacement.getResult()));
    }

    private static boolean isEnchantedBookStack(ItemStack stack) {
        return stack != null && stack.is(Items.ENCHANTED_BOOK);
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
        Registry<Enchantment> registry = trader.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        List<? extends Holder<Enchantment>> tradableEnchantments = registry.holders()
                .filter(holder -> holder.is(EnchantmentTags.TRADEABLE))
                .toList();

        List<MerchantOffer> offers = new ArrayList<>();

        for (Holder<Enchantment> enchantment : tradableEnchantments) {
            int maxLevel = enchantment.value().getMaxLevel();
            ItemStack enchantedBook = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, maxLevel));

            int price = 2 + 3 * maxLevel + Math.min(Math.min(random.nextInt(5 + maxLevel * 10), random.nextInt(5 + maxLevel * 10)), random.nextInt(5 + maxLevel * 10));

            if (enchantment.is(EnchantmentTags.TREASURE)) {
                price *= 2;
            }
            price = Math.min(price, 64);

            MerchantOffer offer = new MerchantOffer(
                    new ItemCost(Items.EMERALD, price),
                    Optional.of(new ItemCost(Items.BOOK, 1)),
                    enchantedBook,
                    12,
                    villagerXp,
                    0.2F
            );
            offers.add(offer);
        }

        return offers;
    }
}

