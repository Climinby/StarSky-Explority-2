package com.climinby.starsky_explority.world.feature;

import com.climinby.starsky_explority.StarSkyExplority;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SSEFeatures {
    public static final Feature<MoonMeteorCraterConfig> METEOR_CRATER_MOON = register(
            "meteor_crater_moon",
            new MoonMeteorCraterFeature(MoonMeteorCraterConfig.CODEC)
    );

    public static final Feature<MoonVegetationConfig> MOON_VEGETATION = register(
            "moon_vegetation",
            new MoonVegetationFeature(MoonVegetationConfig.CODEC)
    );

    private static <C extends FeatureConfig, F extends Feature<C>> F register(String name, F feature) {
        return (F) Registry.register(Registries.FEATURE, new Identifier(StarSkyExplority.MOD_ID, name), feature);
    }

    public static void initialize() {}
//    public static final Identifier METEOR_CRATER_MOON_SMALL_ID =
//            new Identifier(StarSkyExplority.MOD_ID, "meteor_crater_moon_small");
//
//    public static final Feature<MeteorCraterConfig> METEOR_CRATER_MOON_SMALL =
//            new MeteorCraterFeature(MeteorCraterConfig.CODEC);
//
//    public static final ConfiguredFeature<MeteorCraterConfig, MeteorCraterFeature> METEOR_CRATER_MOON_SMALL_CONFIGURED =
//            new ConfiguredFeature<>(
//                    (MeteorCraterFeature) METEOR_CRATER_MOON_SMALL,
//                    new MeteorCraterConfig("moon", MeteorCraterConfig.Size.SMALL, 3)
//            );
//
//    public static final PlacedFeature METEOR_CRATER_MOON_PLACED_KEY = new PlacedFeature(
//            RegistryEntry.of(
//                    METEOR_CRATER_MOON_SMALL_CONFIGURED
//            ),
//            List.of(
//                    RarityFilterPlacementModifier.of(10),
//                    HeightmapPlacementModifier.of(
//                            Heightmap.Type.WORLD_SURFACE
//                    )
//            )
//    );
//
//
//    public static void initialize() {
//        // This method can be used to initialize or register features if needed.
//        // Currently, it does nothing but can be expanded in the future.
//        Registry.register(Registries.FEATURE, METEOR_CRATER_MOON_SMALL_ID, METEOR_CRATER_MOON_SMALL);
//
//        BiomeModifications.addFeature(
//                BiomeSelectors.includeByKey(SSEBiomeKeys.LUNAR_WASTELAND),
//                GenerationStep.Feature.SURFACE_STRUCTURES,
//                RegistryKey.of(RegistryKeys.PLACED_FEATURE, METEOR_CRATER_MOON_SMALL_ID)
//        );
//    }
}
