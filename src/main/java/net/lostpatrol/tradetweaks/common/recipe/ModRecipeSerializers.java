package net.lostpatrol.tradetweaks.common.recipe;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, TradeTweaks.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RestorationBottleRecipe>> RESTORATION_BOTTLE =
            SERIALIZERS.register("restoration_bottle", () -> new SimpleCraftingRecipeSerializer<>(RestorationBottleRecipe::new));

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
