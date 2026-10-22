/** Selects a translucent item sheet while shared chat icons are fading. */
package net.lostpatrol.tradetweaks.mixin.client;

import net.lostpatrol.tradetweaks.client.render.ItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemBlockRenderTypes.class, priority = 999)
public class ItemBlockRenderTypesMixin {
    /** Preserves the existing shader-transparency handling for item icons. */
    @Inject(method = "getRenderType(Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/client/renderer/RenderType;", at = @At("HEAD"), cancellable = true)
    private static void tradetweaks$translucent(BlockState state, boolean needsCulling, CallbackInfoReturnable<RenderType> ci) {
        if (ItemRenderer.alphaValue != 1.0F) {
            ci.setReturnValue(!Minecraft.useShaderTransparency() || needsCulling ? Sheets.translucentCullBlockSheet() : Sheets.translucentItemSheet());
        }
    }
}
