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

    @Accessor
    public abstract ItemColors getItemColors();

    @Inject(method = "renderQuadList", at = @At(value = "HEAD"), cancellable = true, require = 0)
    public void renderQuadList(PoseStack ms, VertexConsumer builder, List<BakedQuad> quads, ItemStack stack, int lightmap, int overlay, CallbackInfo ci) {
        if (ItemRenderer.alphaValue != 1.0F) {
            boolean flag = !stack.isEmpty();
            PoseStack.Pose entry = ms.last();

            for (BakedQuad bakedquad : quads) {
                int i = flag && bakedquad.isTinted() ? getItemColors().getColor(stack, bakedquad.getTintIndex()) : -1;

                float r = (i >> 16 & 255) / 255.0F;
                float g = (i >> 8 & 255) / 255.0F;
                float b = (i & 255) / 255.0F;
                builder.putBulkData(entry, bakedquad, r, g, b, ItemRenderer.alphaValue, lightmap, overlay, true);
            }
            ci.cancel();
        }
    }
}
