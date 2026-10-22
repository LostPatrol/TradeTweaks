/** Initializes Fabric around the existing 1.20.1 gameplay and configuration model. */
package net.lostpatrol.tradetweaks;

import com.mojang.logging.LogUtils;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.lostpatrol.tradetweaks.advancement.ModCriteriaTriggers;
import net.lostpatrol.tradetweaks.command.InternalCommand;
import net.lostpatrol.tradetweaks.command.TradeBroadcastCommand;
import net.lostpatrol.tradetweaks.common.item.ModCreativeModeTab;
import net.lostpatrol.tradetweaks.common.item.ModItems;
import net.lostpatrol.tradetweaks.common.tradecast.VillagerTradeReporter;
import net.lostpatrol.tradetweaks.common.tradeselect.TradeSelectionSessionManager;
import net.lostpatrol.tradetweaks.config.ClientConfig;
import net.lostpatrol.tradetweaks.config.ServerConfig;
import net.lostpatrol.tradetweaks.events.RaidRewardHandler;
import net.lostpatrol.tradetweaks.fabric.FabricInteractions;
import net.lostpatrol.tradetweaks.network.NetworkHandler;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

public final class TradeTweaks implements ModInitializer {
    /** Shared registry and translation namespace. */
    public static final String MODID = "tradetweaks";
    /** Shared diagnostic logger. */
    public static final Logger LOGGER = LogUtils.getLogger();

    /** Installs shared registrations before worlds and datapacks load. */
    @Override public void onInitialize() {
        ModItems.register();
        ModCreativeModeTab.register();
        ModCriteriaTriggers.register();
        ModConfigEvents.loading(MODID).register(TradeTweaks::onConfigEvent);
        ModConfigEvents.unloading(MODID).register(TradeTweaks::onConfigEvent);
        ModConfigEvents.reloading(MODID).register(config -> {
            onConfigEvent(config);
            if (config.getSpec() == ServerConfig.SPEC) ServerConfig.onReload();
        });
        ForgeConfigRegistry.INSTANCE.register(MODID, ModConfig.Type.SERVER, ServerConfig.SPEC);
        ForgeConfigRegistry.INSTANCE.register(MODID, ModConfig.Type.COMMON, ClientConfig.SPEC);
        NetworkHandler.register();
        FabricInteractions.register();
        CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> {
            InternalCommand.registerCommands(dispatcher);
            TradeBroadcastCommand.registerCommands(dispatcher);
        });
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            TradeSelectionSessionManager.onServerTick(server);
            for (var player : server.getPlayerList().getPlayers()) {
                RaidRewardHandler.onPlayerTick(player);
                VillagerTradeReporter.onPlayerTick(player);
            }
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            TradeSelectionSessionManager.onPlayerLoggedOut(handler.player);
            RaidRewardHandler.onPlayerLogout(handler.player);
        });
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> VillagerTradeReporter.onVillagerDeath(entity));
    }

    /** Mirrors Forge's base config-event listener, including its physical-client guard. */
    private static void onConfigEvent(ModConfig config) {
        if (config.getSpec() == ServerConfig.SPEC) ServerConfig.onLoad();
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && config.getSpec() == ClientConfig.SPEC) ClientConfig.onLoad();
    }
}
