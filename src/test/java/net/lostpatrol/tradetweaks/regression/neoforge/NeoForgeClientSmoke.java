/** Registers client startup smoke only on the physical NeoForge client. */
package net.lostpatrol.tradetweaks.regression.neoforge;

import net.lostpatrol.tradetweaks.regression.client.ClientSmoke;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = "tradetweaks", value = Dist.CLIENT)
public final class NeoForgeClientSmoke {
    /** Observes startup without opening or changing any world. */
    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        ClientSmoke.tick(Minecraft.getInstance());
    }
}
