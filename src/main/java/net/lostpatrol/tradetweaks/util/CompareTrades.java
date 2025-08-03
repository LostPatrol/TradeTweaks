package net.lostpatrol.tradetweaks.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;

public class CompareTrades {
    public static boolean isCounterpartTrade(MerchantOffer a, MerchantOffer b) {
        return isItemEqualIgnoreAllNBT(a.getBaseCostA(), b.getBaseCostA()) &&  // 输入A
                isItemEqualIgnoreAllNBT(a.getCostB(), b.getCostB()) &&          // 输入B
                isItemEqualIgnoreAllNBT(a.getResult(), b.getResult());          // 输出
    }

    private static boolean isItemEqualIgnoreAllNBT(ItemStack stack1, ItemStack stack2) {
        if (stack1.isEmpty() && stack2.isEmpty()) return true;
        if (stack1.isEmpty() || stack2.isEmpty()) return false;

        return ItemStack.isSameItem(stack1, stack2);
    }
}