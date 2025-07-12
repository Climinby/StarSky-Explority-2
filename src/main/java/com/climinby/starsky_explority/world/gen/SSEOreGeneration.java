package com.climinby.starsky_explority.world.gen;

import com.climinby.starsky_explority.world.SSEPlacedFeatures;
import com.climinby.starsky_explority.world.biome.SSEBiomeKeys;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.gen.GenerationStep;

public class SSEOreGeneration {
    public static void generateOres() {
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(
                        SSEBiomeKeys.LUNAR_WASTELAND,
                        SSEBiomeKeys.LUNAR_PLAINS
                ),
                GenerationStep.Feature.UNDERGROUND_ORES,
                SSEPlacedFeatures.LUNAR_SILVER_ORE_PLACED_KEY
        );
    }
}
