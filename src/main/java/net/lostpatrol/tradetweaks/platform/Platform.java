/** Implements the small set of Forge facilities used by shared gameplay. */
package net.lostpatrol.tradetweaks.platform;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.event.ForgeEventFactory;
import java.util.Optional;
import java.util.function.Consumer;

public final class Platform {
    /** Preserves item-provided creator metadata for search integrations. */
    public static String creatorModId(net.minecraft.world.item.ItemStack stack) {
        return stack.getItem().getCreatorModId(stack);
    }
    /** Uses Forge's native inventory capability, including modded providers. */
    public static ItemAccess inventory(net.minecraft.world.entity.player.Player player) {
        var handler = player.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER).orElse(null);
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

    /** Preserves Forge's conversion veto hook. */
    public static boolean canLivingConvert(LivingEntity entity, EntityType<? extends LivingEntity> target, Consumer<Integer> timer) {
        return ForgeEventFactory.canLivingConvert(entity, target, timer);
    }

    /** Preserves Forge's conversion completion hook. */
    public static void onLivingConvert(LivingEntity entity, LivingEntity outcome) {
        ForgeEventFactory.onLivingConvert(entity, outcome);
    }
}
