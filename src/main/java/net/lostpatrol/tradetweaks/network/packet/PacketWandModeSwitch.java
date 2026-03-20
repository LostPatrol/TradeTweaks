package net.lostpatrol.tradetweaks.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;

public class PacketWandModeSwitch {
    private final boolean forward;
    private final InteractionHand hand;

    public PacketWandModeSwitch(boolean forward, InteractionHand hand) {
        this.forward = forward;
        this.hand = hand;
    }

    public PacketWandModeSwitch(FriendlyByteBuf buf) {
        this.forward = buf.readBoolean();
        this.hand = buf.readEnum(InteractionHand.class);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(forward);
        buf.writeEnum(hand);
    }

    public boolean isForward() {
        return forward;
    }

    public InteractionHand getHand() {
        return hand;
    }
}
