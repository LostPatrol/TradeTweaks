/** Uses access-widened vanilla fields for the shared trade color search. */
package net.lostpatrol.tradetweaks.platform;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.world.item.ItemStack;

public final class ClientPlatform {
    /** Returns the atlas-owned original sprite pixels. */
    public static NativeImage originalImage(SpriteContents sprite) { return sprite.originalImage; }

    /** Returns the primary vanilla item tint. */
    public static int itemTint(ItemStack stack) { return Minecraft.getInstance().itemColors.getColor(stack, 0); }
}
