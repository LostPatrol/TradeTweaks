package net.lostpatrol.tradetweaks.common.item.villager;

import net.lostpatrol.tradetweaks.common.item.ModItems;
import net.lostpatrol.tradetweaks.network.NetworkHandler;
import net.lostpatrol.tradetweaks.network.packet.PacketItemActivation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class TotemOfVillageHeroItem extends Item {
    private static final int DURATION_INCREMENT = 5 * 60 * 20;
    private static final int MAX_AMPLIFIER = 4;

    public TotemOfVillageHeroItem(Properties properties) {
        super(properties);
    }

    @Override
    @Nonnull
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player,
                                                  @Nonnull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (level.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }

        MobEffectInstance current = player.getEffect(MobEffects.HERO_OF_THE_VILLAGE);
        int amplifier = 0;
        int duration = DURATION_INCREMENT;
        if (current != null) {
            amplifier = Math.min(current.getAmplifier() + 1, MAX_AMPLIFIER);
            duration = current.getDuration() + DURATION_INCREMENT;
        }

        player.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, duration, amplifier));
        VillagerItemEffects.consumeOne(player, stack);
        player.awardStat(Stats.ITEM_USED.get(this));
        ServerLevel serverLevel = (ServerLevel) level;
        VillagerItemEffects.spawnFireworkTrail(serverLevel, player);
        level.playSound(null, player.blockPosition(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.AMBIENT, 3.0F, 1.0F);
        if (player instanceof ServerPlayer serverPlayer) {
            NetworkHandler.sendItemActivationToPlayer(
                    serverPlayer,
                    new PacketItemActivation(new ItemStack(ModItems.TOTEM_OF_VILLAGE_HERO.get()))
            );
        }
        return InteractionResultHolder.success(stack);
    }
}
