/** Wires Fabric client input, payloads, model predicates and world highlights. */
package net.lostpatrol.tradetweaks.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.lostpatrol.tradetweaks.client.events.ClientKeyMappings;
import net.lostpatrol.tradetweaks.client.events.PropertyRegistry;
import net.lostpatrol.tradetweaks.client.events.WandModeWheelHandler;
import net.lostpatrol.tradetweaks.network.PayloadContext;
import net.lostpatrol.tradetweaks.network.handler.HandlerBlockHighlight;
import net.lostpatrol.tradetweaks.network.handler.HandlerOpenTradeSelection;
import net.lostpatrol.tradetweaks.network.packet.PacketBlockHighlight;
import net.lostpatrol.tradetweaks.network.packet.PacketItemActivation;
import net.lostpatrol.tradetweaks.network.packet.PacketOpenTradeSelection;

public final class FabricClient implements ClientModInitializer {
    /** Initializes callbacks only on physical clients. */
    @Override public void onInitializeClient() {
        ClientKeyMappings.register();
        PropertyRegistry.propertyOverrideRegistry();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            HandlerBlockHighlight.onClientTick();
            WandModeWheelHandler.onClientTick();
        });
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> HandlerBlockHighlight.onRenderWorldLast(context.matrixStack()));
        ClientPlayNetworking.registerGlobalReceiver(PacketBlockHighlight.TYPE, (packet, ctx) -> HandlerBlockHighlight.handle(packet, new PayloadContext(ctx.player(), ctx.client()::execute)));
        ClientPlayNetworking.registerGlobalReceiver(PacketOpenTradeSelection.TYPE, (packet, ctx) -> HandlerOpenTradeSelection.handle(packet, new PayloadContext(ctx.player(), ctx.client()::execute)));
        ClientPlayNetworking.registerGlobalReceiver(PacketItemActivation.TYPE, (packet, ctx) -> PacketItemActivation.handle(packet, new PayloadContext(ctx.player(), ctx.client()::execute)));
    }
}
