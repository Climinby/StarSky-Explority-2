package com.climinby.starsky_explority.world.feature;

import com.climinby.starsky_explority.world.biome.SSEBiomeKeys;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.FeatureConfig;

public record MoonVegetationConfig(BiomeType biome, int dispersion) implements FeatureConfig {
    public static final Codec<MoonVegetationConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BiomeType.CODEC.fieldOf("biome").forGetter(MoonVegetationConfig::biome),
                    Codec.intRange(0, Integer.MAX_VALUE).fieldOf("dispersion").forGetter(MoonVegetationConfig::dispersion)
            ).apply(instance, MoonVegetationConfig::new)
    );

    public enum BiomeType {
        THORNWILDS(0, SSEBiomeKeys.THORNWILDS),
        LUNASEA(1, SSEBiomeKeys.LUNASEA);

        public final int id;
        public final RegistryKey<Biome> biomeKey;

        private static final Codec<BiomeType> CODEC = Codec.INT.xmap(
                id -> BiomeType.values()[id],
                biomeType -> biomeType.id
        );

        BiomeType(int id, RegistryKey<Biome> biomeKey) {
            this.id = id;
            this.biomeKey = biomeKey;
        }
    }
}
