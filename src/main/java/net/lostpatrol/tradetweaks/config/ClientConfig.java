package net.lostpatrol.tradetweaks.config;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = TradeTweaks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public enum ReportMode {
        OFF("off"),
        LIBRARIAN_ONLY("librarian"),
        ALL_VILLAGERS("all");
        private final String key;

        ReportMode(String key) {
            this.key = key;
        }

        public String getTranslationKey() {
            return "tradecast.mode." + key;
        }

        @Override
        public String toString() {
            return key;
        }
    }

    private static final ForgeConfigSpec.EnumValue<ReportMode> REPORT_MODE = BUILDER
            .comment("Player's local report mode setting")
            .defineEnum("client.reportMode", ReportMode.LIBRARIAN_ONLY);

    private static final ForgeConfigSpec.BooleanValue RENDER_ITEMS = BUILDER
            .comment("Render the item icon in chat bar")
            .define("client.renderItems", true);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static ReportMode tempMode = ReportMode.LIBRARIAN_ONLY;
    public static boolean tempRenderFlag = true;

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            tempMode = getMode();
            TradeTweaks.LOGGER.debug("Loaded client config: mode={}", tempMode);
        }
    }

    public static void setMode(ReportMode mode) {
        REPORT_MODE.set(mode);
    }

    public static ReportMode getMode() {
        return REPORT_MODE.get();
    }

    public static void setRenderMode(boolean flag) {
        RENDER_ITEMS.set(flag);
    }

    public static boolean getRenderMode() {
        return RENDER_ITEMS.get();
    }

}