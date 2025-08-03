package net.lostpatrol.tradetweaks.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public class PacketTradeReplace {
    private final int villagerId;
    private final int tradeIndex;
    private final MerchantOffers replacement;

    public PacketTradeReplace(int villagerId, int tradeIndex, MerchantOffer replacement) {
        this.villagerId = villagerId;
        this.tradeIndex = tradeIndex;
        this.replacement = new MerchantOffers();
        this.replacement.add(replacement);
    }

    public PacketTradeReplace(FriendlyByteBuf buf) {
        this.villagerId = buf.readInt();
        this.tradeIndex = buf.readInt();
        this.replacement = MerchantOffers.createFromStream(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(villagerId);
        buf.writeInt(tradeIndex);
        replacement.writeToStream(buf);
    }

    public int getVillagerId() {
        return this.villagerId;
    }

    public int getTradeIndex() {
        return this.tradeIndex;
    }

    public MerchantOffers getReplacement() {
        return this.replacement;
    }

    public MerchantOffer getReplacementOffer(){
        return this.replacement.get(0);
    }
}