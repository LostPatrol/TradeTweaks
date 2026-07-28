package net.lostpatrol.tradetweaks.common.wand.handler;

import net.lostpatrol.tradetweaks.advancement.AdvancementEventTrigger;
import net.lostpatrol.tradetweaks.advancement.ModCriteriaTriggers;
import net.lostpatrol.tradetweaks.network.NetworkHandler;
import net.lostpatrol.tradetweaks.network.packet.PacketBlockHighlight;
import net.lostpatrol.tradetweaks.util.VillagerUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;


public class HandlerTrackBlock {
    public static final int DURATION_TICKS = 300;
    public static final float R = 1.0F;
    public static final float G = 0.0F;
    public static final float B = 0.0F;
    public static final float POTENTIAL_R = 0.0F;
    public static final float POTENTIAL_G = 1.0F;
    public static final float POTENTIAL_B = 0.0F;

    public static Component TRACK_BLOCK_SUCCESS(BlockPos pos) {
        return Component.translatable("tradetweaks.emerald_wand.success.track_block", pos.getX(), pos.getY(), pos.getZ())
                .withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, pos.getX() + " " + pos.getY() + " " + pos.getZ()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("tradetweaks.emerald_wand.text.copy"))));
    }

    public static final Component TRACK_BLOCK_FAIL = Component.translatable("tradetweaks.emerald_wand.fail.no_potential_jobsite").withStyle(ChatFormatting.RED);

    public static InteractionResult handle(Player player, Villager villager) {
        if (VillagerUtil.isNitwit(villager)) {
            player.displayClientMessage(TRACK_BLOCK_FAIL, true);
            return InteractionResult.SUCCESS;
        }

        if (VillagerUtil.isUnemployed(villager)) {
            Optional<GlobalPos> potentialJobSite = villager.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
            if (potentialJobSite.isPresent()) {
                highlightBlock(player, potentialJobSite.get().pos(), POTENTIAL_R, POTENTIAL_G, POTENTIAL_B);
            } else {
                player.displayClientMessage(TRACK_BLOCK_FAIL, true);
            }
            return InteractionResult.SUCCESS;
        }

        Optional<GlobalPos> jobSite = villager.getBrain().getMemory(MemoryModuleType.JOB_SITE);
        if (jobSite.isPresent()) {
            highlightBlock(player, jobSite.get().pos(), R, G, B);
        }
        else {
            player.displayClientMessage(TRACK_BLOCK_FAIL, true);
        }
        return InteractionResult.SUCCESS;
    }

    private static void highlightBlock(Player player, BlockPos pos, float red, float green, float blue) {
        ModCriteriaTriggers.trigger(player, AdvancementEventTrigger.Event.TRACK_BLOCK);
        player.sendSystemMessage(TRACK_BLOCK_SUCCESS(pos));
        if (player instanceof ServerPlayer serverPlayer) {
            NetworkHandler.sendBlockHighlightToPlayer(
                    serverPlayer,
                    new PacketBlockHighlight(pos, DURATION_TICKS, red, green, blue)
            );
        }
    }
}
