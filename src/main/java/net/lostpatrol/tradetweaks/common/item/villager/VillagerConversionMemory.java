package net.lostpatrol.tradetweaks.common.item.villager;

import com.mojang.serialization.DataResult;
import net.lostpatrol.tradetweaks.TradeTweaks;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingConversionEvent;

import java.util.Optional;

public final class VillagerConversionMemory {
    private static final String JOB_SITE_KEY = TradeTweaks.MODID + ":job_site";

    private VillagerConversionMemory() {
    }

    public static Optional<Tag> encodeJobSite(Villager villager) {
        return villager.getBrain()
                .getMemory(MemoryModuleType.JOB_SITE)
                .flatMap(jobSite -> GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, jobSite)
                        .resultOrPartial(TradeTweaks.LOGGER::error));
    }

    public static void storeJobSite(ZombieVillager zombieVillager, Tag jobSite) {
        zombieVillager.getPersistentData().put(JOB_SITE_KEY, jobSite);
    }

    @SubscribeEvent
    public static void onLivingConversionPost(LivingConversionEvent.Post event) {
        if (!(event.getEntity() instanceof ZombieVillager zombieVillager)
                || !(event.getOutcome() instanceof Villager villager)) {
            return;
        }

        if (!zombieVillager.getPersistentData().contains(JOB_SITE_KEY)) {
            return;
        }

        DataResult<GlobalPos> jobSite = GlobalPos.CODEC.parse(
                NbtOps.INSTANCE,
                zombieVillager.getPersistentData().get(JOB_SITE_KEY)
        );
        jobSite.resultOrPartial(TradeTweaks.LOGGER::error).ifPresent(pos -> {
            villager.getBrain().setMemory(MemoryModuleType.JOB_SITE, pos);
            if (villager.level() instanceof ServerLevel serverLevel) {
                villager.refreshBrain(serverLevel);
            }
        });
    }
}
