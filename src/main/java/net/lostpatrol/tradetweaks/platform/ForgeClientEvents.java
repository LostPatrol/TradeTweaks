/** Connects native client events to shared input, properties and highlights. */
package net.lostpatrol.tradetweaks.platform;

import net.lostpatrol.tradetweaks.client.events.PropertyRegistry;
import net.lostpatrol.tradetweaks.client.events.WandModeWheelHandler;
import net.lostpatrol.tradetweaks.client.events.WandScrollHandler;
import net.lostpatrol.tradetweaks.network.handler.HandlerBlockHighlight;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public final class ForgeClientEvents {
    /** Registers callbacks only on a physical client. */
    public static void register(IEventBus modBus) {
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent event) -> {
            if (event.phase == TickEvent.Phase.END) {
                HandlerBlockHighlight.onClientTick();
                WandModeWheelHandler.onClientTick();
            }
        });
        MinecraftForge.EVENT_BUS.addListener((InputEvent.MouseScrollingEvent event) -> {
            if (WandScrollHandler.onMouseScroll(event.getScrollDelta())) event.setCanceled(true);
        });
        MinecraftForge.EVENT_BUS.addListener((RenderLevelStageEvent event) -> {
            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) HandlerBlockHighlight.onRenderWorldLast(event.getPoseStack());
        });
        modBus.addListener((FMLClientSetupEvent event) -> event.enqueueWork(PropertyRegistry::propertyOverrideRegistry));
    }
}
