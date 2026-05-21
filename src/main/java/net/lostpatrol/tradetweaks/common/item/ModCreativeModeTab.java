package net.lostpatrol.tradetweaks.common.item;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTab {
    public static final String TRADETWEAKS_STRING = "creativetab.tradetweaks_tab";

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TradeTweaks.MODID);

    public static final RegistryObject<CreativeModeTab> TRADETWEAKS_TAB = TABS.register("tradetweaks_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable(TRADETWEAKS_STRING))
                    .icon(() -> new ItemStack(ModItems.EMERALD_WAND.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.EMERALD_WAND.get());
                        output.accept(ModItems.BOOK_OF_ENLIGHTENMENT.get());
                        output.accept(ModItems.RESTORATION_BOTTLE.get());
                        output.accept(ModItems.REVERSION_BOTTLE.get());
                        output.accept(ModItems.TOTEM_OF_VILLAGE_HERO.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        TABS.register(eventBus);
    }
}
