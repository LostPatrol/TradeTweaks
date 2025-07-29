package net.lostpatrol.tradetweaks.mixin;

import net.lostpatrol.tradetweaks.client.render.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemStack.class, priority = 999)
public class ItemStackMixin {
    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true, require = 0)
    private void getHoverName(CallbackInfoReturnable<Component> callbackInfoReturnable) {
        callbackInfoReturnable.setReturnValue(ItemRenderer.createStackComponent((ItemStack) (Object) this, (MutableComponent) callbackInfoReturnable.getReturnValue()));
    }
}