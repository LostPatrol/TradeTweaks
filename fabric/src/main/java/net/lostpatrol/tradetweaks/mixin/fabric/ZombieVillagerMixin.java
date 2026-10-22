/** Restores the original workstation after all vanilla curing steps finish. */
package net.lostpatrol.tradetweaks.mixin.fabric;

import com.llamalad7.mixinextras.sugar.Local;
import net.lostpatrol.tradetweaks.common.item.villager.VillagerConversionMemory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieVillager.class)
public abstract class ZombieVillagerMixin {
    /** Covers restoration bottles and ordinary golden-apple curing alike. */
    @Inject(method = "finishConversion", at = @At("TAIL"))
    private void tradetweaks$restoreWorkstation(ServerLevel level, CallbackInfo ci, @Local Villager villager) {
        VillagerConversionMemory.onLivingConversionPost((ZombieVillager) (Object) this, villager);
    }
}
