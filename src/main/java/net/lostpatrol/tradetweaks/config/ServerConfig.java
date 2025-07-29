package net.lostpatrol.tradetweaks.config;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = TradeTweaks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ServerConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue CHECK_INTERVAL = BUILDER
            .comment("Detection interval in seconds")
            .defineInRange("server.checkInterval", 5, 1, 100);
    private static final ForgeConfigSpec.IntValue CHECK_RADIUS = BUILDER
            .comment("Detection radius in blocks")
            .defineInRange("server.checkRadius", 10, 1, 32);


    public static final ForgeConfigSpec SPEC = BUILDER.build();

    // playertick will use this.
    public static int tempIntervalTicks = 100;
    public static int tempRadiusBlocks = 10;

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            tempIntervalTicks = getCheckInterval() * 20;
            tempRadiusBlocks = getCheckRadius();
            TradeTweaks.LOGGER.debug("Loaded client config: check interval= {}ticks", tempIntervalTicks);
        }
    }

    public static void setCheckInterval(int seconds) {
        CHECK_INTERVAL.set(seconds);
    }

    public static int getCheckInterval() {
        return CHECK_INTERVAL.get();
    }

    public static void setCheckRadius(int blocks) {
        CHECK_RADIUS.set(blocks);
    }

    public static int getCheckRadius() {
        return CHECK_RADIUS.get();
    }
}