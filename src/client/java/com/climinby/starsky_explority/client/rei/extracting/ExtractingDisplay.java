package com.climinby.starsky_explority.client.rei.extracting;

import com.climinby.starsky_explority.recipe.ExtractRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExtractingDisplay extends BasicDisplay {
    public ExtractingDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        super(inputs, outputs);
    }

    public ExtractingDisplay(RecipeEntry<ExtractRecipe> recipe) {
        this(getInputList(recipe.value()), getOutputList(recipe.value()));
    }

    private static List<EntryIngredient> getInputList(ExtractRecipe recipe) {
        if(recipe == null) return Collections.emptyList();
        List<EntryIngredient> list = new ArrayList<>(EntryIngredients.ofIngredients(recipe.getIngredients()));
        return list;
    }

    private static List<EntryIngredient> getOutputList(ExtractRecipe recipe) {
        if(recipe == null) return Collections.emptyList();
        List<EntryIngredient> list = new ArrayList<>();
        for(ItemStack result : recipe.getResults()) {
            list.add(EntryIngredients.of(result));
        }
        return list;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ExtractingCategory.EXTRACTING;
    }
}
