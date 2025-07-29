package net.lostpatrol.tradetweaks.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.lostpatrol.tradetweaks.TradeTweaks;
import net.lostpatrol.tradetweaks.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TradeTweaks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ItemRenderer {
    public static float alphaValue = 1F;

    @OnlyIn(Dist.CLIENT)
    public static void renderItemForMessage(GuiGraphics guiGraphics, FormattedCharSequence sequence, float x, float y, int color) {
        if (!ClientConfig.tempRenderFlag)
            return;

        Minecraft mc = Minecraft.getInstance();

        StringBuilder before = new StringBuilder();

        int halfSpace = mc.font.width(" ") / 2;

        final class OffsetContext {
            float offset = -halfSpace;
        }
        OffsetContext context = new OffsetContext();

        sequence.accept((counter_, style, character) -> {
            String sofar = before.toString();

            // This is for compatibility with ShowcaseItems...
            if (sofar.endsWith(":  ")) {
                int s_length = mc.font.width(sofar.substring(0, sofar.length() - 2));
                // and Chat Heads
                if (ModList.get().isLoaded("chat_heads")) {
                    render(mc, guiGraphics, s_length+5*halfSpace, character == ' ' ? 0 : -halfSpace, x, y, style, color);
                    return false;
                } else {
                    render(mc, guiGraphics, s_length, character == ' ' ? 0 : -halfSpace, x, y, style, color);
                    return false;
                }

            } else if (sofar.endsWith("  ")) {
                float shift = mc.font.width(before.substring(0, before.length() - 2)) + context.offset;
                render(mc, guiGraphics, shift, 0, x, y, style, color);
                context.offset += mc.font.width(String.valueOf(before)) + 2 * halfSpace;
                before.setLength(0);
                return true;
            }
            before.append((char) character);
            return true;
        });
    }

    public static MutableComponent createStackComponent(ItemStack stack, MutableComponent component) {
        if (!ClientConfig.tempRenderFlag)
            return component;

        Style style = component.getStyle();
        ItemStack copyStack = stack.copy();
        if (stack.getCount() > 64) {
            copyStack.setCount(64);
        }
        style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(copyStack)));
        component.withStyle(style);


        MutableComponent out = Component.literal("   ");
        out.setStyle(style);
        return out.append(component);
    }

    @OnlyIn(Dist.CLIENT)
    private static void render(Minecraft mc, GuiGraphics guiGraphics, float shift, float extraShift, float x, float y, Style style, int color) {
        float a = (color >> 24 & 255) / 255.0F;

        PoseStack pose = guiGraphics.pose();

        HoverEvent hoverEvent = style.getHoverEvent();
        if (hoverEvent != null && hoverEvent.getAction() == HoverEvent.Action.SHOW_ITEM) {
            HoverEvent.ItemStackInfo contents = hoverEvent.getValue(HoverEvent.Action.SHOW_ITEM);

            ItemStack stack = contents != null ? contents.getItemStack() : ItemStack.EMPTY;

            if (stack.isEmpty()) {
                stack = new ItemStack(Blocks.BARRIER); //For invalid icon
            }

            float x_shift = shift + extraShift;

            //Fix y-shift if overflowingbars is installed
            if (ModList.get().isLoaded("overflowingbars")) {
                if (Minecraft.getInstance().player != null) {
                    y += Minecraft.getInstance().player.getAbsorptionAmount() > 10.0F ? 10 : 0;
                    y += Minecraft.getInstance().player.getArmorValue() > 0.5F ? 10 : 0;
                }
            }

            if (a > 0) {
                alphaValue = a;

                guiGraphics.pose().pushPose();

                guiGraphics.pose().mulPoseMatrix(pose.last().pose());

                guiGraphics.pose().translate(x_shift + x, y, 0);
                guiGraphics.pose().scale(0.5f, 0.5f, 0.5f);
                guiGraphics.renderItem(stack, 0, 0);
                guiGraphics.pose().popPose();

                RenderSystem.applyModelViewMatrix();

                alphaValue = 1F;
            }
        }
    }
}
