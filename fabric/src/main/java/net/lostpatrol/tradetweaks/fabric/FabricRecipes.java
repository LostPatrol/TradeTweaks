/** Supplies the upgraded wand output that vanilla shapeless JSON cannot express on Fabric. */
package net.lostpatrol.tradetweaks.fabric;

import com.google.gson.JsonObject;
import net.lostpatrol.tradetweaks.TradeTweaks;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public final class FabricRecipes {
    /** Keeps vanilla ingredient matching and packet encoding, adding only the output upgrade flag. */
    public static void register() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
                new ResourceLocation(TradeTweaks.MODID, "wand_upgrade"), new ShapelessRecipe.Serializer() {
                    /** Sets the upgrade state before the recipe output is copied or synchronized. */
                    @Override
                    public ShapelessRecipe fromJson(ResourceLocation id, JsonObject json) {
                        ShapelessRecipe recipe = super.fromJson(id, json);
                        recipe.getResultItem(RegistryAccess.EMPTY).getOrCreateTag().putBoolean("upgrade", true);
                        return recipe;
                    }
                });
    }
}
