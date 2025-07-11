package com.climinby.starsky_explority.recipe;

import com.climinby.starsky_explority.inventory.AnalysisInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnalysisRecipe implements Recipe<AnalysisInventory> {
    private final ItemStack input;
    private final ItemStack resultA;
    private final ItemStack resultB;
    private final ItemStack resultC;
    private final ItemStack resultD;
    private final ItemStack resultE;
    private final int weightA;
    private final int weightB;
    private final int weightC;
    private final int weightD;
    private final int weightE;
    private final int sumWeight;
    private final List<ItemStack> results = new ArrayList<>(5);
    private final List<Float> odds = new ArrayList<>(5);
    private final List<Integer> weights = new ArrayList<>(5);
    private final RecipeType<?> type;

    public AnalysisRecipe(ItemStack input, List<ItemStack> results, List<Integer> weights) {
        this.input = input;
        resultA = results.get(0);
        resultB = results.get(1);
        resultC = results.get(2);
        resultD = results.get(3);
        resultE = results.get(4);
        weightA = weights.get(0);
        weightB = weights.get(1);
        weightC = weights.get(2);
        weightD = weights.get(3);
        weightE = weights.get(4);
        this.results.addAll(results);
        this.weights.addAll(weights);
        final Float sumWeight = Float.valueOf(weights.stream().reduce(Integer::sum).get());
        this.sumWeight = weights.stream().reduce(Integer::sum).get();
        weights.stream().forEach(integer -> odds.add(Float.valueOf(integer) / sumWeight));
        type = SSERecipeType.ANALYZING;
    }

    @Override
    public boolean matches(AnalysisInventory inventory, World world) {
        if(inventory.size() < 8) return false;
        return !inventory.getStack(0).isEmpty() && inventory.getStack(0).isOf(input.getItem());
    }

    @Override
    public ItemStack craft(AnalysisInventory inventory, DynamicRegistryManager registryManager) {
        ItemStack result = ItemStack.EMPTY;
        int ran = new Random().nextInt(sumWeight);
        for(int i = 0; i < 5; i++) {
            int partialWeight = 0;
            for(int j = 0; j < i + 1; j++) {
                partialWeight += weights.get(j);
            }
            if(ran < partialWeight) {
                result = results.get(i);
            }
        }
        inventory.setStack(2, result);

        inventory.removeStack(0, 1);

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
        return SSERecipiSerializer.ANALYZING;
    }

    @Override
    public RecipeType<?> getType() {
        return type;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> ingredients = DefaultedList.of();
        ingredients.add(Ingredient.ofStacks(input));
        return ingredients;
    }

    public ItemStack getInput() {
        return input;
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

    public ItemStack getResultD() {
        return resultD;
    }

    public ItemStack getResultE() {
        return resultE;
    }

    public int getWeightA() {
        return weightA;
    }

    public int getWeightB() {
        return weightB;
    }

    public int getWeightC() {
        return weightC;
    }

    public int getWeightD() {
        return weightD;
    }

    public int getWeightE() {
        return weightE;
    }

    public List<ItemStack> getResults() {
        return results;
    }

    public List<Float> getOdds() {
        return odds;
    }

    public List<Integer> getWeights() {
        return weights;
    }

    public int getSumWeight() {
        return sumWeight;
    }
}
