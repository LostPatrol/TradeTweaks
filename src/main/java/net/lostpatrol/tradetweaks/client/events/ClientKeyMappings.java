package net.lostpatrol.tradetweaks.client.events;

import com.mojang.blaze3d.platform.InputConstants;
import net.lostpatrol.tradetweaks.TradeTweaks;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = TradeTweaks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientKeyMappings {
    public static final String CATEGORY = "key.categories.tradetweaks";

    public static final KeyMapping WAND_MODE_WHEEL = new KeyMapping(
            "key.tradetweaks.wand_mode_wheel",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_ALT,
            CATEGORY
    );

    private ClientKeyMappings() {
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(WAND_MODE_WHEEL);
    }
}
