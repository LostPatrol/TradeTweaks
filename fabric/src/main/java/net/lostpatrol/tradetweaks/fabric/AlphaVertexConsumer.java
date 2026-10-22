/** Adds chat opacity to vanilla 1.20.1's bulk-quad API, which has no alpha parameter. */
package net.lostpatrol.tradetweaks.fabric;

import com.mojang.blaze3d.vertex.VertexConsumer;

public record AlphaVertexConsumer(VertexConsumer delegate, float alpha) implements VertexConsumer {
    /** Forwards vertex positions while keeping subsequent attributes on this wrapper. */
    @Override public VertexConsumer vertex(double x, double y, double z) { delegate.vertex(x, y, z); return this; }
    /** Multiplies the original vertex alpha by the current chat opacity. */
    @Override public VertexConsumer color(int red, int green, int blue, int opacity) {
        delegate.color(red, green, blue, Math.round(opacity * alpha));
        return this;
    }
    /** Forwards texture coordinates. */
    @Override public VertexConsumer uv(float u, float v) { delegate.uv(u, v); return this; }
    /** Forwards overlay coordinates. */
    @Override public VertexConsumer overlayCoords(int u, int v) { delegate.overlayCoords(u, v); return this; }
    /** Forwards packed light coordinates. */
    @Override public VertexConsumer uv2(int u, int v) { delegate.uv2(u, v); return this; }
    /** Forwards normals. */
    @Override public VertexConsumer normal(float x, float y, float z) { delegate.normal(x, y, z); return this; }
    /** Completes the wrapped vertex. */
    @Override public void endVertex() { delegate.endVertex(); }
    /** Preserves callers that use a default vertex color. */
    @Override public void defaultColor(int red, int green, int blue, int opacity) { delegate.defaultColor(red, green, blue, Math.round(opacity * alpha)); }
    /** Clears the wrapped default color. */
    @Override public void unsetDefaultColor() { delegate.unsetDefaultColor(); }
}
