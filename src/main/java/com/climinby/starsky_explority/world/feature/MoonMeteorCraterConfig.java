package com.climinby.starsky_explority.world.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Range;
import net.minecraft.world.gen.feature.FeatureConfig;

import java.util.List;

public record MoonMeteorCraterConfig(Range<Integer> radiusRange, int dispersion) implements FeatureConfig {
    public static final Codec<MoonMeteorCraterConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Range.CODEC.fieldOf("radius_range").forGetter(MoonMeteorCraterConfig::radiusRange),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("dispersion").forGetter(MoonMeteorCraterConfig::dispersion)
    ).apply(instance, MoonMeteorCraterConfig::new));

    public MoonMeteorCraterConfig {
        if (dispersion < 0) {
            throw new IllegalArgumentException("Dispersion must be non-negative");
        }
    }
}
