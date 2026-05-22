package net.lostpatrol.tradetweaks.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.trading.MerchantOffers;

public class PacketOpenTradeSelection {
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

    public PacketOpenTradeSelection(FriendlyByteBuf buf) {
        this.villagerId = buf.readInt();
        this.offers = MerchantOffers.createFromStream(buf);
        this.level = buf.readInt();
        this.profession = buf.readUtf();
        this.type = buf.readUtf();
        this.librarianEnchantedBookSelectionEnabled = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(villagerId);
        offers.writeToStream(buf);
        buf.writeInt(level);
        buf.writeUtf(profession);
        buf.writeUtf(type);
        buf.writeBoolean(librarianEnchantedBookSelectionEnabled);
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
