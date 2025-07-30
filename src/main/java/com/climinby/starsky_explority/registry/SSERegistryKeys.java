package com.climinby.starsky_explority.registry;

import com.climinby.starsky_explority.StarSkyExplority;
import com.climinby.starsky_explority.registry.gravity_changed_dimension.GravityChangedDimension;
import com.climinby.starsky_explority.registry.ink.InkType;
import com.climinby.starsky_explority.registry.material.MaterialType;
import com.climinby.starsky_explority.registry.planet.Galaxy;
import com.climinby.starsky_explority.registry.planet.Planet;
import com.climinby.starsky_explority.registry.sample.Sample;
import com.climinby.starsky_explority.registry.stargate.Stargate;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SSERegistryKeys {
    public static final RegistryKey<Registry<Planet>> PLANET = of("planet");
    public static final RegistryKey<Registry<Galaxy>> GALAXY = of("galaxy");
    public static final RegistryKey<Registry<Sample>> SAMPLE_TYPE = of("sample_type");
    public static final RegistryKey<Registry<MaterialType>> MATERIAL_TYPE = of("material_type");
    public static final RegistryKey<Registry<InkType>> INK_TYPE = of("ink_type");
    public static final RegistryKey<Registry<Stargate>> STARGATE = of("stargate");
    public static final RegistryKey<Registry<GravityChangedDimension>> GRAVITY_CHANGED_DIMENSION = of("gravity_changed_dimension");

    private static <T> RegistryKey<Registry<T>> of(String id) {
        return RegistryKey.ofRegistry(new Identifier(StarSkyExplority.MOD_ID, id));
    }

    public static void initialize() {}
}
