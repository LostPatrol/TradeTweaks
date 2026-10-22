/** Initializes native NeoForge registrations and shared gameplay callbacks. */
package net.lostpatrol.tradetweaks;

import com.mojang.logging.LogUtils;
import net.lostpatrol.tradetweaks.advancement.ModCriteriaTriggers;
import net.lostpatrol.tradetweaks.config.ClientConfig;
import net.lostpatrol.tradetweaks.config.ServerConfig;
import net.lostpatrol.tradetweaks.common.item.ModCreativeModeTab;
import net.lostpatrol.tradetweaks.common.item.ModItems;
import net.lostpatrol.tradetweaks.client.events.ClientKeyMappings;
import net.lostpatrol.tradetweaks.events.RaidRewardHandler;
import net.lostpatrol.tradetweaks.events.McaInteractionHandler;
import net.lostpatrol.tradetweaks.events.VillagerToolInteractionHandler;
import net.lostpatrol.tradetweaks.events.WandInteractionHandler;
import net.lostpatrol.tradetweaks.network.NetworkHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(TradeTweaks.MODID)
public class TradeTweaks {
    /** Registry and translation namespace shared by both loaders. */
    public static final String MODID = "tradetweaks";
    /** Shared diagnostic logger. */
    public static final Logger LOGGER = LogUtils.getLogger();

    /** Registers native services and shared gameplay callbacks. */
    public TradeTweaks(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, ClientConfig.SPEC);

        NeoForge.EVENT_BUS.register(WandInteractionHandler.class);
        NeoForge.EVENT_BUS.register(McaInteractionHandler.class);
        NeoForge.EVENT_BUS.register(VillagerToolInteractionHandler.class);
        NeoForge.EVENT_BUS.register(RaidRewardHandler.class);
        if (Dist.CLIENT.equals(net.neoforged.fml.loading.FMLEnvironment.dist)) {
            net.lostpatrol.tradetweaks.platform.NeoForgeClientEvents.register(modEventBus);
        }

        NetworkHandler.register(modEventBus);

        net.lostpatrol.tradetweaks.platform.NeoForgeEvents.register(modEventBus);
        if (Dist.CLIENT.equals(net.neoforged.fml.loading.FMLEnvironment.dist)) {
            modEventBus.register(ClientKeyMappings.class);
        }

        ModItems.register(modEventBus);
        ModCreativeModeTab.register(modEventBus);
        ModCriteriaTriggers.register(modEventBus);
    }
}
