package net.lostpatrol.tradetweaks.common.wand.handler;

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

    public static Component TRACK_BLOCK_SUCCESS(BlockPos pos) {
        return Component.translatable("tradetweaks.emerald_wand.success.track_block", pos.getX(), pos.getY(), pos.getZ())
                .withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, pos.getX() + " " + pos.getY() + " " + pos.getZ()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("tradetweaks.emerald_wand.text.copy"))));
    }

    public static final Component TRACK_BLOCK_FAIL = Component.translatable("tradetweaks.emerald_wand.fail.villager_not_occupied").withStyle(ChatFormatting.RED);
    public static final Component TRACK_BLOCK_FAIL_ERR = Component.translatable("tradetweaks.emerald_wand.fail.block_not_exist").withStyle(ChatFormatting.RED);

    public static InteractionResult handle(Player player, Villager villager) {
        if (VillagerUtil.isUnemployed(villager) || VillagerUtil.isNitwit(villager)) {
            player.displayClientMessage(TRACK_BLOCK_FAIL, true);
            return InteractionResult.SUCCESS;
        }
        Optional<GlobalPos> jobSite = villager.getBrain().getMemory(MemoryModuleType.JOB_SITE);
        if (jobSite.isPresent()) {
            BlockPos pos = jobSite.get().pos();
            player.sendSystemMessage(TRACK_BLOCK_SUCCESS(pos));
            if (player instanceof ServerPlayer serverPlayer) {
                NetworkHandler.sendBlockHighlightToPlayer(
                        serverPlayer,
                        new PacketBlockHighlight(pos, DURATION_TICKS, R, G, B)
                );
            }
        }
        else {
            player.displayClientMessage(TRACK_BLOCK_FAIL_ERR, true);
        }
        return InteractionResult.SUCCESS;
    }
}
