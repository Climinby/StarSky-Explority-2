package com.climinby.starsky_explority.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;

import java.util.List;

public class AnalysisRecipeSerializer implements RecipeSerializer<AnalysisRecipe> {
    @Override
    public Codec<AnalysisRecipe> codec() {
        return RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.fieldOf("input").forGetter(AnalysisRecipe::getInput),
                ItemStack.CODEC.listOf().fieldOf("result").forGetter(AnalysisRecipe::getResults),
                Codec.INT.listOf().fieldOf("weight").forGetter(AnalysisRecipe::getWeights)
        ).apply(instance, AnalysisRecipe::new));
    }

    @Override
    public AnalysisRecipe read(PacketByteBuf buf) {
        ItemStack input = buf.readItemStack();
        List<ItemStack> results = buf.readList(PacketByteBuf::readItemStack);
        List<Integer> weights = buf.readList(PacketByteBuf::readInt);
        return new AnalysisRecipe(input, results, weights);
    }

    @Override
    public void write(PacketByteBuf buf, AnalysisRecipe recipe) {
        buf.writeItemStack(recipe.getInput()).writeCollection(recipe.getResults(), PacketByteBuf::writeItemStack);
        buf.writeCollection(recipe.getWeights(), PacketByteBuf::writeInt);
    }
}
