package net.lostpatrol.tradetweaks.common.item.villager;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nonnull;

public class RestorationBottleItem extends Item {
    public RestorationBottleItem(Properties properties) {
        super(properties);
    }

    @Override
    @Nonnull
    public InteractionResult interactLivingEntity(@Nonnull ItemStack stack, @Nonnull Player player,
                                                  @Nonnull LivingEntity target, @Nonnull InteractionHand hand) {
        if (!(target instanceof ZombieVillager zombieVillager)) {
            return InteractionResult.PASS;
        }

        if (player.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        ServerLevel level = (ServerLevel) player.level();
        if (!ForgeEventFactory.canLivingConvert(zombieVillager, EntityType.VILLAGER, timer -> {})) {
            return InteractionResult.FAIL;
        }

        VillagerItemEffects.spawnSoulParticles(level, zombieVillager);
        zombieVillager.startConverting(player.getUUID(), 1);
        zombieVillager.finishConversion(level);

        VillagerItemEffects.consumeOne(player, stack);
        player.awardStat(Stats.ITEM_USED.get(this));
        player.swing(hand, true);
        return InteractionResult.SUCCESS;
    }
}
