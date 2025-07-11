package com.climinby.starsky_explority.recipe;

import com.climinby.starsky_explority.inventory.ExtractInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ExtractRecipe implements Recipe<ExtractInventory> {
    private final ItemStack input;
    private final ItemStack resultA;
    private final ItemStack resultB;
    private final ItemStack resultC;
    private final List<ItemStack> results = new ArrayList<>(3);
    private final Identifier id;
    protected final RecipeType<?> type;

    public ExtractRecipe(ItemStack input, ItemStack resultA, ItemStack resultB, ItemStack resultC, Identifier id, RecipeType<?> type) {
        this.input = input;
        this.resultA = resultA;
        this.resultB = resultB;
        this.resultC = resultC;
        results.add(resultA);
        results.add(resultB);
        results.add(resultC);
        this.id = id;
        this.type = type;
    }
    public ExtractRecipe(Identifier id, ItemStack input, List<ItemStack> results) {
        this.results.addAll(results);
        this.input = input;
        this.id = id;
        resultA = results.get(0);
        resultB = results.get(1);
        resultC = results.get(2);
        type = SSERecipeType.EXTRACTING;
    }

    @Override
    public boolean matches(ExtractInventory inventory, World world) {
        if(inventory.size() < 4) return false;
        return !inventory.getStack(0).isEmpty() && inventory.getStack(0).isOf(input.getItem());
    }

    @Override
    public ItemStack craft(ExtractInventory inventory, DynamicRegistryManager registryManager) {
        inventory.setStack(1, resultA);
        inventory.setStack(2, resultB);
        inventory.setStack(3, resultC);

        inventory.removeStack(0, input.getCount());

        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(DynamicRegistryManager registryManager) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SSERecipiSerializer.EXTRACTING;
    }

    @Override
    public RecipeType<?> getType() {
        return type;
    }

    public ItemStack getResultA() {
        return resultA;
    }

    public ItemStack getResultB() {
        return resultB;
    }

    public ItemStack getResultC() {
        return resultC;
    }

    public ItemStack getInput() {
        return input;
    }

    public Identifier getId() {
        return id;
    }

    public List<ItemStack> getResults() {
        return results;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> ingredients = DefaultedList.of();
        ingredients.add(Ingredient.ofStacks(this.input));
        return ingredients;
    }
}
