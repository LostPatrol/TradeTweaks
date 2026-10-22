/** Implements the small set of NeoForge facilities used by shared gameplay. */
package net.lostpatrol.tradetweaks.platform;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.EventHooks;
import java.util.Optional;
import java.util.function.Consumer;

public final class Platform {
    /** Preserves item-provided creator metadata for search integrations. */
    public static String creatorModId(net.minecraft.world.item.ItemStack stack) {
        return stack.getItem().getCreatorModId(stack);
    }
    /** Uses NeoForge's native inventory capability, including modded providers. */
    public static ItemAccess inventory(net.minecraft.world.entity.player.Player player) {
        var handler = player.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.ENTITY);
        if (handler == null) return null;
        return new ItemAccess() {
            /** Returns all exposed slots. */
            public int getSlots() { return handler.getSlots(); }
            /** Reads a capability slot. */
            public net.minecraft.world.item.ItemStack getStackInSlot(int slot) { return handler.getStackInSlot(slot); }
            /** Extracts the payment through the capability. */
            public void extractItem(int slot, int amount, boolean simulate) { handler.extractItem(slot, amount, simulate); }
        };
    }
    /** Returns the loader-owned persisted entity tag. */
    public static CompoundTag persistentData(Entity entity) {
        return entity.getPersistentData();
    }

    /** Checks an optional integration without linking against its classes. */
    public static boolean isModLoaded(String id) {
        return ModList.get().isLoaded(id);
    }

    /** Resolves the display name used by trade search. */
    public static Optional<String> modName(String id) {
        return ModList.get().getModContainerById(id).map(mod -> mod.getModInfo().getDisplayName());
    }

    /** Preserves NeoForge's conversion veto hook. */
    public static boolean canLivingConvert(LivingEntity entity, EntityType<? extends LivingEntity> target, Consumer<Integer> timer) {
        return EventHooks.canLivingConvert(entity, target, timer);
    }

    /** Preserves NeoForge's conversion completion hook. */
    public static void onLivingConvert(LivingEntity entity, LivingEntity outcome) {
        EventHooks.onLivingConvert(entity, outcome);
    }
}
