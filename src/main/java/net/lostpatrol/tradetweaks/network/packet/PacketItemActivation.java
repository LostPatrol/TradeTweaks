package net.lostpatrol.tradetweaks.network.packet;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/*
* Animation of Totem of Village Hero
* */
public class PacketItemActivation implements CustomPacketPayload {
    public static final Type<PacketItemActivation> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TradeTweaks.MODID, "item_activation"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketItemActivation> STREAM_CODEC =
            StreamCodec.of((buf, packet) -> packet.encode(buf), PacketItemActivation::new);

    private final ItemStack stack;

    public PacketItemActivation(ItemStack stack) {
        this.stack = stack.copy();
    }

    public PacketItemActivation(RegistryFriendlyByteBuf buf) {
        this.stack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
    }

    public void encode(RegistryFriendlyByteBuf buf) {
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, stack);
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PacketItemActivation packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            try {
                Class<?> clazz = Class.forName("net.lostpatrol.tradetweaks.client.ClientItemActivationHandler");
                clazz.getDeclaredMethod("display", ItemStack.class).invoke(null, packet.getStack());
            } catch (Exception e) {
                TradeTweaks.LOGGER.error("Failed to display item activation animation", e);
            }
        });
    }
}
