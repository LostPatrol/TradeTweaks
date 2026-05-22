package net.lostpatrol.tradetweaks.events;

import net.lostpatrol.tradetweaks.common.item.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RaidRewardHandler {
    private static final Map<UUID, Integer> LAST_RAID_WINS = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) {
            return;
        }

        if (!(event.player instanceof ServerPlayer player)) {
            return;
        }

        int currentWins = player.getStats().getValue(Stats.CUSTOM.get(Stats.RAID_WIN));
        int previousWins = LAST_RAID_WINS.getOrDefault(player.getUUID(), currentWins);
        if (currentWins <= previousWins) {
            LAST_RAID_WINS.put(player.getUUID(), currentWins);
            return;
        }

        LAST_RAID_WINS.put(player.getUUID(), currentWins);
        grantTotems(player);
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        LAST_RAID_WINS.remove(event.getEntity().getUUID());
    }

    private static void grantTotems(ServerPlayer player) {
        MobEffectInstance heroEffect = player.getEffect(MobEffects.HERO_OF_THE_VILLAGE);
        if (heroEffect == null) {
            return;
        }

        int raidLevel = Math.min(Math.max(heroEffect.getAmplifier() + 1, 1), 5);
        int amount = raidLevel * 2 + player.getRandom().nextInt(3);
        ItemStack reward = new ItemStack(ModItems.TOTEM_OF_VILLAGE_HERO.get(), amount);
        if (!player.addItem(reward)) {
            player.drop(reward, false);
        }
    }
}
