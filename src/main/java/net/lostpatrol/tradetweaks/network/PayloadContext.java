/** Provides the legacy server sender and game-thread executor without a loader type. */
package net.lostpatrol.tradetweaks.network;

import net.minecraft.server.level.ServerPlayer;
import java.util.function.Consumer;

public record PayloadContext(ServerPlayer player, Consumer<Runnable> executor) {
    /** Schedules decoded packet work on its receiving game thread. */
    public void enqueueWork(Runnable work) { executor.accept(work); }
}
