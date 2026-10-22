/** Registers the shared advancement trigger in Fabric's vanilla registry. */
package net.lostpatrol.tradetweaks.advancement;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class ModCriteriaTriggers {
    /** The shared criterion implementation used by every custom advancement. */
    private static final AdvancementEventTrigger EVENT = new AdvancementEventTrigger();

    /** Registers the custom criterion before datapacks load. */
    public static void register() {
        Registry.register(BuiltInRegistries.TRIGGER_TYPES, ResourceLocation.fromNamespaceAndPath("tradetweaks", "event"), EVENT);
    }

    /** Awards an event only to a real server player. */
    public static void trigger(Player player, AdvancementEventTrigger.Event event) {
        if (player instanceof ServerPlayer serverPlayer) EVENT.trigger(serverPlayer, event);
    }
}
