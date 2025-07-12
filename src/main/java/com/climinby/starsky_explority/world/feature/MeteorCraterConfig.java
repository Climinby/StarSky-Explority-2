package com.climinby.starsky_explority.world.feature;

import com.climinby.starsky_explority.registry.planet.Planet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;

public record MeteorCraterConfig(String planet, Size size, int varianceCount) implements FeatureConfig {

    public static final Codec<MeteorCraterConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("planet").forGetter(MeteorCraterConfig::planet),
            Size.CODEC.fieldOf("size").forGetter(MeteorCraterConfig::size),
            Codec.INT.fieldOf("varianceCount").forGetter(MeteorCraterConfig::varianceCount)
    ).apply(instance, MeteorCraterConfig::new));

    public enum Size {
        SMALL(0, "small"),
        MEDIUM(1, "medium"),
        LARGE(2, "large");

        public final int id;
        public final String name;

        Size(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public static final Codec<Size> CODEC = Codec.INT.xmap(
            id -> Size.values()[id],
            size -> size.id
        );
    }
}
