/** Implements Fabric metadata, entity data and inventory access. */
package net.lostpatrol.tradetweaks.platform;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import java.util.Optional;
import java.util.function.Consumer;

public final class Platform {
    /** Uses the item's registry namespace because vanilla has no creator hook. */
    public static String creatorModId(ItemStack stack) {
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace();
    }
    /** Exposes the complete vanilla player inventory, including both hands. */
    public static ItemAccess inventory(Player player) {
        var inventory = player.getInventory();
        return new ItemAccess() {
            /** Returns all player inventory slots. */
            public int getSlots() { return inventory.getContainerSize(); }
            /** Reads a player inventory slot. */
            public ItemStack getStackInSlot(int slot) { return inventory.getItem(slot); }
            /** Removes the selected payment and marks the inventory dirty. */
            public void extractItem(int slot, int amount, boolean simulate) {
                if (!simulate) {
                    inventory.removeItem(slot, amount);
                    inventory.setChanged();
                }
            }
        };
    }

    /** Returns the mixin-owned data written into entity NBT. */
    public static CompoundTag persistentData(Entity entity) {
        return ((PersistentEntityData) entity).tradetweaks$data();
    }

    /** Checks whether an optional integration is present. */
    public static boolean isModLoaded(String id) { return FabricLoader.getInstance().isModLoaded(id); }

    /** Resolves the display name used by trade search. */
    public static Optional<String> modName(String id) {
        return FabricLoader.getInstance().getModContainer(id).map(mod -> mod.getMetadata().getName());
    }

    /** Fabric has no cancellable living conversion event. */
    public static boolean canLivingConvert(LivingEntity entity, EntityType<? extends LivingEntity> target, Consumer<Integer> timer) {
        return true;
    }

    /** Restores shared conversion memory for explicit conversions. */
    public static void onLivingConvert(LivingEntity entity, LivingEntity outcome) {
        net.lostpatrol.tradetweaks.common.item.villager.VillagerConversionMemory.onLivingConversionPost(entity, outcome);
    }
}
