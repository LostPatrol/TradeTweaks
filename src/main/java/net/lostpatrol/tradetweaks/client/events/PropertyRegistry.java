package net.lostpatrol.tradetweaks.client.events;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.lostpatrol.tradetweaks.common.item.ModItems;
import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class PropertyRegistry {
    @SubscribeEvent
    public static void propertyOverrideRegistry(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(ModItems.EMERALD_WAND.get(), ResourceLocation.fromNamespaceAndPath(TradeTweaks.MODID, "upgrade"), (itemStack, clientLevel, livingEntity, num)
                    -> EmeraldWand.isUpgradedTexture(itemStack));
        });
    }
}
