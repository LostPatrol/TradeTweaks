/** Records client mixin/registry startup after a stable title screen, then exits. */
package net.lostpatrol.tradetweaks.regression.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ClientSmoke {
    // Allow normal title-screen rendering for one second before recording startup.
    private static final int REQUIRED_TITLE_TICKS = 20;
    private static int titleTicks;
    private static boolean finished;

    /** Runs only when an explicitly configured test client enables this smoke check. */
    public static void tick(Minecraft client) {
        if (!Boolean.getBoolean("tradetweaks.clientSmoke") || finished) return;
        titleTicks = client.screen instanceof TitleScreen ? titleTicks + 1 : 0;
        if (titleTicks < REQUIRED_TITLE_TICKS) return;
        Path result = Path.of(System.getProperty("tradetweaks.clientSmokeResult", "build/client-smoke-result.txt"));
        try {
            Files.createDirectories(result.toAbsolutePath().getParent());
            Files.writeString(result, "PASS titleScreenTicks=" + titleTicks + System.lineSeparator());
        } catch (Exception failure) {
            throw new IllegalStateException("Cannot record client smoke result", failure);
        }
        finished = true;
        System.out.println("TRADETWEAKS CLIENT SMOKE PASS title screen rendered");
        client.stop();
    }
}
