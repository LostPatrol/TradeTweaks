/** Initializes native Forge registrations and shared gameplay callbacks. */
package net.lostpatrol.tradetweaks;

import com.mojang.logging.LogUtils;
import net.lostpatrol.tradetweaks.advancement.ModCriteriaTriggers;
import net.lostpatrol.tradetweaks.command.InternalCommand;
import net.lostpatrol.tradetweaks.command.TradeBroadcastCommand;
import net.lostpatrol.tradetweaks.common.tradecast.VillagerTradeReporter;
import net.lostpatrol.tradetweaks.common.tradeselect.TradeSelectionSessionManager;
import net.lostpatrol.tradetweaks.config.ClientConfig;
import net.lostpatrol.tradetweaks.config.ServerConfig;
import net.lostpatrol.tradetweaks.common.item.ModCreativeModeTab;
import net.lostpatrol.tradetweaks.common.item.ModItems;
import net.lostpatrol.tradetweaks.network.handler.HandlerBlockHighlight;
import net.lostpatrol.tradetweaks.network.NetworkHandler;
import net.lostpatrol.tradetweaks.network.handler.HandlerOpenTradeSelection;
import net.lostpatrol.tradetweaks.events.RaidRewardHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(TradeTweaks.MODID)
public class TradeTweaks {
    /** Registry and translation namespace shared by both loaders. */
    public static final String MODID = "tradetweaks";
    /** Shared diagnostic logger. */
    public static final Logger LOGGER = LogUtils.getLogger();

    /** Registers native services and the original gameplay event phases. */
    public TradeTweaks() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ClientConfig.SPEC);

        MinecraftForge.EVENT_BUS.register(this);
        net.lostpatrol.tradetweaks.platform.ForgeEvents.register(modEventBus);
        if (FMLEnvironment.dist == Dist.CLIENT){
            net.lostpatrol.tradetweaks.platform.ForgeClientEvents.register(modEventBus);
        }

        NetworkHandler.register();

        ModItems.register(modEventBus);
        ModCreativeModeTab.register(modEventBus);
        ModCriteriaTriggers.register();
    }
}
