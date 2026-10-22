/** Supplies the entity tag persisted by the Fabric entity mixin. */
package net.lostpatrol.tradetweaks.platform;

import net.minecraft.nbt.CompoundTag;

public interface PersistentEntityData {
    /** Returns the mutable Trade Tweaks entity tag. */
    CompoundTag tradetweaks$data();
}
