package net.lostpatrol.tradetweaks.common.item.villager;

import net.lostpatrol.tradetweaks.advancement.AdvancementEventTrigger;
import net.lostpatrol.tradetweaks.advancement.ModCriteriaTriggers;
import net.lostpatrol.tradetweaks.TradeTweaks;
import net.lostpatrol.tradetweaks.util.VillagerUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;

import javax.annotation.Nonnull;

public class RestockWritItem extends Item {
    private static final String DAY_TAG = TradeTweaks.MODID + "_restock_writ_day";
    private static final String COUNT_TAG = TradeTweaks.MODID + "_restock_writ_count";

    public static final Component RESTOCK_FAIL_NOT_OCCUPIED = Component.translatable("tradetweaks.restock_writ.fail.villager_not_occupied").withStyle(ChatFormatting.RED);
    public static final Component RESTOCK_FAIL_NO_OUT_OF_STOCK_TRADES = Component.translatable("tradetweaks.restock_writ.fail.no_out_of_stock_trades").withStyle(ChatFormatting.RED);
    public static Component RESTOCK_FAIL_INSUFFICIENT_WRITS(int required) {
        return Component.translatable("tradetweaks.restock_writ.fail.insufficient_writs", required).withStyle(ChatFormatting.RED);
    }

    public static Component RESTOCK_SUCCESS(int useCount, int cost) {
        return Component.translatable("tradetweaks.restock_writ.success", useCount, cost).withStyle(ChatFormatting.DARK_GREEN);
    }

    public RestockWritItem(Properties properties) {
        super(properties);
    }

    @Override
    @Nonnull
    public InteractionResult interactLivingEntity(@Nonnull ItemStack stack, @Nonnull Player player,
                                                  @Nonnull LivingEntity target, @Nonnull InteractionHand hand) {
        if (!(target instanceof Villager villager) || !player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        if (player.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (VillagerUtil.isTradingWithOtherPlayer(villager, player)) {
            VillagerUtil.refuseInteraction(villager);
            return InteractionResult.SUCCESS;
        }

        if (VillagerUtil.isUnemployed(villager) || VillagerUtil.isNitwit(villager)) {
            player.displayClientMessage(RESTOCK_FAIL_NOT_OCCUPIED, true);
            return InteractionResult.SUCCESS;
        }

        if (!hasOutOfStockTrade(villager)) {
            player.displayClientMessage(RESTOCK_FAIL_NO_OUT_OF_STOCK_TRADES, true);
            return InteractionResult.SUCCESS;
        }

        int useCount = getUseCountToday(villager) + 1;
        if (!player.getAbilities().instabuild && stack.getCount() < useCount) {
            player.displayClientMessage(RESTOCK_FAIL_INSUFFICIENT_WRITS(useCount), true);
            return InteractionResult.SUCCESS;
        }

        villager.restock();
        setUseCountToday(villager, useCount);
        if (!player.getAbilities().instabuild) {
            stack.shrink(useCount);
        }

        player.displayClientMessage(RESTOCK_SUCCESS(useCount, useCount), true);
        player.awardStat(Stats.ITEM_USED.get(this));
        ModCriteriaTriggers.trigger(player, AdvancementEventTrigger.Event.RESTOCK);
        player.swing(hand, true);
        villager.level().broadcastEntityEvent(villager, (byte) 14);
        VillagerItemEffects.playSound((ServerLevel) player.level(), villager, SoundEvents.VILLAGER_YES);
        return InteractionResult.SUCCESS;
    }

    private static boolean hasOutOfStockTrade(Villager villager) {
        for (MerchantOffer offer : villager.getOffers()) {
            if (offer.isOutOfStock()) {
                return true;
            }
        }
        return false;
    }

    private static int getUseCountToday(Villager villager) {
        CompoundTag data = villager.getPersistentData();
        long currentDay = villager.level().getDayTime() / 24000L;
        if (data.getLong(DAY_TAG) != currentDay) {
            return 0;
        }
        return data.getInt(COUNT_TAG);
    }

    private static void setUseCountToday(Villager villager, int count) {
        CompoundTag data = villager.getPersistentData();
        data.putLong(DAY_TAG, villager.level().getDayTime() / 24000L);
        data.putInt(COUNT_TAG, count);
    }
}
