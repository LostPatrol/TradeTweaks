/** Connects native client events to shared input and rendering code. */
package net.lostpatrol.tradetweaks.platform;

import net.lostpatrol.tradetweaks.client.events.PropertyRegistry;
import net.lostpatrol.tradetweaks.client.events.WandModeWheelHandler;
import net.lostpatrol.tradetweaks.client.events.WandScrollHandler;
import net.lostpatrol.tradetweaks.network.handler.HandlerBlockHighlight;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public final class NeoForgeClientEvents {
    /** Registers client callbacks without loading them on a dedicated server. */
    public static void register(IEventBus modBus) {
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> {
            HandlerBlockHighlight.onClientTick();
            WandModeWheelHandler.onClientTick();
        });
        NeoForge.EVENT_BUS.addListener((InputEvent.MouseScrollingEvent event) -> {
            if (WandScrollHandler.onMouseScroll(event.getScrollDeltaY())) event.setCanceled(true);
        });
        NeoForge.EVENT_BUS.addListener((RenderLevelStageEvent event) -> {
            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) HandlerBlockHighlight.onRenderWorldLast(event.getPoseStack());
        });
        modBus.addListener((FMLClientSetupEvent event) -> event.enqueueWork(PropertyRegistry::propertyOverrideRegistry));
    }
}
