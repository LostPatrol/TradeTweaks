package net.lostpatrol.tradetweaks.common.item.villager;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public final class VillagerItemEffects {
    private static final DustParticleOptions BLOOD = new DustParticleOptions(new Vector3f(0.65F, 0.0F, 0.0F), 1.0F);

    private VillagerItemEffects() {
    }

    public static void consumeOne(Player player, ItemStack stack) {
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
    }

    public static void playSound(Level level, LivingEntity entity, SoundEvent sound) {
        level.playSound(null, entity.blockPosition(), sound, SoundSource.NEUTRAL, 1.0F, 1.0F);
    }

    public static void spawnEmeraldPattern(ServerLevel level, LivingEntity entity) {
        ItemParticleOption emerald = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.EMERALD));
        double centerX = entity.getX();
        double centerY = entity.getY() + entity.getBbHeight() * 0.65D;
        double centerZ = entity.getZ();
        double width = Math.max(0.45D, entity.getBbWidth());

        double[][] points = new double[][]{
                {0.0D, 0.45D, 0.0D},
                {width, 0.0D, 0.0D},
                {0.0D, -0.45D, 0.0D},
                {-width, 0.0D, 0.0D},
                {0.0D, 0.45D, 0.0D}
        };

        for (int i = 0; i < points.length - 1; i++) {
            spawnLine(level, emerald, centerX, centerY, centerZ, points[i], points[i + 1], 5);
        }
        level.sendParticles(emerald, centerX, centerY, centerZ, 8, 0.12D, 0.12D, 0.12D, 0.02D);
    }

    public static void spawnSoulParticles(ServerLevel level, LivingEntity entity) {
        level.sendParticles(
                ParticleTypes.SOUL,
                entity.getX(),
                entity.getY() + entity.getBbHeight() * 0.55D,
                entity.getZ(),
                36,
                entity.getBbWidth() * 0.65D,
                entity.getBbHeight() * 0.35D,
                entity.getBbWidth() * 0.65D,
                0.04D
        );
        level.sendParticles(
                ParticleTypes.SOUL_FIRE_FLAME,
                entity.getX(),
                entity.getY() + 0.2D,
                entity.getZ(),
                16,
                entity.getBbWidth() * 0.5D,
                entity.getBbHeight() * 0.25D,
                entity.getBbWidth() * 0.5D,
                0.02D
        );
    }

    public static void spawnBloodParticles(ServerLevel level, LivingEntity entity) {
        level.sendParticles(
                BLOOD,
                entity.getX(),
                entity.getY() + entity.getBbHeight() * 0.55D,
                entity.getZ(),
                48,
                entity.getBbWidth() * 0.7D,
                entity.getBbHeight() * 0.35D,
                entity.getBbWidth() * 0.7D,
                0.08D
        );
    }

    public static void spawnFireworkTrail(ServerLevel level, LivingEntity entity) {
        double baseY = entity.getY() + 0.1D;
        double height = entity.getBbHeight() + 0.75D;
        double radius = Math.max(0.35D, entity.getBbWidth() * 0.65D);

        for (int i = 0; i < 28; i++) {
            double progress = (double) i / 27.0D;
            double angle = progress * Math.PI * 4.0D;
            double currentRadius = radius * (0.55D + progress * 0.45D);
            double x = entity.getX() + Math.cos(angle) * currentRadius;
            double y = baseY + height * progress;
            double z = entity.getZ() + Math.sin(angle) * currentRadius;
            level.sendParticles(ParticleTypes.FIREWORK, x, y, z, 1, 0.01D, 0.02D, 0.01D, 0.03D);
        }

        level.sendParticles(
                ParticleTypes.FIREWORK,
                entity.getX(),
                entity.getY() + entity.getBbHeight() * 0.55D,
                entity.getZ(),
                20,
                entity.getBbWidth() * 0.45D,
                entity.getBbHeight() * 0.45D,
                entity.getBbWidth() * 0.45D,
                0.04D
        );
    }

    private static void spawnLine(ServerLevel level, ItemParticleOption particle, double centerX, double centerY,
                                  double centerZ, double[] from, double[] to, int steps) {
        for (int i = 0; i <= steps; i++) {
            double progress = (double) i / (double) steps;
            double x = centerX + from[0] + (to[0] - from[0]) * progress;
            double y = centerY + from[1] + (to[1] - from[1]) * progress;
            double z = centerZ + from[2] + (to[2] - from[2]) * progress;
            level.sendParticles(particle, x, y, z, 1, 0.01D, 0.01D, 0.01D, 0.0D);
        }
    }
}
