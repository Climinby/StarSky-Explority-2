package com.climinby.starsky_explority.world.biome;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.PlacedFeature;

public class SSEBiomes {
    public static void bootstrap(Registerable<Biome> biomeRegisterable) {
        RegistryEntryLookup<PlacedFeature> registryEntryLookup = biomeRegisterable.getRegistryLookup(RegistryKeys.PLACED_FEATURE);
        RegistryEntryLookup<ConfiguredCarver<?>> registryEntryLookup2 = biomeRegisterable.getRegistryLookup(RegistryKeys.CONFIGURED_CARVER);

        biomeRegisterable.register(SSEBiomeKeys.LUNAR_PLAINS, TheMoonBiomeCreator.createLunarPlain(registryEntryLookup, registryEntryLookup2));
        biomeRegisterable.register(SSEBiomeKeys.LUNAR_WASTELAND, TheMoonBiomeCreator.createLunarWasteLand(registryEntryLookup, registryEntryLookup2));
        biomeRegisterable.register(SSEBiomeKeys.THORNWILDS, TheMoonBiomeCreator.createThornwilds(registryEntryLookup, registryEntryLookup2));
        biomeRegisterable.register(SSEBiomeKeys.LUNASEA, TheMoonBiomeCreator.createLunasea(registryEntryLookup, registryEntryLookup2));
    }
}
