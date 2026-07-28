package net.lostpatrol.tradetweaks.network.packet;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class PacketCloseTradeSelection implements CustomPacketPayload {
    public static final Type<PacketCloseTradeSelection> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TradeTweaks.MODID, "close_trade_selection"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketCloseTradeSelection> STREAM_CODEC =
            StreamCodec.of((buf, packet) -> packet.encode(buf), PacketCloseTradeSelection::new);

    private final UUID sessionId;

    public PacketCloseTradeSelection(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public PacketCloseTradeSelection(RegistryFriendlyByteBuf buf) {
        this.sessionId = buf.readUUID();
    }

    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeUUID(sessionId);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public UUID getSessionId() {
        return sessionId;
    }
}
