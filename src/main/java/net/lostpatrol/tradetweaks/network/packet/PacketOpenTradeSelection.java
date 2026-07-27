package net.lostpatrol.tradetweaks.network.packet;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.trading.MerchantOffers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PacketOpenTradeSelection implements CustomPacketPayload {
    public static final Type<PacketOpenTradeSelection> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TradeTweaks.MODID, "open_trade_selection"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketOpenTradeSelection> STREAM_CODEC =
            StreamCodec.of((buf, packet) -> packet.encode(buf), PacketOpenTradeSelection::new);

    private final UUID sessionId;
    private final int villagerId;
    private final MerchantOffers offers;
    private final int[] candidatePoolIndices;
    private final List<MerchantOffers> candidatePools;

    public PacketOpenTradeSelection(
            UUID sessionId,
            int villagerId,
            MerchantOffers offers,
            int[] candidatePoolIndices,
            List<MerchantOffers> candidatePools
    ) {
        this.sessionId = sessionId;
        this.villagerId = villagerId;
        this.offers = offers;
        this.candidatePoolIndices = candidatePoolIndices;
        this.candidatePools = candidatePools;
    }

    public PacketOpenTradeSelection(RegistryFriendlyByteBuf buf) {
        this.sessionId = buf.readUUID();
        this.villagerId = buf.readVarInt();
        this.offers = MerchantOffers.STREAM_CODEC.decode(buf);
        this.candidatePoolIndices = new int[buf.readVarInt()];
        for (int i = 0; i < candidatePoolIndices.length; i++) {
            candidatePoolIndices[i] = buf.readVarInt();
        }
        int poolCount = buf.readVarInt();
        this.candidatePools = new ArrayList<>(poolCount);
        for (int i = 0; i < poolCount; i++) {
            candidatePools.add(MerchantOffers.STREAM_CODEC.decode(buf));
        }
    }

    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeUUID(sessionId);
        buf.writeVarInt(villagerId);
        MerchantOffers.STREAM_CODEC.encode(buf, offers);
        buf.writeVarInt(candidatePoolIndices.length);
        for (int poolIndex : candidatePoolIndices) {
            buf.writeVarInt(poolIndex);
        }
        buf.writeVarInt(candidatePools.size());
        for (MerchantOffers candidatePool : candidatePools) {
            MerchantOffers.STREAM_CODEC.encode(buf, candidatePool);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public int getVillagerId() {
        return villagerId;
    }

    public MerchantOffers getOffers() {
        return offers;
    }

    public int[] getCandidatePoolIndices() {
        return candidatePoolIndices;
    }

    public List<MerchantOffers> getCandidatePools() {
        return candidatePools;
    }
}

