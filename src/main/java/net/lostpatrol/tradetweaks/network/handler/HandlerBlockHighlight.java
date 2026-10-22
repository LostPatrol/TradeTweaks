/** Tracks and renders workstation outlines from shared network packets. */
package net.lostpatrol.tradetweaks.network.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lostpatrol.tradetweaks.network.packet.PacketBlockHighlight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.lostpatrol.tradetweaks.network.PayloadContext;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.lostpatrol.tradetweaks.client.render.HighlightRender;

public class HandlerBlockHighlight {

    public static class HighlightEntry {
        private int remainingTicks;
        private final float red, green, blue;

        public HighlightEntry(int duration, float[] color) {
            this.remainingTicks = duration;
            this.red = color[0];
            this.green = color[1];
            this.blue = color[2];
        }

        public void decrement() {
            this.remainingTicks--;
        }

        public boolean shouldRemove() {
            return this.remainingTicks <= 0;
        }

        public int getRemainingTicks() {
            return remainingTicks;
        }

        public float getRed() {
            return red;
        }

        public float getGreen() {
            return green;
        }

        public float getBlue() {
            return blue;
        }
    }

    private static final Map<BlockPos, HighlightEntry> HIGHLIGHTED_BLOCKS = new HashMap<>();

    /** Stores the server-provided outline on the client game thread. */
    public static void handle(PacketBlockHighlight packet, PayloadContext ctx) {
        ctx.enqueueWork(() -> {
            HIGHLIGHTED_BLOCKS.put(packet.getPos(), new HighlightEntry(
                    packet.getDurationTicks(),
                    new float[]{packet.getRed(), packet.getGreen(), packet.getBlue()}
            ));
        });
    }

    /** Ages workstation outlines once per client tick. */
    public static void onClientTick() {
        HIGHLIGHTED_BLOCKS.entrySet().removeIf(entry -> {
            HighlightEntry highlight = entry.getValue();
            highlight.decrement();
            return highlight.shouldRemove();
        });
    }

    /** Renders the active workstation outlines after world particles. */
    public static void onRenderWorldLast(PoseStack poseStack) {

        Minecraft mc = Minecraft.getInstance();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        for (Map.Entry<BlockPos, HighlightEntry> entry : HIGHLIGHTED_BLOCKS.entrySet()) {
            HighlightRender.renderBlockOutline(poseStack, bufferSource, entry.getKey(), entry.getValue());
        }

        bufferSource.endBatch();
    }
}
