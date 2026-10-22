/** Registers the shared regression suite only in the NeoForge test source set. */
package net.lostpatrol.tradetweaks.regression.neoforge;

import net.lostpatrol.tradetweaks.regression.GameplayRegressionSuite;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("tradetweaks")
@PrefixGameTestTemplate(false)
public final class NeoForgeGameplayTests {
    /** Runs actual shared gameplay with NeoForge adapters and registered events. */
    @GameTest(template = "regression_empty", timeoutTicks = 200)
    public static void gameplay(GameTestHelper helper) throws Exception {
        GameplayRegressionSuite.run(helper.getLevel().getServer());
        helper.succeed();
    }
}
