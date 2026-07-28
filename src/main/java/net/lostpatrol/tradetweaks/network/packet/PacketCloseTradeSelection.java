package net.lostpatrol.tradetweaks.network.packet;

import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class PacketCloseTradeSelection {
    private final UUID sessionId;

    public PacketCloseTradeSelection(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public PacketCloseTradeSelection(FriendlyByteBuf buf) {
        this.sessionId = buf.readUUID();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(sessionId);
    }

    public UUID getSessionId() {
        return sessionId;
    }
}
