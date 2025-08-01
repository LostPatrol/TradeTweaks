package net.lostpatrol.tradetweaks.common.wand.handler;

import net.lostpatrol.tradetweaks.config.ServerConfig;
import net.lostpatrol.tradetweaks.util.VillagerUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class HandlerUpgradeVillager {
    public static final Component UPGRADE_FAIL_NOT_OCCUPIED = Component.translatable("tradetweaks.emerald_wand.fail.villager_not_occupied").withStyle(ChatFormatting.RED);
    public static final Component UPGRADE_FAIL_MAX_LEVEL = Component.translatable("tradetweaks.emerald_wand.fail.villager_max_level").withStyle(ChatFormatting.WHITE);
    public static final Component UPGRADE_FAIL_INSUFFICIENT_FUNDS = Component.translatable("tradetweaks.emerald_wand.fail.insufficient_funds").withStyle(ChatFormatting.RED);

    // Default cost list (can be overridden by config)
    private static List<ItemCost> upgradeCosts = List.of(
            new ItemCost(Items.EMERALD_BLOCK, 1),
            new ItemCost(Items.EMERALD, 9)
    );

    public static void reloadCosts() {
        upgradeCosts = ServerConfig.loadCosts();
    }

    public static InteractionResult handle(Player player, Villager villager) {
        if (VillagerUtil.isUnemployed(villager) || VillagerUtil.isNitwit(villager)) {
            player.displayClientMessage(UPGRADE_FAIL_NOT_OCCUPIED, true);
            return InteractionResult.SUCCESS;
        }

        if (villager.getVillagerData().getLevel() >= 5) {
            player.displayClientMessage(UPGRADE_FAIL_MAX_LEVEL, true);
            return InteractionResult.SUCCESS;
        }

        if (!consumeUpgradeCost(player)) {
            player.displayClientMessage(UPGRADE_FAIL_INSUFFICIENT_FUNDS, true);
            return InteractionResult.SUCCESS;
        }

        if (villager.increaseProfessionLevelOnUpdate)
            villager.increaseProfessionLevelOnUpdate=false;
        villager.increaseMerchantCareer();
        villager.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
//        villager.handleEntityEvent((byte) 14);    this doesn't work, so we add particles manually
        spawnHappyParticles(villager);

        return InteractionResult.SUCCESS;
    }

    private static boolean consumeUpgradeCost(Player player) {
        AtomicBoolean success = new AtomicBoolean(false);

        player.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .ifPresent(handler -> {
                    // Try each cost option in order
                    for (ItemCost cost : upgradeCosts) {
                        int total = findTotal(handler, cost.item());
                        if (total >= cost.amount()) {
                            removeExactly(handler, cost.item(), cost.amount());
                            success.set(true);
                            break; // Stop after first successful consumption
                        }
                    }
                });

        return success.get();
    }

    private static int findTotal(IItemHandler handler, Item item) {
        int total = 0;
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.getItem() == item) {
                total += stack.getCount();
            }
        }
        return total;
    }

    private static void removeExactly(IItemHandler handler, Item item, int amount) {
        int remaining = amount;
        for (int i = 0; i < handler.getSlots() && remaining > 0; i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.getItem() == item) {
                int toExtract = Math.min(remaining, stack.getCount());
                handler.extractItem(i, toExtract, false);
                remaining -= toExtract;
            }
        }
    }

    private static void spawnHappyParticles(Villager villager) {
        if (!(villager.level() instanceof ServerLevel level)) return;

        RandomSource random = villager.getRandom();
        for (int i = 0; i < 5; i++) {
            double x = villager.getRandomX((double)1.0F);
            double y = villager.getRandomY() + (double)1.0F;
            double z = villager.getRandomZ((double)1.0F);

            level.sendParticles(
                    ParticleTypes.HAPPY_VILLAGER,
                    x, y, z,
                    1,
                    random.nextGaussian() * 0.02,
                    random.nextGaussian() * 0.02,
                    random.nextGaussian() * 0.02,
                    0.5
            );
        }
    }

    public record ItemCost(Item item, int amount) {
        public ItemCost {
            Objects.requireNonNull(item, "Item cannot be null");
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be positive");
            }
        }
    }
}
