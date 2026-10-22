/** Minimal inventory view used by villager upgrade payment. */
package net.lostpatrol.tradetweaks.platform;

import net.minecraft.world.item.ItemStack;

public interface ItemAccess {
    /** Returns the number of accessible slots. */
    int getSlots();
    /** Returns the stack currently held by a slot. */
    ItemStack getStackInSlot(int slot);
    /** Removes at most the requested number of items. */
    void extractItem(int slot, int amount, boolean simulate);
}
