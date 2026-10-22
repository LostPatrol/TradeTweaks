/** Registers the shared suite as a Fabric API GameTest entrypoint. */
package net.lostpatrol.tradetweaks.regression;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.lostpatrol.tradetweaks.common.item.ModItems;
import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;

public final class FabricGameplayTests {
    /** Starts after Fabric mixins, registries and server configs are active. */
    @GameTest(template = "fabric-gametest-api-v1:empty", timeoutTicks = 200)
    public void gameplay(GameTestHelper helper) throws Exception {
        GameplayRegressionSuite.run(helper.getLevel().getServer(), (player, villager) -> {
            player.getInventory().clearContent();
            player.setShiftKeyDown(true);
            ItemStack wand = new ItemStack(ModItems.EMERALD_WAND.get());
            EmeraldWand.setMode(wand, EmeraldWand.WandMode.UPGRADE_MODE);
            player.setItemInHand(InteractionHand.MAIN_HAND, wand);
            player.getInventory().setItem(1, new ItemStack(Items.EMERALD_BLOCK, 2));
            int originalLevel = villager.getVillagerData().getLevel();
            // Normal precise entity hits must dispatch once, even without MCA installed.
            InteractionResult result = UseEntityCallback.EVENT.invoker().interact(
                    player, player.level(), InteractionHand.MAIN_HAND, villager, new EntityHitResult(villager));
            if (result != InteractionResult.SUCCESS
                    || villager.getVillagerData().getLevel() != originalLevel + 1
                    || player.getInventory().countItem(Items.EMERALD_BLOCK) != 1) {
                throw new AssertionError("Fabric precise-hit tool callback must upgrade once and charge once");
            }
            System.out.println("GAMEPLAY CHECK Fabric precise-hit callback upgrades once and charges once");
        });
        helper.succeed();
    }
}
