package com.climinby.starsky_explority.registry.sample;

import com.climinby.starsky_explority.StarSkyExplority;
import com.climinby.starsky_explority.block.SSEBlocks;
import com.climinby.starsky_explority.item.SSEItems;
import com.climinby.starsky_explority.registry.SSERegistries;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SampleTypes {
    public static final Sample SAMPLE_EARTH = register("sample_earth", new Sample(
            new Sample.Settings()
                    .setSampleItem(SSEItems.SAMPLE_EARTH)
                    .setOdds(0.1F)
                    .addBlocks(Blocks.DIRT)
                    .addBlocks(Blocks.SAND)
                    .addBlocks(Blocks.RED_SAND)
                    .addBlocks(Blocks.GRAVEL)
                    .addBlocks(Blocks.GRASS_BLOCK)
    ));
    public static final Sample SAMPLE_MOON = register("sample_moon", new Sample(
            new Sample.Settings()
                    .setSampleItem(SSEItems.SAMPLE_MOON)
                    .setOdds(0.1F)
                    .addBlocks(SSEBlocks.MOON_SOIL)
    ));

    private static Sample register(String id, Sample sample) {
        Sample registeredSample = Registry.register(SSERegistries.SAMPLE_TYPE, new Identifier(StarSkyExplority.MOD_ID, id), sample);
        return registeredSample;
    }

    public static void initialize() {}
}
