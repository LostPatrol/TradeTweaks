package net.lostpatrol.tradetweaks.events;

import net.lostpatrol.tradetweaks.common.item.ModItems;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.StatAwardEvent;

public class RaidRewardHandler {
    @SubscribeEvent
    public static void onStatAward(StatAwardEvent event) {
        if (!event.getStat().equals(Stats.CUSTOM.get(Stats.RAID_WIN))) {
            return;
        }

        if (event.getEntity().level().isClientSide()) {
            return;
        }

        MobEffectInstance heroEffect = event.getEntity().getEffect(MobEffects.HERO_OF_THE_VILLAGE);
        if (heroEffect == null) {
            return;
        }

        int raidLevel = Math.clamp(heroEffect.getAmplifier() + 1, 1, 5);
        int amount = raidLevel * 2 + event.getEntity().getRandom().nextInt(3);
        ItemStack reward = new ItemStack(ModItems.TOTEM_OF_VILLAGE_HERO.get(), amount);
        if (!event.getEntity().addItem(reward)) {
            event.getEntity().drop(reward, false);
        }
    }
}
