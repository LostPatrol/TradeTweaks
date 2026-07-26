package net.lostpatrol.tradetweaks.common.tradeselect;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.lostpatrol.tradetweaks.TradeTweaks;
import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.lostpatrol.tradetweaks.config.ServerConfig;
import net.lostpatrol.tradetweaks.integrations.QuarkCompat;
import net.lostpatrol.tradetweaks.network.packet.PacketOpenTradeSelection;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class TradeSelectionSessionManager {
    private static final long SESSION_LIFETIME_MILLIS = 120_000L;
    private static final double MAX_REPLACE_DISTANCE_SQUARED = 64.0D;
    private static final Map<UUID, SelectionSession> SESSIONS = new HashMap<>();

    private TradeSelectionSessionManager() {
    }

    public static PacketOpenTradeSelection createSession(ServerPlayer player, Villager villager) {
        long now = System.currentTimeMillis();
        SESSIONS.values().removeIf(session -> now - session.createdAtMillis() > SESSION_LIFETIME_MILLIS);

        MerchantOffers offers = villager.getOffers();
        int careerLevel = villager.getVillagerData().getLevel();
        Int2ObjectMap<VillagerTrades.ItemListing[]> trades = getTrades(villager);
        boolean allowEnchantedBooks = ServerConfig.isLibrarianEnchantedBookSelectionEnabled();

        List<MerchantOffers> candidatePools = new ArrayList<>(careerLevel);
        for (int level = 1; level <= careerLevel; level++) {
            candidatePools.add(generateCandidatePool(villager, trades, level, allowEnchantedBooks));
        }

        int[] candidatePoolIndices = new int[offers.size()];
        for (int tradeIndex = 0; tradeIndex < offers.size(); tradeIndex++) {
            candidatePoolIndices[tradeIndex] = resolveTradeLevel(
                    tradeIndex,
                    offers.size(),
                    careerLevel,
                    trades
            ) - 1;
        }

        UUID sessionId = UUID.randomUUID();
        SelectionSession session = new SelectionSession(
                sessionId,
                villager.getUUID(),
                now,
                new ArrayList<>(offers),
                offers.stream().map(OfferSnapshot::create).toList(),
                candidatePoolIndices,
                candidatePools
        );
        SESSIONS.put(player.getUUID(), session);

        return new PacketOpenTradeSelection(
                sessionId,
                offers,
                candidatePoolIndices,
                candidatePools
        );
    }

    public static void replaceTrade(ServerPlayer player, UUID sessionId, int tradeIndex, int candidateIndex) {
        SelectionSession session = SESSIONS.get(player.getUUID());
        if (session == null || !session.id().equals(sessionId)) {
            return;
        }
        SESSIONS.remove(player.getUUID());

        if (System.currentTimeMillis() - session.createdAtMillis() > SESSION_LIFETIME_MILLIS) {
            return;
        }

        Entity entity = player.serverLevel().getEntity(session.villagerId());
        if (!(entity instanceof Villager villager)
                || !villager.isAlive()
                || player.distanceToSqr(villager) > MAX_REPLACE_DISTANCE_SQUARED
                || !hasSelectionWand(player)) {
            return;
        }

        MerchantOffers currentOffers = villager.getOffers();
        if (tradeIndex < 0
                || tradeIndex >= currentOffers.size()
                || tradeIndex >= session.originalOffers().size()
                || tradeIndex >= session.originalSnapshots().size()
                || tradeIndex >= session.candidatePoolIndices().length) {
            return;
        }

        MerchantOffer currentOffer = currentOffers.get(tradeIndex);
        if (currentOffer != session.originalOffers().get(tradeIndex)
                || !session.originalSnapshots().get(tradeIndex).matches(currentOffer)) {
            return;
        }

        int poolIndex = session.candidatePoolIndices()[tradeIndex];
        if (poolIndex < 0 || poolIndex >= session.candidatePools().size()) {
            return;
        }

        MerchantOffers candidatePool = session.candidatePools().get(poolIndex);
        if (candidateIndex < 0 || candidateIndex >= candidatePool.size()) {
            return;
        }

        MerchantOffer replacement = candidatePool.get(candidateIndex);
        if (!ServerConfig.isLibrarianEnchantedBookSelectionEnabled() && containsEnchantedBook(replacement)) {
            return;
        }

        currentOffers.set(tradeIndex, replacement);
    }

    private static Int2ObjectMap<VillagerTrades.ItemListing[]> getTrades(Villager villager) {
        VillagerProfession profession = villager.getVillagerData().getProfession();
        return VillagerTrades.TRADES.get(profession);
    }

    private static MerchantOffers generateCandidatePool(
            Villager villager,
            Int2ObjectMap<VillagerTrades.ItemListing[]> trades,
            int level,
            boolean allowEnchantedBooks
    ) {
        MerchantOffers candidates = new MerchantOffers();
        if (trades == null) {
            return candidates;
        }

        VillagerTrades.ItemListing[] listings = trades.get(level);
        if (listings == null) {
            return candidates;
        }

        RandomSource random = RandomSource.create();
        boolean enchantedBooksAdded = false;
        for (VillagerTrades.ItemListing listing : listings) {
            try {
                if (listing instanceof VillagerTrades.EnchantBookForEmeralds) {
                    if (allowEnchantedBooks && !enchantedBooksAdded) {
                        MerchantOffer generated = listing.getOffer(villager, random);
                        if (generated != null) {
                            candidates.addAll(getEnchantedBookOffers(villager, random, generated.getXp()));
                        }
                    }
                    enchantedBooksAdded = true;
                    continue;
                }

                MerchantOffer offer = listing.getOffer(villager, random);
                if (offer != null && isValidReplacement(offer, allowEnchantedBooks)) {
                    candidates.add(offer);
                }
            } catch (Exception e) {
                TradeTweaks.LOGGER.error("Failed to generate a level {} trade candidate", level, e);
            }
        }

        try {
            if (allowEnchantedBooks
                    && level == 5
                    && villager.getVillagerData().getProfession() == VillagerProfession.LIBRARIAN
                    && QuarkCompat.isQuarkLoaded()) {
                candidates.addAll(QuarkCompat.getAncientTomeOffers());
            }
        } catch (Exception e) {
            TradeTweaks.LOGGER.error("Failed to generate Quark Ancient Tome trade candidates", e);
        }

        return candidates;
    }

    private static int resolveTradeLevel(
            int tradeIndex,
            int offerCount,
            int careerLevel,
            Int2ObjectMap<VillagerTrades.ItemListing[]> trades
    ) {
        int newerOffers = offerCount - 1 - tradeIndex;
        if (trades != null) {
            for (int level = careerLevel; level >= 1; level--) {
                VillagerTrades.ItemListing[] listings = trades.get(level);
                int unlockedSlots = listings == null ? 0 : Math.min(2, listings.length);
                if (newerOffers < unlockedSlots) {
                    return level;
                }
                newerOffers -= unlockedSlots;
            }
        }

        int newerUnlockGroups = (offerCount - 1 - tradeIndex) / 2;
        return Math.max(1, careerLevel - newerUnlockGroups);
    }

    private static boolean hasSelectionWand(ServerPlayer player) {
        return isSelectionWand(player.getMainHandItem()) || isSelectionWand(player.getOffhandItem());
    }

    private static boolean isSelectionWand(ItemStack stack) {
        return stack.getItem() instanceof EmeraldWand
                && EmeraldWand.isUpgraded(stack)
                && EmeraldWand.getMode(stack) == EmeraldWand.WandMode.SELECT_MODE;
    }

    private static boolean isValidReplacement(MerchantOffer replacement, boolean allowEnchantedBooks) {
        return allowEnchantedBooks || !containsEnchantedBook(replacement);
    }

    private static boolean containsEnchantedBook(MerchantOffer offer) {
        return isEnchantedBookStack(offer.getCostA())
                || isEnchantedBookStack(offer.getCostB())
                || isEnchantedBookStack(offer.getResult());
    }

    private static boolean isEnchantedBookStack(ItemStack stack) {
        return stack != null && stack.is(Items.ENCHANTED_BOOK);
    }

    private static List<MerchantOffer> getEnchantedBookOffers(Entity trader, RandomSource random, int villagerXp) {
        List<Enchantment> tradableEnchantments = BuiltInRegistries.ENCHANTMENT.stream()
                .filter(Enchantment::isTradeable)
                .toList();
        List<MerchantOffer> offers = new ArrayList<>();

        for (Enchantment enchantment : tradableEnchantments) {
            int maxLevel = enchantment.getMaxLevel();
            ItemStack enchantedBook = EnchantedBookItem.createForEnchantment(
                    new EnchantmentInstance(enchantment, maxLevel)
            );
            int priceBound = 5 + maxLevel * 10;
            int price = 2 + 3 * maxLevel + Math.min(
                    Math.min(random.nextInt(priceBound), random.nextInt(priceBound)),
                    random.nextInt(priceBound)
            );

            if (enchantment.isTreasureOnly()) {
                price *= 2;
            }
            price = Math.min(price, 64);

            offers.add(new MerchantOffer(
                    new ItemStack(Items.EMERALD, price),
                    new ItemStack(Items.BOOK),
                    enchantedBook,
                    12,
                    villagerXp,
                    0.2F
            ));
        }
        return offers;
    }

    private record SelectionSession(
            UUID id,
            UUID villagerId,
            long createdAtMillis,
            List<MerchantOffer> originalOffers,
            List<OfferSnapshot> originalSnapshots,
            int[] candidatePoolIndices,
            List<MerchantOffers> candidatePools
    ) {
    }

    private record OfferSnapshot(
            ItemStack baseCostA,
            ItemStack costB,
            ItemStack result,
            int uses,
            int maxUses,
            int xp,
            int specialPrice,
            int demand,
            float priceMultiplier,
            boolean rewardExperience
    ) {
        private static OfferSnapshot create(MerchantOffer offer) {
            return new OfferSnapshot(
                    offer.getBaseCostA().copy(),
                    offer.getCostB().copy(),
                    offer.getResult().copy(),
                    offer.getUses(),
                    offer.getMaxUses(),
                    offer.getXp(),
                    offer.getSpecialPriceDiff(),
                    offer.getDemand(),
                    offer.getPriceMultiplier(),
                    offer.shouldRewardExp()
            );
        }

        private boolean matches(MerchantOffer offer) {
            return ItemStack.matches(baseCostA, offer.getBaseCostA())
                    && ItemStack.matches(costB, offer.getCostB())
                    && ItemStack.matches(result, offer.getResult())
                    && uses == offer.getUses()
                    && maxUses == offer.getMaxUses()
                    && xp == offer.getXp()
                    && specialPrice == offer.getSpecialPriceDiff()
                    && demand == offer.getDemand()
                    && Float.compare(priceMultiplier, offer.getPriceMultiplier()) == 0
                    && rewardExperience == offer.shouldRewardExp();
        }
    }
}
