package net.lostpatrol.tradetweaks.events;

import net.lostpatrol.tradetweaks.common.item.ModItems;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class VillagerToolInteractionHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof LivingEntity target)) {
            return;
        }

        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();
        if (item != ModItems.BOOK_OF_ENLIGHTENMENT.get()
                && item != ModItems.RESTORATION_BOTTLE.get()
                && item != ModItems.REVERSION_BOTTLE.get()
                && item != ModItems.RESTOCK_WRIT.get()) {
            return;
        }

        InteractionResult result = item.interactLivingEntity(stack, event.getEntity(), target, event.getHand());
        if (result != InteractionResult.PASS) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }
}
