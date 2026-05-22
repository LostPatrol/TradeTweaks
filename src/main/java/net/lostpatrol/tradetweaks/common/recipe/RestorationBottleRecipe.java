package net.lostpatrol.tradetweaks.common.recipe;

import net.lostpatrol.tradetweaks.common.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class RestorationBottleRecipe extends CustomRecipe {
    public RestorationBottleRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput container, Level level) {
        boolean foundGoldenApple = false;
        boolean foundWeaknessPotion = false;

        for (int i = 0; i < container.size(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            if (stack.is(Items.GOLDEN_APPLE) && !foundGoldenApple) {
                foundGoldenApple = true;
            } else if (isWeaknessPotion(stack) && !foundWeaknessPotion) {
                foundWeaknessPotion = true;
            } else {
                return false;
            }
        }

        return foundGoldenApple && foundWeaknessPotion;
    }

    @Override
    public ItemStack assemble(CraftingInput container, HolderLookup.Provider registries) {
        return new ItemStack(ModItems.RESTORATION_BOTTLE.get());
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
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
        for (var effect : stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getAllEffects()) {
            if (effect.getEffect().is(MobEffects.WEAKNESS)) {
                return true;
            }
        }
        return false;
    }
}
