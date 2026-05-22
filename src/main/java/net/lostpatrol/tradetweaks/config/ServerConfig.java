package net.lostpatrol.tradetweaks.config;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.lostpatrol.tradetweaks.common.wand.handler.HandlerUpgradeVillager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;

import java.util.ArrayList;
import java.util.List;

public class ServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue CHECK_INTERVAL = BUILDER
            .comment("Detection interval in seconds")
            .defineInRange("server.checkInterval", 5, 1, 100);
    private static final ModConfigSpec.IntValue CHECK_RADIUS = BUILDER
            .comment("Detection radius in blocks")
            .defineInRange("server.checkRadius", 10, 1, 32);

    private static final ModConfigSpec.ConfigValue<List<? extends String>> UPGRADE_COSTS = BUILDER
            .comment("List of items and amounts(range: 1-1000) required for villager upgrade in format 'modid:itemid:amount'. Items will be consumed in order.")
            .defineList("server.upgradeCosts", List.of("minecraft:emerald_block:1", "minecraft:emerald:9"), ServerConfig::validateItemEntry);

    private static final ModConfigSpec.BooleanValue LIBRARIAN_ENCHANTED_BOOK_SELECTION = BUILDER
            .comment("Whether librarian enchanted book related trades can appear in the trade selection replacement list.")
            .define("server.librarianEnchantedBookSelection", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemEntry(final Object obj) {
        if (!(obj instanceof String entry)) return false;

        String[] parts = entry.split(":");
        if (parts.length < 2 || parts.length > 3) return false;

        if (!BuiltInRegistries.ITEM.containsKey(ResourceLocation.fromNamespaceAndPath(parts[0], parts[1]))) {
            return false;
        }

        if (parts.length == 3) {
            try {
                int i = Integer.parseInt(parts[2]);
                return (i > 0) && (i <= 1000);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    public static List<HandlerUpgradeVillager.ItemCost> loadCosts() {
        List<HandlerUpgradeVillager.ItemCost> costs = new ArrayList<>();
        for (String entry : UPGRADE_COSTS.get()) {
            String[] parts = entry.split(":");
            ResourceLocation itemId = ResourceLocation.fromNamespaceAndPath(parts[0], parts[1]);
            int amount = Integer.parseInt(parts[2]);
            Item item = BuiltInRegistries.ITEM.get(itemId);
            if (item != null) {
                costs.add(new HandlerUpgradeVillager.ItemCost(item, amount));
            }
        }
        return costs;
    }

    // playertick will use this.
    public static int tempIntervalTicks = 100;
    public static int tempRadiusBlocks = 10;

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == SPEC) {
            tempIntervalTicks = getCheckInterval() * 20;
            tempRadiusBlocks = getCheckRadius();
            TradeTweaks.LOGGER.debug("Loaded client config: check interval= {}ticks", tempIntervalTicks);
            HandlerUpgradeVillager.reloadCosts();
        }
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            HandlerUpgradeVillager.reloadCosts();
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

    public static void setLibrarianEnchantedBookSelection(boolean enabled) {
        LIBRARIAN_ENCHANTED_BOOK_SELECTION.set(enabled);
        LIBRARIAN_ENCHANTED_BOOK_SELECTION.save();
    }

    public static boolean isLibrarianEnchantedBookSelectionEnabled() {
        return LIBRARIAN_ENCHANTED_BOOK_SELECTION.get();
    }
}
