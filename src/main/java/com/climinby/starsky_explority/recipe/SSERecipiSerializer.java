package com.climinby.starsky_explority.recipe;

import com.climinby.starsky_explority.StarSkyExplority;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public interface SSERecipiSerializer<T extends Recipe<?>> extends RecipeSerializer {
    RecipeSerializer<ExtractRecipe> EXTRACTING = register("extracting", new ExtractRecipeSerializer());
    RecipeSerializer<AnalysisRecipe> ANALYZING = register("analyzing", new AnalysisRecipeSerializer());

    static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String id, S serializer) {
        return Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(StarSkyExplority.MOD_ID, id), serializer);
    }

    static void init() {}
}
