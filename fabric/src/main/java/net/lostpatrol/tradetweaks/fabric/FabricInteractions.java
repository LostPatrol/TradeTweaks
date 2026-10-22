/** Runs tools before vanilla or MCA can consume entity interactions. */
package net.lostpatrol.tradetweaks.fabric;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.lostpatrol.tradetweaks.common.item.ModItems;
import net.lostpatrol.tradetweaks.common.tradeselect.TradeSelectionSessionManager;
import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.lostpatrol.tradetweaks.util.VillagerUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class FabricInteractions {
    /** Handles both Fabric packet paths before vanilla or MCA entity behavior. */
    public static void register() {
        // Fabric's client supplies a hit result even for ordinary interaction, so both paths share one dispatcher.
        UseEntityCallback.EVENT.register((player, level, hand, entity, hit) -> interact(player, entity, hand));
    }

    /** Preserves trade locks and the native client wand interaction behavior. */
    private static InteractionResult interact(Player player, Entity entity, InteractionHand hand) {
        if (player.isSpectator() || !(entity instanceof LivingEntity target)) return InteractionResult.PASS;
        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();
        if (!player.level().isClientSide && target instanceof Villager villager && VillagerUtil.isTradingWithOtherPlayer(villager, player)) {
            VillagerUtil.refuseInteraction(villager);
            if (TradeSelectionSessionManager.isSelectionLocked(villager) || restricted(stack)) return InteractionResult.SUCCESS;
        }
        if (item instanceof EmeraldWand wand && target instanceof Villager villager
                && player.isShiftKeyDown() && !villager.isSleeping() && villager.isAlive()) {
            InteractionResult result = wand.interactLivingEntity(stack, player, target, hand);
            // Fabric only sends the interaction packet for SUCCESS; the shared wand returns CONSUME on clients.
            return player.level().isClientSide ? InteractionResult.SUCCESS : result;
        }
        return isTool(item) ? item.interactLivingEntity(stack, player, target, hand) : InteractionResult.PASS;
    }

    /** Identifies the four early-interaction villager tools. */
    private static boolean isTool(Item item) {
        return item == ModItems.BOOK_OF_ENLIGHTENMENT.get() || item == ModItems.RESTORATION_BOTTLE.get()
                || item == ModItems.REVERSION_BOTTLE.get() || item == ModItems.RESTOCK_WRIT.get();
    }

    /** Restricts mutations while another player owns the villager trade session. */
    private static boolean restricted(ItemStack stack) {
        if (stack.is(ModItems.RESTOCK_WRIT.get())) return true;
        if (!(stack.getItem() instanceof EmeraldWand)) return false;
        var mode = EmeraldWand.getMode(stack);
        return mode == EmeraldWand.WandMode.RESET_MODE || mode == EmeraldWand.WandMode.UPGRADE_MODE || mode == EmeraldWand.WandMode.SELECT_MODE;
    }
}
