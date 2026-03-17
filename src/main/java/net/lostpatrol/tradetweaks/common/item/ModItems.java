package net.lostpatrol.tradetweaks.common.item;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, TradeTweaks.MODID);

    public static final DeferredHolder<Item, Item> EMERALD_WAND = ITEMS.register("emerald_wand", EmeraldWand::new);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

