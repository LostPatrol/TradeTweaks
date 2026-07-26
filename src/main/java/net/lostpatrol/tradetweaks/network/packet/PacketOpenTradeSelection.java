package net.lostpatrol.tradetweaks.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.trading.MerchantOffers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PacketOpenTradeSelection {
    private final UUID sessionId;
    private final MerchantOffers offers;
    private final int[] candidatePoolIndices;
    private final List<MerchantOffers> candidatePools;

    public PacketOpenTradeSelection(
            UUID sessionId,
            MerchantOffers offers,
            int[] candidatePoolIndices,
            List<MerchantOffers> candidatePools
    ) {
        this.sessionId = sessionId;
        this.offers = offers;
        this.candidatePoolIndices = candidatePoolIndices;
        this.candidatePools = candidatePools;
    }

    public PacketOpenTradeSelection(FriendlyByteBuf buf) {
        this.sessionId = buf.readUUID();
        this.offers = MerchantOffers.createFromStream(buf);
        this.candidatePoolIndices = new int[buf.readVarInt()];
        for (int i = 0; i < candidatePoolIndices.length; i++) {
            candidatePoolIndices[i] = buf.readVarInt();
        }
        int poolCount = buf.readVarInt();
        this.candidatePools = new ArrayList<>(poolCount);
        for (int i = 0; i < poolCount; i++) {
            candidatePools.add(MerchantOffers.createFromStream(buf));
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(sessionId);
        offers.writeToStream(buf);
        buf.writeVarInt(candidatePoolIndices.length);
        for (int poolIndex : candidatePoolIndices) {
            buf.writeVarInt(poolIndex);
        }
        buf.writeVarInt(candidatePools.size());
        for (MerchantOffers candidatePool : candidatePools) {
            candidatePool.writeToStream(buf);
        }
    }

    public UUID getSessionId() {
        return sessionId;
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
