package net.lostpatrol.tradetweaks.events;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TradeTweaks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WandInteractionHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!event.getLevel().isClientSide) {
            return;
        }

        if (!(event.getItemStack().getItem() instanceof EmeraldWand wand)) {
            return;
        }

        if (!(event.getTarget() instanceof Villager villager)) {
            return;
        }

        Player player = event.getEntity();

        if (!player.isShiftKeyDown() || villager.isSleeping() || !villager.isAlive()) {
            return;
        }

        wand.interactLivingEntity(event.getItemStack(), event.getEntity(), (LivingEntity) event.getTarget(), event.getHand());
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }
}