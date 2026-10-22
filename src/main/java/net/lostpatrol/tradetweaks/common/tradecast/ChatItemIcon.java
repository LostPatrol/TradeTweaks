/** Encodes chat icons using common components, safe on dedicated servers. */
package net.lostpatrol.tradetweaks.common.tradecast;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

public final class ChatItemIcon {
    /** Reserves three spaces with a hover item consumed by the client renderer. */
    public static MutableComponent createIconComponent(ItemStack stack) {
        ItemStack copy = stack.copy();
        copy.setCount(Math.min(copy.getCount(), 64));
        Style style = Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(copy)));
        return Component.literal("   ").withStyle(style);
    }
}
