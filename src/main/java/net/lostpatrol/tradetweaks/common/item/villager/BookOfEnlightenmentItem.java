package net.lostpatrol.tradetweaks.common.item.villager;

import net.lostpatrol.tradetweaks.util.VillagerUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class BookOfEnlightenmentItem extends Item {
    public BookOfEnlightenmentItem(Properties properties) {
        super(properties);
    }

    @Override
    @Nonnull
    public InteractionResult interactLivingEntity(@Nonnull ItemStack stack, @Nonnull Player player,
                                                  @Nonnull LivingEntity target, @Nonnull InteractionHand hand) {
        if (!(target instanceof Villager villager) || !VillagerUtil.isNitwit(villager)) {
            return InteractionResult.PASS;
        }

        if (player.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ServerLevel level = (ServerLevel) player.level();
        villager.setVillagerData(villager.getVillagerData()
                .setType(VillagerType.byBiome(level.getBiome(villager.blockPosition())))
                .setProfession(VillagerProfession.NONE)
                .setLevel(1));
        villager.setVillagerXp(0);
        villager.getOffers().clear();
        villager.refreshBrain(level);

        VillagerItemEffects.consumeOne(player, stack);
        player.awardStat(Stats.ITEM_USED.get(this));
        VillagerItemEffects.playSound(level, villager, SoundEvents.PLAYER_LEVELUP);
        VillagerItemEffects.spawnEmeraldPattern(level, villager);
        player.swing(hand, true);
        return InteractionResult.SUCCESS;
    }
}
