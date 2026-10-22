/** Preserves native image and item-color access used by trade color search. */
package net.lostpatrol.tradetweaks.platform;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.world.item.ItemStack;

public final class ClientPlatform {
    /** Returns the original sprite pixels without copying or closing the atlas image. */
    public static NativeImage originalImage(SpriteContents sprite) { return sprite.getOriginalImage(); }

    /** Returns the primary item tint through Forge's client accessor. */
    public static int itemTint(ItemStack stack) { return Minecraft.getInstance().getItemColors().getColor(stack, 0); }
}
