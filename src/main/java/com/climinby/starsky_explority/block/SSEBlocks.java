package com.climinby.starsky_explority.block;

import com.climinby.starsky_explority.StarSkyExplority;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ColorCode;
import net.minecraft.util.Identifier;

import java.awt.*;

public class SSEBlocks {
    public static final Block MOON_SOIL = register(
            "moon_soil",
            new ColoredFallingBlock(new ColorCode(new Color(90, 99, 102).getRGB()),
                    AbstractBlock.Settings
                            .create()
                            .sounds(BlockSoundGroup.SAND)
                            .mapColor(MapColor.STONE_GRAY)
                            .strength(0.6F)
            ),
            true);
    public static final Block ANORTHOSITE = register(
            "anorthosite",
            new Block(AbstractBlock.Settings
                    .create()
                    .sounds(BlockSoundGroup.STONE)
                    .mapColor(MapColor.STONE_GRAY)
                    .requiresTool()
                    .strength(1.7F, 6.0F)
            ),
            true);
    public static final Block MOON_ROCK = register(
            "moon_rock",
            new Block(AbstractBlock.Settings
                    .create()
                    .sounds(BlockSoundGroup.STONE)
                    .mapColor(MapColor.STONE_GRAY)
                    .requiresTool()
                    .strength(1.5F, 6.0F)
            ),
            true);
    public static final Block LUNAR_SILVER_ORE = register(
            "lunar_silver_ore",
            new Block(AbstractBlock.Settings
                    .create()
                    .sounds(BlockSoundGroup.STONE)
                    .mapColor(MapColor.STONE_GRAY)
                    .requiresTool()
                    .strength(2.0F, 7.0F)
            ),
            true);
    public static final Block MOONVEIL_MOSS = register(
            "moonveil_moss",
            new ForalFallingBlock(
                    SSEBlocks.MOON_SOIL,
                    AbstractBlock.Settings
                            .create()
                            .sounds(BlockSoundGroup.SAND)
                            .mapColor(MapColor.STONE_GRAY)
                            .strength(0.8F)
            ),
            true
    );

    public static final Block ANALYZER = register("analyzer", new AnalyzerBlock(
            AbstractBlock.Settings.create()
                    .sounds(BlockSoundGroup.STONE)
                    .mapColor(MapColor.STONE_GRAY)
                    .strength(3.0F, 9.0F)
                    .requiresTool()), true);

    public static final Block EXTRACTOR = register("extractor", new ExtractorBlock(
            AbstractBlock.Settings.create()
                    .sounds(BlockSoundGroup.STONE)
                    .mapColor(MapColor.STONE_GRAY)
                    .strength(3.0F, 9.0F)
                    .requiresTool()), true);

    public static Block register(String id, Block block, boolean shouldRegisterItem) {
        Identifier blockID = Identifier.of(StarSkyExplority.MOD_ID, id);

        if(shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Settings());
            Registry.register(Registries.ITEM, blockID, blockItem);
        }

        return Registry.register(Registries.BLOCK, blockID, block);
    }

    public static void initialize() {}
}
