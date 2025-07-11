package com.climinby.starsky_explority.recipe;

import com.climinby.starsky_explority.StarSkyExplority;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public interface SSERecipeType<T extends Recipe<?>> {
    RecipeType<ExtractRecipe> EXTRACTING = register("extracting");
    RecipeType<AnalysisRecipe> ANALYZING = register("analyzing");

    static <T extends Recipe<?>> RecipeType<T> register(String id) {
        return Registry.register(Registries.RECIPE_TYPE, new Identifier(StarSkyExplority.MOD_ID, id), new RecipeType<T>() {
            @Override
            public String toString() {
                return new Identifier(StarSkyExplority.MOD_ID, id).toString();
            }
        });
    }

    static void init() {}
}
