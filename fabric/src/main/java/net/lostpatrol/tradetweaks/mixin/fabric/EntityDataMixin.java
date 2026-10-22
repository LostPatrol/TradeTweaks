/** Persists tool cooldowns and conversion memory with their owning entity. */
package net.lostpatrol.tradetweaks.mixin.fabric;

import net.lostpatrol.tradetweaks.platform.PersistentEntityData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityDataMixin implements PersistentEntityData {
    /** Fabric-specific entity NBT storage, independent of vanilla fields. */
    @Unique private CompoundTag tradetweaks$data = new CompoundTag();

    /** Exposes the tag to shared item logic. */
    public CompoundTag tradetweaks$data() { return tradetweaks$data; }

    /** Writes persistent mod data alongside vanilla entity data. */
    @Inject(method = "saveWithoutId", at = @At("RETURN"))
    private void tradetweaks$save(CompoundTag tag, CallbackInfoReturnable<CompoundTag> ci) {
        if (!tradetweaks$data.isEmpty()) tag.put("TradeTweaksData", tradetweaks$data.copy());
    }

    /** Restores persistent mod data when chunks or entities load. */
    @Inject(method = "load", at = @At("TAIL"))
    private void tradetweaks$load(CompoundTag tag, CallbackInfo ci) {
        tradetweaks$data = tag.getCompound("TradeTweaksData").copy();
    }
}
