/** Connects NeoForge server events to shared gameplay callbacks. */
package net.lostpatrol.tradetweaks.platform;

import net.lostpatrol.tradetweaks.command.InternalCommand;
import net.lostpatrol.tradetweaks.command.TradeBroadcastCommand;
import net.lostpatrol.tradetweaks.common.item.villager.VillagerConversionMemory;
import net.lostpatrol.tradetweaks.common.tradecast.VillagerTradeReporter;
import net.lostpatrol.tradetweaks.common.tradeselect.TradeSelectionSessionManager;
import net.lostpatrol.tradetweaks.config.ClientConfig;
import net.lostpatrol.tradetweaks.config.ServerConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingConversionEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class NeoForgeEvents {
    /** Registers native lifecycle callbacks once. */
    public static void register(IEventBus modBus) {
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> {
            InternalCommand.registerCommands(event.getDispatcher());
            TradeBroadcastCommand.registerCommands(event.getDispatcher());
        });
        NeoForge.EVENT_BUS.addListener((PlayerTickEvent.Post event) -> VillagerTradeReporter.onPlayerTick(event.getEntity()));
        NeoForge.EVENT_BUS.addListener((LivingDeathEvent event) -> VillagerTradeReporter.onVillagerDeath(event.getEntity()));
        NeoForge.EVENT_BUS.addListener((ServerTickEvent.Post event) -> TradeSelectionSessionManager.onServerTick(event.getServer()));
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedOutEvent event) -> TradeSelectionSessionManager.onPlayerLoggedOut(event.getEntity()));
        NeoForge.EVENT_BUS.addListener((LivingConversionEvent.Post event) -> VillagerConversionMemory.onLivingConversionPost(event.getEntity(), event.getOutcome()));
        modBus.addListener((ModConfigEvent.Loading event) -> {
            if (event.getConfig().getSpec() == ServerConfig.SPEC) ServerConfig.onLoad();
            if (net.neoforged.fml.loading.FMLEnvironment.dist.isClient() && event.getConfig().getSpec() == ClientConfig.SPEC) ClientConfig.onLoad();
        });
        modBus.addListener((ModConfigEvent.Reloading event) -> {
            if (event.getConfig().getSpec() == ServerConfig.SPEC) ServerConfig.onReload();
            if (net.neoforged.fml.loading.FMLEnvironment.dist.isClient() && event.getConfig().getSpec() == ClientConfig.SPEC) ClientConfig.onReload();
        });
    }
}
