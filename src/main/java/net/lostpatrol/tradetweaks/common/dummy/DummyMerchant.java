package net.lostpatrol.tradetweaks.common.dummy;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class DummyMerchant implements Merchant {
    private final Component displayName;
    private final MerchantOffers offers;
    private final int villagerLevel;
    private final int xp;
    private final boolean showProgress;
    private final boolean canRestock;

    public DummyMerchant(Component title, MerchantOffers offers, int level, int xp, boolean showProgress, boolean canRestock) {
        this.displayName = title;
        this.offers = offers;
        this.villagerLevel = level;
        this.xp = xp;
        this.showProgress = showProgress;
        this.canRestock = canRestock;
    }

    @Override
    public void setTradingPlayer(@Nullable Player player) {

    }

    @Nullable
    @Override
    public Player getTradingPlayer() {
        return null;
    }

    @Override public MerchantOffers getOffers() { return offers; }

    @Override
    public void overrideOffers(MerchantOffers merchantOffers) {

    }

    @Override
    public void notifyTrade(MerchantOffer merchantOffer) {

    }

    @Override
    public void notifyTradeUpdated(ItemStack itemStack) {

    }

    @Override public int getVillagerXp() { return xp; }

    @Override
    public void overrideXp(int i) {

    }

    @Override public boolean showProgressBar() { return showProgress; }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return null;
    }

    @Override public boolean canRestock() { return canRestock; }

    @Override
    public boolean isClientSide() {
        return false;
    }
}