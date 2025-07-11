package com.climinby.starsky_explority.world.biome;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.PlacedFeature;

public class TheMoonBiomeCreator {
    public static Biome createLunarPlain(
            RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup
    ) {
        SpawnSettings spawnSettings = new SpawnSettings.Builder()
                .spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 12, 4, 5))
                .build();
        GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
        return new Biome.Builder()
                .precipitation(false)
                .temperature(0.0F)
                .downfall(0.0F)
                .effects(
                        new BiomeEffects.Builder()
                                .waterColor(4159204)
                                .waterFogColor(0x111B2D)
                                .fogColor(0x0F1736)
                                .skyColor(OverworldBiomeCreator.getSkyColor(0.0F))
                                .music(null)
                                .build()
                )
                .spawnSettings(spawnSettings)
                .generationSettings(lookupBackedBuilder.build())
                .build();
    }

    public static Biome createLunarWasteLand(
            RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup
    ) {
        SpawnSettings spawnSettings = new SpawnSettings.Builder()
                .spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 12, 4, 5))
                .build();
        GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
        return new Biome.Builder()
                .precipitation(false)
                .temperature(0.0F)
                .downfall(0.0F)
                .effects(
                        new BiomeEffects.Builder()
                                .waterColor(4159204)
                                .waterFogColor(0x111B2D)
                                .fogColor(0x0F1736)
                                .skyColor(OverworldBiomeCreator.getSkyColor(0.0F))
                                .music(null)
                                .build()
                )
                .spawnSettings(spawnSettings)
                .generationSettings(lookupBackedBuilder.build())
                .build();
    }
}
