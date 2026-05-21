package net.lostpatrol.tradetweaks.common.recipe;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, TradeTweaks.MODID);

    public static final RegistryObject<SimpleCraftingRecipeSerializer<RestorationBottleRecipe>> RESTORATION_BOTTLE =
            SERIALIZERS.register("restoration_bottle", () -> new SimpleCraftingRecipeSerializer<>(RestorationBottleRecipe::new));

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
