package net.lostpatrol.tradetweaks.common.tradeselect;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.lostpatrol.tradetweaks.TradeTweaks;
import net.lostpatrol.tradetweaks.advancement.AdvancementEventTrigger;
import net.lostpatrol.tradetweaks.advancement.ModCriteriaTriggers;
import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.lostpatrol.tradetweaks.config.ServerConfig;
import net.lostpatrol.tradetweaks.integrations.QuarkCompat;
import net.lostpatrol.tradetweaks.network.packet.PacketOpenTradeSelection;
import net.lostpatrol.tradetweaks.util.VillagerUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class TradeSelectionSessionManager {
    private static final long SESSION_LIFETIME_MILLIS = 120_000L;
    private static final double MAX_REPLACE_DISTANCE_SQUARED = 64.0D;
    private static final Map<UUID, SelectionSession> SESSIONS = new HashMap<>();

    private TradeSelectionSessionManager() {
    }

    @Nullable
    public static PacketOpenTradeSelection createSession(ServerPlayer player, Villager villager) {
        long now = System.currentTimeMillis();
        cleanupSessions(player.getServer(), now);

        SelectionSession previousSession = SESSIONS.remove(player.getUUID());
        releaseVillager(player.getServer(), player.getUUID(), previousSession);
        if (villager.isTrading()) {
            VillagerUtil.refuseInteraction(villager);
            return null;
        }

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
        villager.setTradingPlayer(player);
        villager.getNavigation().stop();
        SESSIONS.put(player.getUUID(), session);

        return new PacketOpenTradeSelection(
                sessionId,
                villager.getId(),
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
        try {
            if (System.currentTimeMillis() - session.createdAtMillis() > SESSION_LIFETIME_MILLIS) {
                return;
            }

            Entity entity = player.serverLevel().getEntity(session.villagerId());
            if (!(entity instanceof Villager villager)
                    || !villager.isAlive()
                    || !isSessionOwner(villager, player.getUUID())
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
            ModCriteriaTriggers.trigger(player, AdvancementEventTrigger.Event.REPLACE_TRADE);
            if (villager.getVillagerData().getProfession() == VillagerProfession.LIBRARIAN
                    && replacement.getResult().is(Items.ENCHANTED_BOOK)) {
                ModCriteriaTriggers.trigger(player, AdvancementEventTrigger.Event.SELECT_ENCHANTED_BOOK);
            }
        } finally {
            if (SESSIONS.remove(player.getUUID(), session)) {
                releaseVillager(player.getServer(), player.getUUID(), session);
            }
        }
    }

    public static void closeSession(ServerPlayer player, UUID sessionId) {
        SelectionSession session = SESSIONS.get(player.getUUID());
        if (session != null
                && session.id().equals(sessionId)
                && SESSIONS.remove(player.getUUID(), session)) {
            releaseVillager(player.getServer(), player.getUUID(), session);
        }
    }

    public static boolean isSelectionLocked(Villager villager) {
        Player tradingPlayer = villager.getTradingPlayer();
        if (tradingPlayer == null) {
            return false;
        }
        SelectionSession session = SESSIONS.get(tradingPlayer.getUUID());
        return session != null && session.villagerId().equals(villager.getUUID());
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (event.getServer().getTickCount() % 20 == 0) {
            cleanupSessions(event.getServer(), System.currentTimeMillis());
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SelectionSession session = SESSIONS.remove(player.getUUID());
            releaseVillager(player.getServer(), player.getUUID(), session);
        }
    }

    private static void cleanupSessions(MinecraftServer server, long now) {
        Iterator<Map.Entry<UUID, SelectionSession>> iterator = SESSIONS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, SelectionSession> entry = iterator.next();
            UUID playerId = entry.getKey();
            SelectionSession session = entry.getValue();
            ServerPlayer player = server.getPlayerList().getPlayer(playerId);
            Villager villager = findVillager(server, session.villagerId());
            boolean invalid = now - session.createdAtMillis() > SESSION_LIFETIME_MILLIS
                    || player == null
                    || villager == null
                    || !villager.isAlive()
                    || player.level() != villager.level()
                    || player.distanceToSqr(villager) > MAX_REPLACE_DISTANCE_SQUARED
                    || !isSessionOwner(villager, playerId);
            if (invalid) {
                iterator.remove();
                releaseVillager(villager, playerId);
            }
        }
    }

    @Nullable
    private static Villager findVillager(MinecraftServer server, UUID villagerId) {
        for (ServerLevel level : server.getAllLevels()) {
            Entity entity = level.getEntity(villagerId);
            if (entity instanceof Villager villager) {
                return villager;
            }
        }
        return null;
    }

    private static void releaseVillager(
            MinecraftServer server,
            UUID playerId,
            @Nullable SelectionSession session
    ) {
        if (session != null) {
            releaseVillager(findVillager(server, session.villagerId()), playerId);
        }
    }

    private static void releaseVillager(@Nullable Villager villager, UUID playerId) {
        if (villager != null && isSessionOwner(villager, playerId)) {
            villager.setTradingPlayer(null);
        }
    }

    private static boolean isSessionOwner(Villager villager, UUID playerId) {
        Player tradingPlayer = villager.getTradingPlayer();
        return tradingPlayer != null && tradingPlayer.getUUID().equals(playerId);
    }

    private static Int2ObjectMap<VillagerTrades.ItemListing[]> getTrades(Villager villager) {
        VillagerProfession profession = villager.getVillagerData().getProfession();
        if (villager.level().enabledFeatures().contains(FeatureFlags.TRADE_REBALANCE)) {
            Int2ObjectMap<VillagerTrades.ItemListing[]> experimental = VillagerTrades.EXPERIMENTAL_TRADES.get(profession);
            if (experimental != null) {
                return experimental;
            }
        }
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

                for (MerchantOffer offer : ListingVariantGenerator.generate(listing, villager, level)) {
                    if (isValidReplacement(offer, allowEnchantedBooks)) {
                        candidates.add(offer);
                    }
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
                candidates.addAll(QuarkCompat.getAncientTomeOffers(villager.level().registryAccess()));
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
        Registry<Enchantment> registry = trader.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        List<? extends Holder<Enchantment>> tradableEnchantments = registry.holders()
                .filter(holder -> holder.is(EnchantmentTags.TRADEABLE))
                .toList();
        List<MerchantOffer> offers = new ArrayList<>();

        for (Holder<Enchantment> enchantment : tradableEnchantments) {
            int maxLevel = enchantment.value().getMaxLevel();
            ItemStack enchantedBook = EnchantedBookItem.createForEnchantment(
                    new EnchantmentInstance(enchantment, maxLevel)
            );
            int priceBound = 5 + maxLevel * 10;
            int price = 2 + 3 * maxLevel + Math.min(
                    Math.min(random.nextInt(priceBound), random.nextInt(priceBound)),
                    random.nextInt(priceBound)
            );

            if (enchantment.is(EnchantmentTags.TREASURE)) {
                price *= 2;
            }
            price = Math.min(price, 64);

            offers.add(new MerchantOffer(
                    new ItemCost(Items.EMERALD, price),
                    Optional.of(new ItemCost(Items.BOOK, 1)),
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
