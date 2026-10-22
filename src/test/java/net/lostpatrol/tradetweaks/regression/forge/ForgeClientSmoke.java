/** Registers client startup smoke only on the physical Forge client. */
package net.lostpatrol.tradetweaks.regression.forge;

import net.lostpatrol.tradetweaks.regression.client.ClientSmoke;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "tradetweaks_regression", value = Dist.CLIENT)
public final class ForgeClientSmoke {
    /** Observes startup without opening or changing any world. */
    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) ClientSmoke.tick(Minecraft.getInstance());
    }
}
