package com.climinby.starsky_explority.world;

import com.climinby.starsky_explority.StarSkyExplority;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;

import java.util.List;

public class SSEPlacedFeatures {
    public static final RegistryKey<PlacedFeature> METEOR_CRATER_MOON_PLACED_KEY = registerKey("meteor_crater_moon_placed");

    public static final RegistryKey<PlacedFeature> LUNAR_SILVER_ORE_PLACED_KEY = registerKey("lunar_silver_ore_placed");

    public static final RegistryKey<PlacedFeature> MOON_VEGETATION_PLACED_KEY = registerKey("moon_vegetation_placed");

    public static void bootstrap(Registerable<PlacedFeature> context) {
        var configuredFeatureRegistryLookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        register(
                context,
                LUNAR_SILVER_ORE_PLACED_KEY,
                configuredFeatureRegistryLookup.getOrThrow(SSEConfiguredFeatures.LUNAR_SILVER_ORE_KEY),
                SSEOrePlacement.modifiersWithCount(
                        10,
                        HeightRangePlacementModifier.uniform(
                                YOffset.fixed(-20),
                                YOffset.fixed(100)
                        )
                )
        );

        register(
                context,
                METEOR_CRATER_MOON_PLACED_KEY,
                configuredFeatureRegistryLookup.getOrThrow(SSEConfiguredFeatures.METEOR_CRATER_MOON_KEY),
                List.of(
                        HeightRangePlacementModifier.uniform(
                                YOffset.fixed(50),
                                YOffset.fixed(160)
                        )
                )
        );

        register(
                context,
                MOON_VEGETATION_PLACED_KEY,
                configuredFeatureRegistryLookup.getOrThrow(SSEConfiguredFeatures.MOON_VEGETATION_KEY),
                List.of(
                        HeightRangePlacementModifier.uniform(
                                YOffset.fixed(50),
                                YOffset.fixed(160)
                        )
                )
        );
    }

    private static RegistryKey<PlacedFeature> registerKey(String id) {
        return RegistryKey.of(
                RegistryKeys.PLACED_FEATURE,
                new Identifier(StarSkyExplority.MOD_ID, id)
        );
    }

    private static void register(
            Registerable<PlacedFeature> context,
            RegistryKey<PlacedFeature> key,
            RegistryEntry<ConfiguredFeature<?, ?>> configuration,
            List<PlacementModifier> modifiers
    ) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}
