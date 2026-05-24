package net.lostpatrol.tradetweaks.network.packet;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;

public class PacketWandModeSet implements CustomPacketPayload {
    public static final Type<PacketWandModeSet> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TradeTweaks.MODID, "wand_mode_set"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketWandModeSet> STREAM_CODEC =
            StreamCodec.of((buf, packet) -> packet.encode(buf), PacketWandModeSet::new);

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

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public EmeraldWand.WandMode getMode() {
        return mode;
    }

    public InteractionHand getHand() {
        return hand;
    }
}
