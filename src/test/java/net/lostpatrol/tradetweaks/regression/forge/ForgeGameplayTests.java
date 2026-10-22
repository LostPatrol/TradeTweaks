/** Registers the shared regression suite only in the Forge test source set. */
package net.lostpatrol.tradetweaks.regression.forge;

import net.lostpatrol.tradetweaks.regression.GameplayRegressionSuite;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@Mod("tradetweaks_regression")
@GameTestHolder("tradetweaks")
@PrefixGameTestTemplate(false)
public final class ForgeGameplayTests {
    /** Runs actual shared gameplay with Forge adapters and registered events. */
    @GameTest(templateNamespace = "tradetweaks", template = "regression_empty", timeoutTicks = 200)
    public static void gameplay(GameTestHelper helper) throws Exception {
        GameplayRegressionSuite.run(helper.getLevel().getServer(), (player, villager) -> { }, ForgeGameplayTests::tickPlayer);
        helper.succeed();
    }

    /** Dispatches the real END player tick used by Forge raid-reward polling. */
    private static void tickPlayer(ServerPlayer player) {
        MinecraftForge.EVENT_BUS.post(new TickEvent.PlayerTickEvent(TickEvent.Phase.END, player));
    }
}
