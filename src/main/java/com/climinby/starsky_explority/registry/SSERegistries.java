package com.climinby.starsky_explority.registry;

import com.climinby.starsky_explority.StarSkyExplority;
import com.climinby.starsky_explority.registry.ink.InkType;
import com.climinby.starsky_explority.registry.material.MaterialType;
import com.climinby.starsky_explority.registry.planet.Galaxy;
import com.climinby.starsky_explority.registry.planet.Planet;
import com.climinby.starsky_explority.registry.sample.Sample;
import com.mojang.serialization.Lifecycle;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public class SSERegistries {
    public static final Registry<Registry<?>> REGISTRIES = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(StarSkyExplority.MOD_ID, "registries")),
            Lifecycle.experimental(),
            false);
    public static final Registry<Planet> PLANET = register(SSERegistryKeys.PLANET, Lifecycle.experimental(), false);
    public static final Registry<Galaxy> GALAXY = register(SSERegistryKeys.GALAXY, Lifecycle.experimental(), false);
    public static final Registry<Sample> SAMPLE_TYPE = register(SSERegistryKeys.SAMPLE_TYPE, Lifecycle.experimental(), false);
    public static final Registry<MaterialType> MATERIAL_TYPE = register(SSERegistryKeys.MATERIAL_TYPE, Lifecycle.experimental(), false);
    public static final Registry<InkType> INK_TYPE = register(SSERegistryKeys.INK_TYPE, Lifecycle.experimental(), false);

    private static <T> Registry<T> register(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle, boolean intrusive) {
        Identifier registryID = key.getValue();
        SimpleRegistry<T> simpleRegistry = new SimpleRegistry<>(key, lifecycle, intrusive);
        return Registry.register(REGISTRIES, registryID, simpleRegistry);
    }

    public static void initialize() {}
}
