package com.climinby.starsky_explority.world.biome;

import com.climinby.starsky_explority.StarSkyExplority;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;

public class SSEBiomeKeys {
    public static final RegistryKey<Biome> LUNAR_PLAINS = register("lunar_plains");
    public static final RegistryKey<Biome> LUNAR_WASTELAND = register("lunar_wasteland");
    public static final RegistryKey<Biome> THORNWILDS = register("thornwilds");
    public static final RegistryKey<Biome> LUNASEA = register("lunasea");

    private static RegistryKey<Biome> register(String name) {
        Identifier id = new Identifier(StarSkyExplority.MOD_ID, name);
        return RegistryKey.of(RegistryKeys.BIOME, id);
    }

    public static void init() {
        BiomeModifications.create(new Identifier(StarSkyExplority.MOD_ID, "biome_modifications"))
                .add(
                        ModificationPhase.ADDITIONS,
                        context -> context.getBiomeKey().equals(SSEBiomeKeys.LUNAR_PLAINS),
                        context -> {
                            context.getSpawnSettings()
                                    .addSpawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 12, 4, 4));
                        }
                );
    }
}
