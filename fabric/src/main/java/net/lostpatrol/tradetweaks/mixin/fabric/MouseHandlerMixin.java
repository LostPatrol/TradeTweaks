/** Stops vanilla hotbar scrolling when sneak scrolling changes a wand mode. */
package net.lostpatrol.tradetweaks.mixin.fabric;

import net.lostpatrol.tradetweaks.client.events.WandScrollHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
    /** Consumes scrolling only during normal gameplay in the focused window. */
    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void tradetweaks$scroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (window == client.getWindow().getWindow() && client.screen == null && WandScrollHandler.onMouseScroll(vertical)) ci.cancel();
    }
}
