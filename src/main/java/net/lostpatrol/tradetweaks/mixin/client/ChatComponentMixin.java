package net.lostpatrol.tradetweaks.mixin.client;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.lostpatrol.tradetweaks.client.render.ItemRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ChatComponent.class, priority = 999)
public class ChatComponentMixin {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;III)I"))
    private int wrapDrawString(GuiGraphics instance, Font font, FormattedCharSequence text, int x, int y, int color, Operation<Integer> original) {
        ItemRenderer.renderItemForMessage(instance, text, x, y, color);

        return original.call(instance, font, text, x, y, color);
    }
}