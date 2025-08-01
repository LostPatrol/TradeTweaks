package net.lostpatrol.tradetweaks.common.item;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TradeTweaks.MODID);

    public static final RegistryObject<Item> EMERALD_WAND = ITEMS.register("emerald_wand", EmeraldWand::new);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}