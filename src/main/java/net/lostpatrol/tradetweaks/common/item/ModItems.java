package net.lostpatrol.tradetweaks.common.item;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.lostpatrol.tradetweaks.common.item.villager.BookOfEnlightenmentItem;
import net.lostpatrol.tradetweaks.common.item.villager.RestockWritItem;
import net.lostpatrol.tradetweaks.common.item.villager.RestorationBottleItem;
import net.lostpatrol.tradetweaks.common.item.villager.ReversionBottleItem;
import net.lostpatrol.tradetweaks.common.item.villager.TotemOfVillageHeroItem;
import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, TradeTweaks.MODID);

    public static final DeferredHolder<Item, Item> EMERALD_WAND = ITEMS.register("emerald_wand", EmeraldWand::new);
    public static final DeferredHolder<Item, Item> BOOK_OF_ENLIGHTENMENT = ITEMS.register("book_of_enlightenment",
            () -> new BookOfEnlightenmentItem(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, Item> RESTORATION_BOTTLE = ITEMS.register("restoration_bottle",
            () -> new RestorationBottleItem(new Item.Properties().stacksTo(16).rarity(Rarity.RARE)));
    public static final DeferredHolder<Item, Item> REVERSION_BOTTLE = ITEMS.register("reversion_bottle",
            () -> new ReversionBottleItem(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, Item> TOTEM_OF_VILLAGE_HERO = ITEMS.register("totem_of_village_hero",
            () -> new TotemOfVillageHeroItem(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)));
    public static final DeferredHolder<Item, Item> RESTOCK_WRIT = ITEMS.register("restock_writ",
            () -> new RestockWritItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
