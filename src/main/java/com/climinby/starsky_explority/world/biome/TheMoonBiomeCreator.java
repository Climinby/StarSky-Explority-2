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
                .spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 50, 4, 5))
                .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SKELETON, 100, 4, 4))
                .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.CREEPER, 100, 4, 4))
                .build();
        GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
        return new Biome.Builder()
                .precipitation(false)
                .temperature(2.0F)
                .downfall(0.0F)
                .effects(
                        new BiomeEffects.Builder()
                                .grassColor(0x7cb6d3)
                                .waterColor(4159204)
                                .waterFogColor(1121069)
                                .fogColor(5)
                                .skyColor(5)
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
                .spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 50, 4, 5))
                .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SKELETON, 100, 4, 4))
                .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.CREEPER, 100, 4, 4))
                .build();
        GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
        return new Biome.Builder()
                .precipitation(false)
                .temperature(2.0F)
                .downfall(0.0F)
                .effects(
                        new BiomeEffects.Builder()
                                .grassColor(0x7cb6d3)
                                .waterColor(4159204)
                                .waterFogColor(0x111B2D)
                                .fogColor(5)
                                .skyColor(5)
                                .music(null)
                                .build()
                )
                .spawnSettings(spawnSettings)
                .generationSettings(lookupBackedBuilder.build())
                .build();
    }

    public static Biome createThornwilds(
            RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup
    ) {
        SpawnSettings spawnSettings = new SpawnSettings.Builder()
                .spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 50, 4, 5))
                .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SKELETON, 100, 4, 4))
                .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.CREEPER, 100, 4, 4))
                .build();
        GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
        return new Biome.Builder()
                .precipitation(false)
                .temperature(2.0F)
                .downfall(0.0F)
                .effects(
                        new BiomeEffects.Builder()
                                .grassColor(0xA088CA)
                                .waterColor(0x8B76E5)
                                .waterFogColor(0x5957B9)
                                .fogColor(0xD37FDC)
                                .skyColor(0xC661D0)
                                .music(null)
                                .build()
                )
                .spawnSettings(spawnSettings)
                .generationSettings(lookupBackedBuilder.build())
                .build();
    }

    public static Biome createLunasea(
            RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup
    ) {
        SpawnSettings spawnSettings = new SpawnSettings.Builder()
                .build();
        GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
        return new Biome.Builder()
                .precipitation(false)
                .temperature(2.0F)
                .downfall(0.0F)
                .effects(
                        new BiomeEffects.Builder()
                                .grassColor(0x7cb6d3)
                                .waterColor(4159204)
                                .waterFogColor(0x111B2D)
                                .fogColor(5)
                                .skyColor(5)
                                .music(null)
                                .build()
                )
                .spawnSettings(spawnSettings)
                .generationSettings(lookupBackedBuilder.build())
                .build();
    }
}
