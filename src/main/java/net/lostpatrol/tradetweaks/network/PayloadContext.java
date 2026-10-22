/** Provides the player and game-thread executor without exposing a loader API. */
package net.lostpatrol.tradetweaks.network;

import net.minecraft.world.entity.player.Player;
import java.util.function.Consumer;

public record PayloadContext(Player player, Consumer<Runnable> executor) {
    /** Schedules packet work on the owning game thread. */
    public void enqueueWork(Runnable work) {
        executor.accept(work);
    }
}
