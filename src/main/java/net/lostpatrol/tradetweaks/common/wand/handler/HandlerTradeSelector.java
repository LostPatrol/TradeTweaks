package net.lostpatrol.tradetweaks.common.wand.handler;

import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.lostpatrol.tradetweaks.network.NetworkHandler;
import net.lostpatrol.tradetweaks.network.packet.PacketOpenTradeSelection;
import net.lostpatrol.tradetweaks.util.VillagerUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


public class HandlerTradeSelector {

    public static final Component SELECT_FAIL_NOT_UPGRADED = Component.translatable("tradetweaks.emerald_wand.fail.wand_not_upgraded").withStyle(ChatFormatting.RED);
    public static final Component SELECT_FAIL_NOT_OCCUPIED = Component.translatable("tradetweaks.emerald_wand.fail.villager_not_occupied").withStyle(ChatFormatting.RED);

    public static InteractionResult handle(ItemStack stack, Player player, Villager villager) {
        if (!EmeraldWand.isUpgraded(stack)){
            player.displayClientMessage(SELECT_FAIL_NOT_UPGRADED, true);
            return InteractionResult.SUCCESS;
        }
        if (VillagerUtil.isUnemployed(villager) || VillagerUtil.isNitwit(villager)) {
            player.displayClientMessage(SELECT_FAIL_NOT_OCCUPIED, true);
            return InteractionResult.SUCCESS;
        }

        if (player instanceof ServerPlayer serverPlayer){
            NetworkHandler.sendOpenTradeSelectionToPlayer(serverPlayer, new PacketOpenTradeSelection(
                    villager.getId(),
                    villager.getOffers(),
                    villager.getVillagerData().getLevel(),
                    villager.getVillagerData().getProfession().name(),
                    villager.getVillagerData().getType().toString()
            ));
        }
        return InteractionResult.CONSUME;
    }
}
