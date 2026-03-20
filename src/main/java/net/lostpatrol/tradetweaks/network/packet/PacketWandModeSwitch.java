package net.lostpatrol.tradetweaks.network.packet;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;

public class PacketWandModeSwitch implements CustomPacketPayload {
    public static final Type<PacketWandModeSwitch> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TradeTweaks.MODID, "wand_mode_switch"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketWandModeSwitch> STREAM_CODEC =
            StreamCodec.of((buf, packet) -> packet.encode(buf), PacketWandModeSwitch::new);

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

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public boolean isForward() {
        return forward;
    }

    public InteractionHand getHand() {
        return hand;
    }
}

