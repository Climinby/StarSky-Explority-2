package com.climinby.starsky_explority.client.rei.analyzing;

import com.climinby.starsky_explority.recipe.AnalysisRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnalyzingDisplay extends BasicDisplay {
    public AnalyzingDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        super(inputs, outputs);
    }

    public AnalyzingDisplay(RecipeEntry<AnalysisRecipe> recipe) {
        this(getInputList(recipe.value()), getOutputList(recipe.value()));
    }

    private static List<EntryIngredient> getInputList(AnalysisRecipe recipe) {
        if(recipe == null) return Collections.emptyList();
        List<EntryIngredient> list = new ArrayList<>(EntryIngredients.ofIngredients(recipe.getIngredients()));
        return list;
    }

    private static List<EntryIngredient> getOutputList(AnalysisRecipe recipe) {
        if(recipe == null) return Collections.emptyList();
        List<EntryIngredient> list = new ArrayList<>();
        for(ItemStack result : recipe.getResults()) {
            list.add(EntryIngredients.of(result));
        }
        return list;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return AnalyzingCategory.ANALYZING;
    }
}
