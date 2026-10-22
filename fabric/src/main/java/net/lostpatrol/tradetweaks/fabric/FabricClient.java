/** Connects Fabric client packets, input, model properties and workstation outlines. */
package net.lostpatrol.tradetweaks.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.lostpatrol.tradetweaks.client.events.ClientKeyMappings;
import net.lostpatrol.tradetweaks.client.events.PropertyRegistry;
import net.lostpatrol.tradetweaks.client.events.WandModeWheelHandler;
import net.lostpatrol.tradetweaks.network.NetworkHandler;
import net.lostpatrol.tradetweaks.network.PayloadContext;
import net.lostpatrol.tradetweaks.network.handler.HandlerBlockHighlight;
import net.lostpatrol.tradetweaks.network.handler.HandlerOpenTradeSelection;
import net.lostpatrol.tradetweaks.network.packet.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class FabricClient implements ClientModInitializer {
    /** Registers callbacks solely on the physical client. */
    @Override public void onInitializeClient() {
        ClientKeyMappings.register();
        PropertyRegistry.propertyOverrideRegistry();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            HandlerBlockHighlight.onClientTick();
            WandModeWheelHandler.onClientTick();
        });
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> HandlerBlockHighlight.onRenderWorldLast(context.matrixStack()));
        receive(NetworkHandler.HIGHLIGHT, PacketBlockHighlight::new, HandlerBlockHighlight::handle);
        receive(NetworkHandler.TRADE_OPEN, PacketOpenTradeSelection::new, HandlerOpenTradeSelection::handle);
        receive(NetworkHandler.ITEM_ACTIVATION, PacketItemActivation::new, PacketItemActivation::handle);
    }

    /** Decodes while the network buffer is valid, then runs work on the client thread. */
    private static <T> void receive(ResourceLocation id, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, PayloadContext> handler) {
        ClientPlayNetworking.registerGlobalReceiver(id, (client, listener, buffer, response) -> {
            T packet = decoder.apply(buffer);
            handler.accept(packet, new PayloadContext(null, client::execute));
        });
    }
}
