package net.lostpatrol.tradetweaks.common.wand.handler;

import net.lostpatrol.tradetweaks.util.VillagerUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;


public class HandlerVillagerReset {
    public static final Component RESET_SUCCESS = Component.translatable("tradetweaks.emerald_wand.success.reset").withStyle(ChatFormatting.DARK_GREEN);
    public static final Component RESET_FAIL = Component.translatable("tradetweaks.emerald_wand.fail.villager_not_occupied").withStyle(ChatFormatting.RED);

    public static InteractionResult handle(Player player, Villager villager) {
        if (VillagerUtil.isUnemployed(villager) || VillagerUtil.isNitwit(villager)) {
            player.displayClientMessage(RESET_FAIL, true);
            return InteractionResult.SUCCESS;
        }

        villager.releasePoi(MemoryModuleType.JOB_SITE);
        villager.releasePoi(MemoryModuleType.POTENTIAL_JOB_SITE);

        villager.setVillagerData(villager.getVillagerData()
                .setProfession(VillagerProfession.NONE)
                .setLevel(1));
        villager.setVillagerXp(0);
        villager.clearRestriction();
        villager.getOffers().clear();

        if (player.level() instanceof ServerLevel level) {
            villager.refreshBrain(level);
        }
        else{
            Brain<Villager> brain = villager.getBrain();

            brain.eraseMemory(MemoryModuleType.JOB_SITE);
            brain.eraseMemory(MemoryModuleType.LAST_WORKED_AT_POI);
            brain.eraseMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
            brain.eraseMemory(MemoryModuleType.SECONDARY_JOB_SITE);
            brain.eraseMemory(MemoryModuleType.WALK_TARGET);
            brain.eraseMemory(MemoryModuleType.INTERACTION_TARGET);

            villager.getNavigation().stop();

            brain.setActiveActivityIfPossible(Activity.IDLE);
        }

        player.displayClientMessage(RESET_SUCCESS, true);

        return InteractionResult.SUCCESS;
    }

}
