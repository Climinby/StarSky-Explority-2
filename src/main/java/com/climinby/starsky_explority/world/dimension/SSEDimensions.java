package com.climinby.starsky_explority.world.dimension;

import com.climinby.starsky_explority.StarSkyExplority;
import com.climinby.starsky_explority.registry.SSERegistries;
import com.climinby.starsky_explority.registry.gravity_changed_dimension.GravityChangedDimension;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.OptionalLong;

public class SSEDimensions {
    public static final Identifier THE_MOON_ID = new Identifier(StarSkyExplority.MOD_ID, "the_moon");

    public static final RegistryKey<DimensionOptions> THE_MOON_KEY = RegistryKey.of(
            RegistryKeys.DIMENSION,
            THE_MOON_ID
    );

    public static final RegistryKey<World> THE_MOON_LEVEL_KEY = registerGravityChangedWorld(
            THE_MOON_ID,
            1.0 / 6.0 // 1/6 gravity
    );

//    public static final RegistryKey<World> THE_MOON_LEVEL_KEY = RegistryKey.of(
//            RegistryKeys.WORLD,
//            THE_MOON_ID
//    );

    public static final RegistryKey<DimensionType> THE_MOON_TYPE = RegistryKey.of(
            RegistryKeys.DIMENSION_TYPE,
            THE_MOON_ID
    );

    public static void bootstrapType(Registerable<DimensionType> context) {
        context.register(THE_MOON_TYPE, new DimensionType(
                OptionalLong.empty(),
                true, // hasSkyLight
                false, // hasCeiling
                false, // ultrawarm
                false, // natural
                1.0, // coordinateScale
                true, // bedWorks
                true, // respawnAnchorWorks
                -64, // minY
                384, // height
                384, // logicalHeight
                BlockTags.INFINIBURN_OVERWORLD,
                THE_MOON_ID,
                0.0F, // ambientLight
                new DimensionType.MonsterSettings(
                        true, // piglinCanSpawn
                        false, // hasRaids
                        ConstantIntProvider.create(7),
                        0 // monsterSpawnBlockLightLevel
                )
        ));
    }

    private static RegistryKey<World> registerGravityChangedWorld(
            Identifier id,
            double gravityMultiplier
    ) {
        RegistryKey<World> worldRegistryKey = RegistryKey.of(
                RegistryKeys.WORLD,
                id
        );

        Registry.register(
                SSERegistries.GRAVITY_CHANGED_DIMENSION,
                id,
                new GravityChangedDimension(
                        worldRegistryKey,
                        gravityMultiplier
                )
        );

        return worldRegistryKey;
    }
}
