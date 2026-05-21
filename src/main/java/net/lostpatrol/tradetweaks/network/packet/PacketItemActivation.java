package net.lostpatrol.tradetweaks.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/*
* Animation of Totem of Village Hero
* */
public class PacketItemActivation {
    private final ItemStack stack;

    public PacketItemActivation(ItemStack stack) {
        this.stack = stack.copy();
    }

    public PacketItemActivation(FriendlyByteBuf buf) {
        this.stack = buf.readItem();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeItem(stack);
    }

    public ItemStack getStack() {
        return stack;
    }

    public static void handle(PacketItemActivation packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleClient(packet));
            }
        });
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(PacketItemActivation packet) {
        Minecraft.getInstance().gameRenderer.displayItemActivation(packet.getStack());
    }
}
