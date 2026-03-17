package net.lostpatrol.tradetweaks.config;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;

public class ClientConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

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

    private static final ModConfigSpec.EnumValue<ReportMode> REPORT_MODE = BUILDER
            .comment("Player's local report mode setting")
            .defineEnum("client.reportMode", ReportMode.LIBRARIAN_ONLY);

    private static final ModConfigSpec.BooleanValue RENDER_ITEMS = BUILDER
            .comment("Render the item icon in chat bar")
            .define("client.renderItems", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static ReportMode tempMode = ReportMode.LIBRARIAN_ONLY;
    public static boolean tempRenderFlag = true;

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == SPEC) {
            tempMode = getMode();
            tempRenderFlag = getRenderMode();
            TradeTweaks.LOGGER.debug("Loaded client config: mode={}", tempMode);
        }
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            tempMode = getMode();
            tempRenderFlag = getRenderMode();
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
