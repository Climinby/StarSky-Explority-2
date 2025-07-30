package com.climinby.starsky_explority.world.gen;

import com.climinby.starsky_explority.world.SSEPlacedFeatures;
import com.climinby.starsky_explority.world.biome.SSEBiomeKeys;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.gen.GenerationStep;

public class VegetationGeneration {
    public static void generateVegetation() {
        generateMoonVegetation();
    }

    private static void generateMoonVegetation() {
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(
                        SSEBiomeKeys.THORNWILDS,
                        SSEBiomeKeys.LUNASEA
                ),
                GenerationStep.Feature.VEGETAL_DECORATION,
                SSEPlacedFeatures.MOON_VEGETATION_PLACED_KEY
        );
    }
}
