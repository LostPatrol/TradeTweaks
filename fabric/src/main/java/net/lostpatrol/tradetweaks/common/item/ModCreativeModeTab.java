/** Registers the Fabric creative tab with the shared item order. */
package net.lostpatrol.tradetweaks.common.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class ModCreativeModeTab {
    /** Installs the creative tab after all gameplay items exist. */
    public static void register() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath("tradetweaks", "tradetweaks_tab"), FabricItemGroup.builder()
                .title(Component.translatable("creativetab.tradetweaks_tab"))
                .icon(() -> new ItemStack(ModItems.EMERALD_WAND.get()))
                .displayItems((params, output) -> {
                    output.accept(ModItems.EMERALD_WAND.get());
                    output.accept(ModItems.BOOK_OF_ENLIGHTENMENT.get());
                    output.accept(ModItems.RESTORATION_BOTTLE.get());
                    output.accept(ModItems.REVERSION_BOTTLE.get());
                    output.accept(ModItems.TOTEM_OF_VILLAGE_HERO.get());
                    output.accept(ModItems.RESTOCK_WRIT.get());
                }).build());
    }
}
