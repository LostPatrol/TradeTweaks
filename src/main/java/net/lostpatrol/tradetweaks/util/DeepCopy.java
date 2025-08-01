package net.lostpatrol.tradetweaks.util;

import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public class DeepCopy {
    public static MerchantOffers deepCopyOffers(MerchantOffers original) {
        MerchantOffers copy = new MerchantOffers();
        original.forEach(offer -> copy.add(deepCopyOffer(offer)));
        return copy;
    }

    private static MerchantOffer deepCopyOffer(MerchantOffer original) {
        return new MerchantOffer(
                original.getBaseCostA().copy(),
                original.getCostB().copy(),
                original.getResult().copy(),
                original.getUses(),
                original.getMaxUses(),
                original.getXp(),
                original.getPriceMultiplier(),
                original.getDemand()
        );
    }
}
