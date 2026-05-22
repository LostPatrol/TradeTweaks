package net.lostpatrol.tradetweaks.network.packet;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.trading.MerchantOffers;

public class PacketOpenTradeSelection implements CustomPacketPayload {
    public static final Type<PacketOpenTradeSelection> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TradeTweaks.MODID, "open_trade_selection"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketOpenTradeSelection> STREAM_CODEC =
            StreamCodec.of((buf, packet) -> packet.encode(buf), PacketOpenTradeSelection::new);

    private final int villagerId;
    private final MerchantOffers offers;
    private final int level;
    private final String profession;
    private final String type;
    private final boolean librarianEnchantedBookSelectionEnabled;

    public PacketOpenTradeSelection(int villagerId, MerchantOffers offers, int level, String profession, String type,
                                    boolean librarianEnchantedBookSelectionEnabled) {
        this.villagerId = villagerId;
        this.offers = offers;
        this.level = level;
        this.profession = profession;
        this.type = type;
        this.librarianEnchantedBookSelectionEnabled = librarianEnchantedBookSelectionEnabled;
    }

    public PacketOpenTradeSelection(RegistryFriendlyByteBuf buf) {
        this.villagerId = buf.readInt();
        this.offers = MerchantOffers.STREAM_CODEC.decode(buf);
        this.level = buf.readInt();
        this.profession = buf.readUtf();
        this.type = buf.readUtf();
        this.librarianEnchantedBookSelectionEnabled = buf.readBoolean();
    }

    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeInt(villagerId);
        MerchantOffers.STREAM_CODEC.encode(buf, offers);
        buf.writeInt(level);
        buf.writeUtf(profession);
        buf.writeUtf(type);
        buf.writeBoolean(librarianEnchantedBookSelectionEnabled);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public int getVillagerId() {
        return villagerId;
    }

    public MerchantOffers getOffers() {
        return offers;
    }

    public int getProfessionLevel(){
        return level;
    }

    public String getProfessionName(){
        return profession;
    }

    public String getVillagerType(){
        return type;
    }

    public boolean isLibrarianEnchantedBookSelectionEnabled() {
        return librarianEnchantedBookSelectionEnabled;
    }
}

