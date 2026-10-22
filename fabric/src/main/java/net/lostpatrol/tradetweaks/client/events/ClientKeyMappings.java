/** Defines the Fabric key binding while keeping wheel input shared. */
package net.lostpatrol.tradetweaks.client.events;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public final class ClientKeyMappings {
    /** Translation category for all Trade Tweaks controls. */
    public static final String CATEGORY = "key.categories.tradetweaks";
    /** Opens the mode wheel while holding a wand. */
    public static final KeyMapping WAND_MODE_WHEEL = new KeyMapping("key.tradetweaks.wand_mode_wheel", GLFW.GLFW_KEY_LEFT_ALT, CATEGORY);

    /** Exposes the wheel binding in Minecraft's controls menu. */
    public static void register() { KeyBindingHelper.registerKeyBinding(WAND_MODE_WHEEL); }
}
