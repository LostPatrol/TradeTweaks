/** Awards the same hero totems when vanilla awards the raid-win statistic. */
package net.lostpatrol.tradetweaks.mixin.fabric;

import net.lostpatrol.tradetweaks.common.item.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    /** Uses the existing effect amplifier to preserve difficulty-scaled rewards. */
    @Inject(method = "awardStat", at = @At("HEAD"))
    private void tradetweaks$raidReward(Stat<?> stat, int amount, CallbackInfo ci) {
        if (!stat.equals(Stats.CUSTOM.get(Stats.RAID_WIN))) return;
        ServerPlayer player = (ServerPlayer) (Object) this;
        var effect = player.getEffect(MobEffects.HERO_OF_THE_VILLAGE);
        if (effect == null) return;
        int level = Math.clamp(effect.getAmplifier() + 1, 1, 5);
        ItemStack reward = new ItemStack(ModItems.TOTEM_OF_VILLAGE_HERO.get(), level * 2 + player.getRandom().nextInt(3));
        if (!player.addItem(reward)) player.drop(reward, false);
    }
}
