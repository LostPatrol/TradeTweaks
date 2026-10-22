/** Applies chat-icon opacity using vanilla's per-vertex bulk-quad API. */
package net.lostpatrol.tradetweaks.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.lostpatrol.tradetweaks.client.render.ItemRenderer;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@Mixin(value = net.minecraft.client.renderer.entity.ItemRenderer.class, priority = 999)
public abstract class ItemRendererMixin {
    /** Resolves tint colors without shadowing a private final field. */
    @Accessor public abstract ItemColors getItemColors();

    /** Replaces the normal opaque quad emission only while rendering chat icons. */
    @Inject(method = "renderQuadList", at = @At("HEAD"), cancellable = true)
    private void tradetweaks$fade(PoseStack poses, VertexConsumer vertices, List<BakedQuad> quads, ItemStack stack, int light, int overlay, CallbackInfo ci) {
        if (ItemRenderer.alphaValue == 1.0F) return;
        for (BakedQuad quad : quads) {
            int color = !stack.isEmpty() && quad.isTinted() ? getItemColors().getColor(stack, quad.getTintIndex()) : -1;
            float red = (color >> 16 & 255) / 255.0F;
            float green = (color >> 8 & 255) / 255.0F;
            float blue = (color & 255) / 255.0F;
            // The native NeoForge overload adds vertex-color handling; vanilla needs the arrays explicitly.
            vertices.putBulkData(poses.last(), quad, new float[]{1, 1, 1, 1}, red, green, blue, ItemRenderer.alphaValue,
                    new int[]{light, light, light, light}, overlay, true);
        }
        ci.cancel();
    }
}
