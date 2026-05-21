package net.lostpatrol.tradetweaks.common.item.villager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nonnull;
import java.util.Optional;

public class ReversionBottleItem extends Item {
    public ReversionBottleItem(Properties properties) {
        super(properties);
    }

    @Override
    @Nonnull
    public InteractionResult interactLivingEntity(@Nonnull ItemStack stack, @Nonnull Player player,
                                                  @Nonnull LivingEntity target, @Nonnull InteractionHand hand) {
        if (!(target instanceof Villager villager) || !player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        if (player.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        ServerLevel level = (ServerLevel) player.level();
        if (!ForgeEventFactory.canLivingConvert(villager, EntityType.ZOMBIE_VILLAGER, timer -> {})) {
            return InteractionResult.FAIL;
        }

        VillagerData villagerData = villager.getVillagerData();
        Tag gossips = villager.getGossips().store(NbtOps.INSTANCE);
        CompoundTag tradeOffers = villager.getOffers().createTag();
        int villagerXp = villager.getVillagerXp();
        Optional<Tag> jobSite = VillagerConversionMemory.encodeJobSite(villager);

        ZombieVillager zombieVillager = villager.convertTo(EntityType.ZOMBIE_VILLAGER, false);
        if (zombieVillager == null) {
            return InteractionResult.FAIL;
        }

        DifficultyInstance difficulty = level.getCurrentDifficultyAt(zombieVillager.blockPosition());
        SpawnGroupData spawnData = new Zombie.ZombieGroupData(false, true);
        zombieVillager.finalizeSpawn(level, difficulty, MobSpawnType.CONVERSION, spawnData, (CompoundTag) null);
        zombieVillager.setVillagerData(villagerData);
        zombieVillager.setGossips(gossips);
        zombieVillager.setTradeOffers(tradeOffers);
        zombieVillager.setVillagerXp(villagerXp);
        jobSite.ifPresent(tag -> VillagerConversionMemory.storeJobSite(zombieVillager, tag));
        ForgeEventFactory.onLivingConvert(villager, zombieVillager);

        level.levelEvent(null, 1026, zombieVillager.blockPosition(), 0);
        VillagerItemEffects.spawnBloodParticles(level, zombieVillager);
        VillagerItemEffects.consumeOne(player, stack);
        player.awardStat(Stats.ITEM_USED.get(this));
        player.swing(hand, true);
        return InteractionResult.SUCCESS;
    }
}
