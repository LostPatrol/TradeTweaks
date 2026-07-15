package net.lostpatrol.tradetweaks.common.recipe;

import net.lostpatrol.tradetweaks.common.item.ModItems;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class RestorationBottleRecipe extends CustomRecipe {
    public RestorationBottleRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        boolean foundGoldenApple = false;
        boolean foundWeaknessPotion = false;
        boolean foundClock = false;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            if (stack.is(Items.GOLDEN_APPLE) && !foundGoldenApple) {
                foundGoldenApple = true;
            } else if (isWeaknessPotion(stack) && !foundWeaknessPotion) {
                foundWeaknessPotion = true;
            } else if (stack.is(Items.CLOCK) && !foundClock) {
                foundClock = true;
            } else {
                return false;
            }
        }

        return foundGoldenApple && foundWeaknessPotion && foundClock;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        return new ItemStack(ModItems.RESTORATION_BOTTLE.get());
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 3;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return new ItemStack(ModItems.RESTORATION_BOTTLE.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.RESTORATION_BOTTLE.get();
    }

    private static boolean isWeaknessPotion(ItemStack stack) {
        if (!stack.is(Items.POTION) && !stack.is(Items.SPLASH_POTION) && !stack.is(Items.LINGERING_POTION)) {
            return false;
        }
        return PotionUtils.getMobEffects(stack).stream()
                .anyMatch(effect -> effect.getEffect() == MobEffects.WEAKNESS);
    }
}
