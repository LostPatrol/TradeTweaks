/** Registers the same six gameplay items in Fabric's vanilla registry. */
package net.lostpatrol.tradetweaks.common.item;

import net.lostpatrol.tradetweaks.common.item.villager.*;
import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import java.util.function.Supplier;

public final class ModItems {
    /** Wand registration, retaining the shared supplier-shaped API. */
    public static final Supplier<Item> EMERALD_WAND = item("emerald_wand", new EmeraldWand());
    /** Nitwit enlightenment item. */
    public static final Supplier<Item> BOOK_OF_ENLIGHTENMENT = item("book_of_enlightenment", new BookOfEnlightenmentItem(new Item.Properties().stacksTo(16)));
    /** Instant zombie-villager cure. */
    public static final Supplier<Item> RESTORATION_BOTTLE = item("restoration_bottle", new RestorationBottleItem(new Item.Properties().stacksTo(16).rarity(Rarity.RARE)));
    /** Villager zombification item. */
    public static final Supplier<Item> REVERSION_BOTTLE = item("reversion_bottle", new ReversionBottleItem(new Item.Properties().stacksTo(16)));
    /** Raid reward and hero-effect item. */
    public static final Supplier<Item> TOTEM_OF_VILLAGE_HERO = item("totem_of_village_hero", new TotemOfVillageHeroItem(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)));
    /** Instant trade restock item. */
    public static final Supplier<Item> RESTOCK_WRIT = item("restock_writ", new RestockWritItem(new Item.Properties()));

    /** Registers one item and exposes it using the shared getter contract. */
    private static Supplier<Item> item(String path, Item item) {
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("tradetweaks", path), item);
        return () -> item;
    }

    /** Forces static item registration during common initialization. */
    public static void register() { }
}
