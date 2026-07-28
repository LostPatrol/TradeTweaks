package net.lostpatrol.tradetweaks.events;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.lostpatrol.tradetweaks.common.item.ModItems;
import net.lostpatrol.tradetweaks.common.tradeselect.TradeSelectionSessionManager;
import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.lostpatrol.tradetweaks.util.VillagerUtil;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TradeTweaks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VillagerToolInteractionHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof LivingEntity target)) {
            return;
        }

        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();
        if (!event.getLevel().isClientSide
                && target instanceof Villager villager
                && VillagerUtil.isTradingWithOtherPlayer(villager, event.getEntity())) {
            VillagerUtil.refuseInteraction(villager);
            if (TradeSelectionSessionManager.isSelectionLocked(villager) || isRestrictedTool(stack)) {
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }
        }

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

    private static boolean isRestrictedTool(ItemStack stack) {
        if (stack.is(ModItems.RESTOCK_WRIT.get())) {
            return true;
        }
        if (!(stack.getItem() instanceof EmeraldWand)) {
            return false;
        }
        EmeraldWand.WandMode mode = EmeraldWand.getMode(stack);
        return mode == EmeraldWand.WandMode.RESET_MODE
                || mode == EmeraldWand.WandMode.UPGRADE_MODE
                || mode == EmeraldWand.WandMode.SELECT_MODE;
    }
}
