package com.climinby.starsky_explority.world.gen;

import com.climinby.starsky_explority.world.SSEPlacedFeatures;
import com.climinby.starsky_explority.world.biome.SSEBiomeKeys;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.gen.GenerationStep;

public class MeteorCraterGeneration {
    public static void generateMeteorCrater() {
        generateMoon();
    }

    private static void generateMoon() {
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(
                        SSEBiomeKeys.LUNAR_WASTELAND,
                        SSEBiomeKeys.LUNAR_PLAINS
                ),
                GenerationStep.Feature.SURFACE_STRUCTURES,
                SSEPlacedFeatures.METEOR_CRATER_MOON_PLACED_KEY
        );
    }
}
