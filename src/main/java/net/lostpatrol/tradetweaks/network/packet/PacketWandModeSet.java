package net.lostpatrol.tradetweaks.network.packet;

import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;

public class PacketWandModeSet {
    private final EmeraldWand.WandMode mode;
    private final InteractionHand hand;

    public PacketWandModeSet(EmeraldWand.WandMode mode, InteractionHand hand) {
        this.mode = mode;
        this.hand = hand;
    }

    public PacketWandModeSet(FriendlyByteBuf buf) {
        this.mode = buf.readEnum(EmeraldWand.WandMode.class);
        this.hand = buf.readEnum(InteractionHand.class);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(mode);
        buf.writeEnum(hand);
    }

    public EmeraldWand.WandMode getMode() {
        return mode;
    }

    public InteractionHand getHand() {
        return hand;
    }
}
