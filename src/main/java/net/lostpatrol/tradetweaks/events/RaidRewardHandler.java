/** Awards hero totems when the observed raid-win statistic increases. */
package net.lostpatrol.tradetweaks.events;

import net.lostpatrol.tradetweaks.common.item.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RaidRewardHandler {
    /** Last observed win counter for each connected player. */
    private static final Map<UUID, Integer> LAST_RAID_WINS = new HashMap<>();

    /** Grants rewards only when the player's raid-win counter advances. */
    public static void onPlayerTick(net.minecraft.world.entity.player.Player tickPlayer) {
        if (tickPlayer.level().isClientSide()) {
            return;
        }

        if (!(tickPlayer instanceof ServerPlayer player)) {
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

    /** Discards the cached raid counter on logout. */
    public static void onPlayerLogout(net.minecraft.world.entity.player.Player player) {
        LAST_RAID_WINS.remove(player.getUUID());
    }

    /** Grants the existing reward range for the player's current hero effect. */
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
