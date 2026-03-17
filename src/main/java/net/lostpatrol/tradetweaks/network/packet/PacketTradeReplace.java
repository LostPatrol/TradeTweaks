package net.lostpatrol.tradetweaks.network.packet;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public class PacketTradeReplace implements CustomPacketPayload {
    public static final Type<PacketTradeReplace> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TradeTweaks.MODID, "trade_replace"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketTradeReplace> STREAM_CODEC =
            StreamCodec.of((buf, packet) -> packet.encode(buf), PacketTradeReplace::new);

    private final int villagerId;
    private final int tradeIndex;
    private final MerchantOffers replacement;

    public PacketTradeReplace(int villagerId, int tradeIndex, MerchantOffer replacement) {
        this.villagerId = villagerId;
        this.tradeIndex = tradeIndex;
        this.replacement = new MerchantOffers();
        this.replacement.add(replacement);
    }

    public PacketTradeReplace(RegistryFriendlyByteBuf buf) {
        this.villagerId = buf.readInt();
        this.tradeIndex = buf.readInt();
        this.replacement = MerchantOffers.STREAM_CODEC.decode(buf);
    }

    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeInt(villagerId);
        buf.writeInt(tradeIndex);
        MerchantOffers.STREAM_CODEC.encode(buf, replacement);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
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

