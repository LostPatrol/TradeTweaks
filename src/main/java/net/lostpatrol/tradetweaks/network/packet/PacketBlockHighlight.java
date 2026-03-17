package net.lostpatrol.tradetweaks.network.packet;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class PacketBlockHighlight implements CustomPacketPayload {
    public static final Type<PacketBlockHighlight> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TradeTweaks.MODID, "block_highlight"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketBlockHighlight> STREAM_CODEC =
            StreamCodec.of((buf, packet) -> packet.encode(buf), PacketBlockHighlight::new);

    private final BlockPos pos;
    private final int durationTicks;
    private final float red, green, blue;

    public PacketBlockHighlight(BlockPos pos, int durationTicks, float red, float green, float blue) {
        this.pos = pos;
        this.durationTicks = durationTicks;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public PacketBlockHighlight(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.durationTicks = buf.readInt();
        this.red = buf.readFloat();
        this.green = buf.readFloat();
        this.blue = buf.readFloat();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(durationTicks);
        buf.writeFloat(red);
        buf.writeFloat(green);
        buf.writeFloat(blue);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getDurationTicks() {
        return durationTicks;
    }

    public float getRed() {
        return red;
    }

    public float getGreen() {
        return green;
    }

    public float getBlue() {
        return blue;
    }
}

