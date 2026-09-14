/*
 * Handles MCA's early entity interaction hook without introducing an MCA dependency.
 */
package net.lostpatrol.tradetweaks.events;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.lostpatrol.tradetweaks.common.item.ModItems;
import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = TradeTweaks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class McaInteractionHandler {
    private static final String MCA_MOD_ID = "mca";

    private McaInteractionHandler() {
    }

    /**
     * Runs Trade Tweaks tools before MCA can consume the more specific entity interaction.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        if (!ModList.get().isLoaded(MCA_MOD_ID)
                || !(event.getTarget() instanceof LivingEntity target)
                || !isMcaEntity(event.getTarget())) {
            return;
        }

        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();
        if (item instanceof EmeraldWand) {
            if (!(target instanceof Villager villager)
                    || !event.getEntity().isShiftKeyDown()
                    || villager.isSleeping()
                    || !villager.isAlive()) {
                return;
            }
        } else if (!isVillagerTool(item)) {
            return;
        }

        InteractionResult result = item.interactLivingEntity(stack, event.getEntity(), target, event.getHand());
        if (result != InteractionResult.PASS) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    private static boolean isMcaEntity(Entity entity) {
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        return id != null && MCA_MOD_ID.equals(id.getNamespace());
    }

    private static boolean isVillagerTool(Item item) {
        return item == ModItems.BOOK_OF_ENLIGHTENMENT.get()
                || item == ModItems.RESTORATION_BOTTLE.get()
                || item == ModItems.REVERSION_BOTTLE.get()
                || item == ModItems.RESTOCK_WRIT.get();
    }
}
