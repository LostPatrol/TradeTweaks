/** Encodes the existing chat item prefix without linking client rendering classes. */
package net.lostpatrol.tradetweaks.common.tradecast;

import net.lostpatrol.tradetweaks.config.ClientConfig;
import net.minecraft.network.chat.*;
import net.minecraft.world.item.ItemStack;

public final class ChatItemIcon {
    /** Builds the existing three-space item icon and its styled display name. */
    public static MutableComponent createStackComponent(ItemStack stack, MutableComponent component) {
        if (!ClientConfig.tempRenderFlag)
            return component;

        Style style = component.getStyle();
        ItemStack copyStack = stack.copy();
        if (stack.getCount() > 64) {
            copyStack.setCount(64);
        }
        style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(copyStack)));
        component.withStyle(style);


        MutableComponent out = Component.literal("   ");
        out.setStyle(style);
        return out.append(component);
    }

}
