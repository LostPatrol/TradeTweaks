package net.lostpatrol.tradetweaks.network.packet;

import net.minecraft.network.FriendlyByteBuf;

public class PacketWandModeSwitch {
    private final boolean forward;

    public PacketWandModeSwitch(boolean forward) {
        this.forward = forward;
    }

    public PacketWandModeSwitch(FriendlyByteBuf buf) {
        this.forward = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(forward);
    }

    public boolean isForward() {
        return forward;
    }
}
