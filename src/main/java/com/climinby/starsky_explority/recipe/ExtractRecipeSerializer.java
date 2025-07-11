package com.climinby.starsky_explority.recipe;

import com.climinby.starsky_explority.StarSkyExplority;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

import java.util.List;

public class ExtractRecipeSerializer implements RecipeSerializer<ExtractRecipe> {
    public static final ExtractRecipeSerializer INSTANCE = new ExtractRecipeSerializer();
    public static final Identifier ID = new Identifier(StarSkyExplority.MOD_ID, "extract");

    @Override
    public Codec<ExtractRecipe> codec() {
        return RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("id").forGetter(ExtractRecipe::getId),
                ItemStack.CODEC.fieldOf("input").forGetter(ExtractRecipe::getInput),
                ItemStack.CODEC.listOf().fieldOf("result").forGetter(ExtractRecipe::getResults)
        ).apply(instance, ExtractRecipe::new));
    }

    @Override
    public ExtractRecipe read(PacketByteBuf buf) {
        Identifier id = buf.readIdentifier();
        ItemStack input = buf.readItemStack();
        List<ItemStack> results = buf.readList(PacketByteBuf::readItemStack);
        return new ExtractRecipe(id, input, results);
    }

    @Override
    public void write(PacketByteBuf buf, ExtractRecipe recipe) {
        buf.writeIdentifier(recipe.getId()).writeItemStack(recipe.getInput()).writeCollection(recipe.getResults(), PacketByteBuf::writeItemStack);
    }
}
