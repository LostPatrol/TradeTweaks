package net.lostpatrol.tradetweaks.network.packet;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class PacketWandModeSwitch implements CustomPacketPayload {
    public static final Type<PacketWandModeSwitch> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TradeTweaks.MODID, "wand_mode_switch"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketWandModeSwitch> STREAM_CODEC =
            StreamCodec.of((buf, packet) -> packet.encode(buf), PacketWandModeSwitch::new);

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

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public boolean isForward() {
        return forward;
    }
}

