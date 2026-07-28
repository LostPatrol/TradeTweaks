package net.lostpatrol.tradetweaks.advancement;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class ModCriteriaTriggers {
    private static final AdvancementEventTrigger EVENT = CriteriaTriggers.register(
            new AdvancementEventTrigger(new ResourceLocation(TradeTweaks.MODID, "event"))
    );

    private ModCriteriaTriggers() {
    }

    public static void register() {
    }

    public static void trigger(Player player, AdvancementEventTrigger.Event event) {
        if (player instanceof ServerPlayer serverPlayer) {
            EVENT.trigger(serverPlayer, event);
        }
    }
}
