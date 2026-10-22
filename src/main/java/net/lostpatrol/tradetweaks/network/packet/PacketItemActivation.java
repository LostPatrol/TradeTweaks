/** Encodes hero-totem effects and dispatches their client animation. */
package net.lostpatrol.tradetweaks.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.lostpatrol.tradetweaks.TradeTweaks;
import net.lostpatrol.tradetweaks.network.PayloadContext;

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

    /** Dispatches the animation without linking client classes on servers. */
    public static void handle(PacketItemActivation packet, PayloadContext context) {
        context.enqueueWork(() -> {
            try {
                Class.forName("net.lostpatrol.tradetweaks.client.ClientItemActivationHandler")
                        .getDeclaredMethod("display", ItemStack.class).invoke(null, packet.getStack());
            } catch (ReflectiveOperationException error) {
                TradeTweaks.LOGGER.error("Failed to display item activation", error);
            }
        });
    }
}
