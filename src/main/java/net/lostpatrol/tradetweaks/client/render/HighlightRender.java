package net.lostpatrol.tradetweaks.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.lostpatrol.tradetweaks.network.handler.HandlerBlockHighlight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.OptionalDouble;


public class HighlightRender extends RenderType {

    private static final LineStateShard LINE_3 = new LineStateShard(OptionalDouble.of(3.0));
    private static final RenderType BLOCK_HIGHLIGHT_LINE = create("tradetweaks_block_highlight_line",
            DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 65536, false, false,
            CompositeState.builder()
                    .setLineState(LINE_3)
                    .setTransparencyState(TransparencyStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setTextureState(NO_TEXTURE)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setCullState(NO_CULL)
                    .setLightmapState(NO_LIGHTMAP)
                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .setShaderState(RENDERTYPE_LINES_SHADER)
                    .createCompositeState(false)
    );

    public static void renderBlockOutline(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource,
                                          BlockPos pos, HandlerBlockHighlight.HighlightEntry entry) {
        VertexConsumer consumer = bufferSource.getBuffer(BLOCK_HIGHLIGHT_LINE);

        poseStack.pushPose();

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();

        float alpha = 0.6f + 0.4f * (float) Math.sin(entry.getRemainingTicks() * 0.2f);

        Minecraft mc = Minecraft.getInstance();
        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        LevelRenderer.renderLineBox(
                poseStack,
                consumer,
                new AABB(pos).move(-cameraPos.x, -cameraPos.y, -cameraPos.z),
                entry.getRed(), entry.getGreen(), entry.getBlue(), alpha
        );
        bufferSource.endBatch();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        poseStack.popPose();
    }

    private HighlightRender() {
        super("", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 0, false, false, () -> {}, () -> {});
    }
}
