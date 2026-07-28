package net.lostpatrol.tradetweaks.advancement;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCriteriaTriggers {
    private static final DeferredRegister<CriterionTrigger<?>> TRIGGERS =
            DeferredRegister.create(Registries.TRIGGER_TYPE, TradeTweaks.MODID);

    private static final DeferredHolder<CriterionTrigger<?>, AdvancementEventTrigger> EVENT =
            TRIGGERS.register("event", AdvancementEventTrigger::new);

    private ModCriteriaTriggers() {
    }

    public static void register(IEventBus eventBus) {
        TRIGGERS.register(eventBus);
    }

    public static void trigger(Player player, AdvancementEventTrigger.Event event) {
        if (player instanceof ServerPlayer serverPlayer) {
            EVENT.get().trigger(serverPlayer, event);
        }
    }
}
