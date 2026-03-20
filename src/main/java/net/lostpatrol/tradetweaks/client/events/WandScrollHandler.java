package net.lostpatrol.tradetweaks.client.events;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TradeTweaks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value= Dist.CLIENT)
public class WandScrollHandler {

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Player player = Minecraft.getInstance().player;
        double scroll = event.getScrollDelta();
        if (player == null || !player.isShiftKeyDown()) return;
        if (scroll == (double)0.0F) return;

        InteractionHand hand = null;
        ItemStack heldItem = player.getMainHandItem();
        
        if (heldItem.getItem() instanceof EmeraldWand) {
            hand = InteractionHand.MAIN_HAND;
        } else {
            ItemStack offhandItem = player.getOffhandItem();
            if (offhandItem.getItem() instanceof EmeraldWand) {
                heldItem = offhandItem;
                hand = InteractionHand.OFF_HAND;
            }
        }

        if (hand != null && heldItem.getItem() instanceof EmeraldWand wand) {
            wand.switchMode(heldItem, player, (scroll > (double) 0.0F), hand);
            event.setCanceled(true);
        }
    }
}