/** Connects native Forge events to the existing 1.20.1 gameplay callbacks. */
package net.lostpatrol.tradetweaks.platform;

import net.lostpatrol.tradetweaks.command.InternalCommand;
import net.lostpatrol.tradetweaks.command.TradeBroadcastCommand;
import net.lostpatrol.tradetweaks.common.item.villager.VillagerConversionMemory;
import net.lostpatrol.tradetweaks.common.tradecast.VillagerTradeReporter;
import net.lostpatrol.tradetweaks.common.tradeselect.TradeSelectionSessionManager;
import net.lostpatrol.tradetweaks.config.ClientConfig;
import net.lostpatrol.tradetweaks.config.ServerConfig;
import net.lostpatrol.tradetweaks.events.RaidRewardHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public final class ForgeEvents {
    /** Registers the original event phases and config lifecycle semantics. */
    public static void register(IEventBus modBus) {
        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> {
            InternalCommand.registerCommands(event.getDispatcher());
            TradeBroadcastCommand.registerCommands(event.getDispatcher());
        });
        MinecraftForge.EVENT_BUS.addListener((TickEvent.PlayerTickEvent event) -> {
            if (event.phase == TickEvent.Phase.END) {
                RaidRewardHandler.onPlayerTick(event.player);
                VillagerTradeReporter.onPlayerTick(event.player);
            }
        });
        MinecraftForge.EVENT_BUS.addListener((LivingDeathEvent event) -> VillagerTradeReporter.onVillagerDeath(event.getEntity()));
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ServerTickEvent event) -> {
            if (event.phase == TickEvent.Phase.END) TradeSelectionSessionManager.onServerTick(event.getServer());
        });
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedOutEvent event) -> {
            TradeSelectionSessionManager.onPlayerLoggedOut(event.getEntity());
            RaidRewardHandler.onPlayerLogout(event.getEntity());
        });
        MinecraftForge.EVENT_BUS.addListener((LivingConversionEvent.Post event) -> VillagerConversionMemory.onLivingConversionPost(event.getEntity(), event.getOutcome()));
        // The original config listener observes load, reload and unload events, so keep the base event.
        modBus.addListener((ModConfigEvent event) -> {
            if (event.getConfig().getSpec() == ServerConfig.SPEC) ServerConfig.onLoad();
            if (net.minecraftforge.fml.loading.FMLEnvironment.dist.isClient() && event.getConfig().getSpec() == ClientConfig.SPEC) ClientConfig.onLoad();
        });
        modBus.addListener((ModConfigEvent.Reloading event) -> {
            if (event.getConfig().getSpec() == ServerConfig.SPEC) ServerConfig.onReload();
        });
    }
}
