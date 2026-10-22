/** Registers the upgraded emerald-wand model predicate. */
package net.lostpatrol.tradetweaks.client.events;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.lostpatrol.tradetweaks.common.item.ModItems;
import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class PropertyRegistry {
    /** Registers the upgraded wand model predicate on the client thread. */
    public static void propertyOverrideRegistry() {
            ItemProperties.register(ModItems.EMERALD_WAND.get(), new ResourceLocation(TradeTweaks.MODID, "upgrade"), (itemStack, clientLevel, livingEntity, num)
                    -> EmeraldWand.isUpgradedTexture(itemStack));
    }
}
