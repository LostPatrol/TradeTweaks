// Identifies Quark tome exchanges without linking to Quark implementation classes.
package net.lostpatrol.tradetweaks.integrations;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.List;

public final class QuarkCompat {
    // Registry identity remains usable when Quark is absent.
    private static final ResourceLocation ANCIENT_TOME = ResourceLocation.fromNamespaceAndPath("quark", "ancient_tome");

    private QuarkCompat() {
        // Utility class.
    }

    /** Recognizes the registered tome + enchanted book -> tome exchange. */
    public static boolean isAncientTomeOffer(MerchantOffer offer) {
        return offer != null
                && ANCIENT_TOME.equals(BuiltInRegistries.ITEM.getKey(offer.getCostA().getItem()))
                && offer.getCostB().is(Items.ENCHANTED_BOOK)
                && ANCIENT_TOME.equals(BuiltInRegistries.ITEM.getKey(offer.getResult().getItem()));
    }

    /** Retains distinct tome enchantments without changing other trade identities. */
    public static List<?> variantData(MerchantOffer offer) {
        return isAncientTomeOffer(offer)
                ? List.of(stackData(offer.getBaseCostA()), stackData(offer.getCostB()), stackData(offer.getResult()))
                : List.of();
    }

    /** Snapshots stack data so generated variants remain stable map keys. */
    private static Object stackData(ItemStack stack) {
        return List.of(stack.getCount(), stack.getComponentsPatch());
    }
}
