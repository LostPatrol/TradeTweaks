package net.lostpatrol.tradetweaks.common.wand.handler;

import net.lostpatrol.tradetweaks.util.VillagerUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.Optional;

public class HandlerTrackVillager {
    public static final int DURATION_TICKS = 300;
    public static final int TRACK_RADIUS = 64;

    public static Component TRACK_VILLAGER_SUCCESS(Villager v) {
        return Component.translatable("tradetweaks.emerald_wand.success.track_villager", v.getBlockX(), v.getBlockY(), v.getBlockZ())
                .withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, v.getBlockX() + " " + v.getBlockY() + " " + v.getBlockZ()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("tradetweaks.emerald_wand.text.copy"))));
    }

    public static final Component TRACK_VILLAGER_FAIL = Component.translatable("tradetweaks.emerald_wand.fail.block_not_occupied").withStyle(ChatFormatting.RED);

    public static InteractionResult handle(Player player, BlockPos pos, ServerLevel level) {
        Optional<Villager> optionalVillager = level.getEntitiesOfClass(
                        Villager.class,
                        new AABB(pos).inflate(TRACK_RADIUS)
                ).stream()
                .filter(villager -> isWorkstationForVillager(villager, pos))
                .findFirst();

        if (optionalVillager.isEmpty()) {
            player.displayClientMessage(TRACK_VILLAGER_FAIL, true);
            return InteractionResult.SUCCESS;
        }

        Villager villager = optionalVillager.get();
        VillagerUtil.highlightVillagerWithEntity(villager, DURATION_TICKS);

        player.sendSystemMessage(TRACK_VILLAGER_SUCCESS(villager));
        return InteractionResult.SUCCESS;
    }

    private static boolean isWorkstationForVillager(Villager villager, BlockPos pos) {
        return villager.getBrain().getMemory(MemoryModuleType.JOB_SITE)
                .map(globalPos -> globalPos.pos().equals(pos))
                .orElse(false);
    }
}
