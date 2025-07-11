package com.climinby.starsky_explority.item;

import com.climinby.starsky_explority.StarSkyExplority;
import com.climinby.starsky_explority.block.SSEBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SSEItemGroups {
    public static final RegistryKey<ItemGroup> SSE_TOOLS_AND_WEAPONS_KEY = RegistryKey.of(
            Registries.ITEM_GROUP.getKey(),
            new Identifier(StarSkyExplority.MOD_ID, "tools_and_weapons")
    );
    public static final RegistryKey<ItemGroup> SSE_INGREDIENTS_KEY = RegistryKey.of(
            Registries.ITEM_GROUP.getKey(),
            new Identifier(StarSkyExplority.MOD_ID, "ingredients")
    );
    public static final RegistryKey<ItemGroup> SSE_FOOD_KEY = RegistryKey.of(
            Registries.ITEM_GROUP.getKey(),
            new Identifier(StarSkyExplority.MOD_ID, "food")
    );
    public static final RegistryKey<ItemGroup> SSE_BLOCKS_KEY = RegistryKey.of(
            Registries.ITEM_GROUP.getKey(),
            new Identifier(StarSkyExplority.MOD_ID, "blocks")
    );
    public static final RegistryKey<ItemGroup> SSE_ENTITIES_KEY = RegistryKey.of(
            Registries.ITEM_GROUP.getKey(),
            new Identifier(StarSkyExplority.MOD_ID, "entities")
    );

    public static final ItemGroup SSE_TOOLS_AND_WEAPONS = FabricItemGroup.builder()
            .icon(() -> new ItemStack(SSEItems.DIAMOND_COLLECTOR))
            .displayName(Text.translatable("itemGroup.starsky_explority.tools_and_weapons"))
            .build();
    public static final ItemGroup SSE_INGREDIENTS = FabricItemGroup.builder()
            .icon(() -> new ItemStack(SSEItems.STELLARIUM_INGOT))
            .displayName(Text.translatable("itemGroup.starsky_explority.ingredients"))
            .build();
    public static final ItemGroup SSE_FOOD = FabricItemGroup.builder()
            .icon(() -> new ItemStack(SSEItems.MOON_CAKE))
            .displayName(Text.translatable("itemGroup.starsky_explority.food"))
            .build();
    public static final ItemGroup SSE_BLOCKS = FabricItemGroup.builder()
            .icon(() -> new ItemStack(SSEBlocks.MOON_SOIL))
            .displayName(Text.translatable("itemGroup.starsky_explority.blocks"))
            .build();
    public static final ItemGroup SSE_ENTITIES = FabricItemGroup.builder()
            .icon(() -> new ItemStack(SSEItems.LUNARIAN_SPAWN_EGG))
            .displayName(Text.translatable("itemGroup.starsky_explority.entities"))
            .build();

    public static void initialize() {
        Registry.register(Registries.ITEM_GROUP, SSE_TOOLS_AND_WEAPONS_KEY, SSE_TOOLS_AND_WEAPONS);
        ItemGroupEvents.modifyEntriesEvent(SSE_TOOLS_AND_WEAPONS_KEY)
                .register(itemGroup -> {
                    itemGroup.add(SSEItems.IRON_COLLECTOR);
                    itemGroup.add(SSEItems.DIAMOND_COLLECTOR);
                    itemGroup.add(SSEItems.NETHERITE_COLLECTOR);
                });

        Registry.register(Registries.ITEM_GROUP, SSE_INGREDIENTS_KEY, SSE_INGREDIENTS);
        ItemGroupEvents.modifyEntriesEvent(SSE_INGREDIENTS_KEY)
                .register(itemGroup -> {
                    itemGroup.add(SSEItems.SAMPLE_EMPTY);
                    itemGroup.add(SSEItems.SAMPLE_EARTH);
                    itemGroup.add(SSEItems.SAMPLE_MOON);
                    itemGroup.add(SSEItems.SAMPLE_MARS);
                    itemGroup.add(SSEItems.SAMPLE_VENUS);
                    itemGroup.add(SSEItems.SAMPLE_MERCURY);
                    itemGroup.add(SSEItems.RAW_ALUMINIUM);
                    itemGroup.add(SSEItems.RAW_SILVER);
                    itemGroup.add(SSEItems.ALUMINIUM_INGOT);
                    itemGroup.add(SSEItems.SILVER_NUGGET);
                    itemGroup.add(SSEItems.SILVER_INGOT);
                    itemGroup.add(SSEItems.LUNAR_CRYSTAL);
                    itemGroup.add(SSEItems.STELLARIUM_INGOT);
                    itemGroup.add(SSEItems.RESEARCH_BOOK);
                    itemGroup.add(SSEItems.RESEARCH_BOOK_ALUMINIUM);
                    itemGroup.add(SSEItems.RESEARCH_BOOK_SILVER);
                    itemGroup.add(SSEItems.RESEARCH_BOOK_LUNAR_CRYSTAL);
                    itemGroup.add(SSEItems.RESEARCH_BOOK_STELLARIUM);
                    itemGroup.add(SSEItems.MOON_PEBBLE);
                });

        Registry.register(Registries.ITEM_GROUP, SSE_FOOD_KEY, SSE_FOOD);
        ItemGroupEvents.modifyEntriesEvent(SSE_FOOD_KEY)
                .register(itemGroup -> {
                    itemGroup.add(SSEItems.MOON_CAKE);
                    itemGroup.add(SSEItems.SILVER_MOON_CAKE);
                });

        Registry.register(Registries.ITEM_GROUP, SSE_BLOCKS_KEY, SSE_BLOCKS);
        ItemGroupEvents.modifyEntriesEvent(SSE_BLOCKS_KEY)
                .register(itemGroup -> {
                    itemGroup.add(SSEBlocks.MOONVEIL_MOSS);
                    itemGroup.add(SSEBlocks.MOON_SOIL);
                    itemGroup.add(SSEBlocks.MOON_ROCK);
                    itemGroup.add(SSEBlocks.ANORTHOSITE);
                    itemGroup.add(SSEBlocks.ANALYZER);
                    itemGroup.add(SSEBlocks.EXTRACTOR);
                });

        Registry.register(Registries.ITEM_GROUP, SSE_ENTITIES_KEY, SSE_ENTITIES);
        ItemGroupEvents.modifyEntriesEvent(SSE_ENTITIES_KEY)
                .register(itemGroup -> {
                    itemGroup.add(SSEItems.LUNARIAN_SPAWN_EGG);
                });
    }
}
