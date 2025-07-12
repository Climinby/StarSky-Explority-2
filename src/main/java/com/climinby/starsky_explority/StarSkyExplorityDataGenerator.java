package com.climinby.starsky_explority;

import com.climinby.starsky_explority.datagen.StarSkyExplorityWorldGen;
import com.climinby.starsky_explority.world.biome.SSEBiomes;
import com.climinby.starsky_explority.world.dimension.SSEDimensions;
import com.climinby.starsky_explority.world.SSEConfiguredFeatures;
import com.climinby.starsky_explority.world.SSEPlacedFeatures;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

public class StarSkyExplorityDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(StarSkyExplorityWorldGen::new);
	}

	@Override
	public void buildRegistry(RegistryBuilder registryBuilder) {
		DataGeneratorEntrypoint.super.buildRegistry(registryBuilder);
		registryBuilder.addRegistry(RegistryKeys.BIOME, SSEBiomes::bootstrap);
		registryBuilder.addRegistry(RegistryKeys.DIMENSION_TYPE, SSEDimensions::bootstrapType);
		registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, SSEConfiguredFeatures::bootstrap);
		registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, SSEPlacedFeatures::bootstrap);
	}
}
