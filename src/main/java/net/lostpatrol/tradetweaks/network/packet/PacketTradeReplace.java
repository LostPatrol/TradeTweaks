package net.lostpatrol.tradetweaks.network.packet;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class PacketTradeReplace implements CustomPacketPayload {
    public static final Type<PacketTradeReplace> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TradeTweaks.MODID, "trade_replace"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketTradeReplace> STREAM_CODEC =
            StreamCodec.of((buf, packet) -> packet.encode(buf), PacketTradeReplace::new);

    private final UUID sessionId;
    private final int tradeIndex;
    private final int candidateIndex;

    public PacketTradeReplace(UUID sessionId, int tradeIndex, int candidateIndex) {
        this.sessionId = sessionId;
        this.tradeIndex = tradeIndex;
        this.candidateIndex = candidateIndex;
    }

    public PacketTradeReplace(RegistryFriendlyByteBuf buf) {
        this.sessionId = buf.readUUID();
        this.tradeIndex = buf.readVarInt();
        this.candidateIndex = buf.readVarInt();
    }

    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeUUID(sessionId);
        buf.writeVarInt(tradeIndex);
        buf.writeVarInt(candidateIndex);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
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

