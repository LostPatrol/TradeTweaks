package net.lostpatrol.tradetweaks.network.packet;

import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class PacketTradeReplace {
    private final UUID sessionId;
    private final int tradeIndex;
    private final int candidateIndex;

    public PacketTradeReplace(UUID sessionId, int tradeIndex, int candidateIndex) {
        this.sessionId = sessionId;
        this.tradeIndex = tradeIndex;
        this.candidateIndex = candidateIndex;
    }

    public PacketTradeReplace(FriendlyByteBuf buf) {
        this.sessionId = buf.readUUID();
        this.tradeIndex = buf.readVarInt();
        this.candidateIndex = buf.readVarInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(sessionId);
        buf.writeVarInt(tradeIndex);
        buf.writeVarInt(candidateIndex);
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public int getTradeIndex() {
        return tradeIndex;
    }

    public int getCandidateIndex() {
        return candidateIndex;
    }
}
