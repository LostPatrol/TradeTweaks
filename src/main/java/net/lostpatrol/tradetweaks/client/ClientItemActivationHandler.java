package net.lostpatrol.tradetweaks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public final class ClientItemActivationHandler {
    private ClientItemActivationHandler() {
    }

    public static void display(ItemStack stack) {
        Minecraft.getInstance().gameRenderer.displayItemActivation(stack);
    }
}
