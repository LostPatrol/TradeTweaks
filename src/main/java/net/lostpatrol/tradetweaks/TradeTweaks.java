package net.lostpatrol.tradetweaks;

import com.mojang.logging.LogUtils;
import net.lostpatrol.tradetweaks.command.InternalCommand;
import net.lostpatrol.tradetweaks.command.TradeBroadcastCommand;
import net.lostpatrol.tradetweaks.common.tradecast.VillagerTradeReporter;
import net.lostpatrol.tradetweaks.config.ClientConfig;
import net.lostpatrol.tradetweaks.config.ServerConfig;
import net.lostpatrol.tradetweaks.common.item.ModCreativeModeTab;
import net.lostpatrol.tradetweaks.common.item.ModItems;
import net.lostpatrol.tradetweaks.common.recipe.ModRecipeSerializers;
import net.lostpatrol.tradetweaks.client.events.WandScrollHandler;
import net.lostpatrol.tradetweaks.common.item.villager.VillagerConversionMemory;
import net.lostpatrol.tradetweaks.events.RaidRewardHandler;
import net.lostpatrol.tradetweaks.events.VillagerToolInteractionHandler;
import net.lostpatrol.tradetweaks.events.WandInteractionHandler;
import net.lostpatrol.tradetweaks.network.handler.HandlerBlockHighlight;
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
    public static final String MODID = "tradetweaks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TradeTweaks(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, ClientConfig.SPEC);

        NeoForge.EVENT_BUS.register(WandInteractionHandler.class);
        NeoForge.EVENT_BUS.register(VillagerToolInteractionHandler.class);
        NeoForge.EVENT_BUS.register(VillagerConversionMemory.class);
        NeoForge.EVENT_BUS.register(RaidRewardHandler.class);
        if (Dist.CLIENT.equals(net.neoforged.fml.loading.FMLEnvironment.dist)) {
            NeoForge.EVENT_BUS.register(HandlerBlockHighlight.class);
            NeoForge.EVENT_BUS.register(WandScrollHandler.class);
        }

        VillagerTradeReporter.register();
        TradeBroadcastCommand.register();
        InternalCommand.register();

        NetworkHandler.register(modEventBus);

        modEventBus.register(ServerConfig.class);
        if (Dist.CLIENT.equals(net.neoforged.fml.loading.FMLEnvironment.dist)) {
            modEventBus.register(ClientConfig.class);
            modEventBus.register(net.lostpatrol.tradetweaks.client.events.PropertyRegistry.class);
        }

        ModItems.register(modEventBus);
        ModCreativeModeTab.register(modEventBus);
        ModRecipeSerializers.register(modEventBus);
    }
}
