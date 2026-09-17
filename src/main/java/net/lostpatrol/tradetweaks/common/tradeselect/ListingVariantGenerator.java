// Enumerates registered trade variants while preserving Quark tome enchantments.
package net.lostpatrol.tradetweaks.common.tradeselect;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.lostpatrol.tradetweaks.integrations.QuarkCompat;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class ListingVariantGenerator {
    // Stop ordinary listings after this many unchanged probes.
    private static final int STABLE_PROBES = 32;
    // Hard cap on calls made for any registered listing.
    private static final int MAX_PROBES = 16_384;

    private ListingVariantGenerator() {
    }

    /** Reuses the listing itself so its configuration and payment rules remain authoritative. */
    static List<MerchantOffer> generate(VillagerTrades.ItemListing listing, Entity trader, int level) {
        Map<OfferItemIdentity, MerchantOffer> variants = new LinkedHashMap<>();
        Exception firstFailure = null;
        // Tome listings select one enchantment through a single bounded random call.
        boolean tomeListing = false;

        try {
            MerchantOffer initial = listing.getOffer(trader, RandomSource.create());
            tomeListing = QuarkCompat.isAncientTomeOffer(initial);
            addVariant(variants, initial);
        } catch (Exception e) {
            firstFailure = e;
        }

        int probeLimit = tomeListing ? MAX_PROBES : Math.min(
                MAX_PROBES,
                Math.max(1, BuiltInRegistries.ITEM.size())
        );
        int maximumObservedBound = 1;
        int probesWithoutNewVariant = 0;

        for (int selector = 0; selector < probeLimit; selector++) {
            EnumeratingRandomSource random = new EnumeratingRandomSource(selector);
            boolean added = false;
            try {
                MerchantOffer offer = listing.getOffer(trader, random);
                if (QuarkCompat.isAncientTomeOffer(offer)) {
                    tomeListing = true;
                    probeLimit = MAX_PROBES;
                }
                added = addVariant(variants, offer);
            } catch (Exception e) {
                if (firstFailure == null) {
                    firstFailure = e;
                }
            }

            maximumObservedBound = Math.max(maximumObservedBound, random.maximumObservedBound());
            probesWithoutNewVariant = added ? 0 : probesWithoutNewVariant + 1;

            int completedProbes = selector + 1;
            int requiredProbes = Math.min(
                    probeLimit,
                    maximumObservedBound
            );
            if (completedProbes >= requiredProbes
                    && (tomeListing || variants.size() <= 1 || probesWithoutNewVariant >= STABLE_PROBES)) {
                break;
            }
        }

        if (tomeListing && maximumObservedBound > MAX_PROBES) {
            TradeTweaks.LOGGER.warn("Quark tome candidates are incomplete: {} variants exceed the {} probe limit",
                    maximumObservedBound, MAX_PROBES);
        }
        if (firstFailure != null) {
            TradeTweaks.LOGGER.error("Failed to generate one or more level {} trade variants", level, firstFailure);
        }
        return new ArrayList<>(variants.values());
    }

    /** Adds a distinct candidate while retaining the upstream offer unchanged. */
    private static boolean addVariant(
            Map<OfferItemIdentity, MerchantOffer> variants,
            MerchantOffer offer
    ) {
        if (offer == null) {
            return false;
        }
        return variants.putIfAbsent(OfferItemIdentity.create(offer), offer) == null;
    }

    private record OfferItemIdentity(Item costA, Item costB, Item result, List<?> tomeData) {
        /** Only tome exchanges include stack data in their identity. */
        private static OfferItemIdentity create(MerchantOffer offer) {
            return new OfferItemIdentity(
                    offer.getCostA().getItem(),
                    offer.getCostB().getItem(),
                    offer.getResult().getItem(),
                    QuarkCompat.variantData(offer)
            );
        }
    }

    private static final class EnumeratingRandomSource implements RandomSource {
        private final RandomSource delegate;
        private final int selector;
        private final BoundTracker tracker;

        private EnumeratingRandomSource(int selector) {
            this(
                    RandomSource.create(0x5DEECE66DL ^ (long) selector * 0x9E3779B97F4A7C15L),
                    selector,
                    new BoundTracker()
            );
        }

        private EnumeratingRandomSource(RandomSource delegate, int selector, BoundTracker tracker) {
            this.delegate = delegate;
            this.selector = selector;
            this.tracker = tracker;
        }

        private int maximumObservedBound() {
            return tracker.maximumObservedBound;
        }

        @Override
        public RandomSource fork() {
            return new EnumeratingRandomSource(delegate.fork(), selector, tracker);
        }

        @Override
        public PositionalRandomFactory forkPositional() {
            return delegate.forkPositional();
        }

        @Override
        public void setSeed(long seed) {
            delegate.setSeed(seed);
        }

        @Override
        public int nextInt() {
            return delegate.nextInt();
        }

        @Override
        public int nextInt(int bound) {
            if (bound <= 0) {
                return delegate.nextInt(bound);
            }
            tracker.maximumObservedBound = Math.max(tracker.maximumObservedBound, bound);
            long value = (long) selector + (long) tracker.boundedCalls++ * 31L;
            return (int) Math.floorMod(value, bound);
        }

        @Override
        public long nextLong() {
            return delegate.nextLong();
        }

        @Override
        public boolean nextBoolean() {
            return nextInt(2) == 0;
        }

        @Override
        public float nextFloat() {
            return delegate.nextFloat();
        }

        @Override
        public double nextDouble() {
            return delegate.nextDouble();
        }

        @Override
        public double nextGaussian() {
            return delegate.nextGaussian();
        }
    }

    private static final class BoundTracker {
        private int maximumObservedBound = 1;
        private int boundedCalls;
    }
}
