package com.climinby.starsky_explority.world;

import com.climinby.starsky_explority.StarSkyExplority;
import com.climinby.starsky_explority.block.SSEBlocks;
import com.climinby.starsky_explority.world.feature.MeteorCraterConfig;
import com.climinby.starsky_explority.world.feature.MeteorCraterFeature;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.*;

import java.util.List;

public class SSEConfiguredFeatures {
    public static final RegistryKey<ConfiguredFeature<?, ?>> METEOR_CRATER_MOON_SMALL_KEY = registerKey("meteor_crater_moon_small");
    public static final RegistryKey<ConfiguredFeature<?, ?>> LUNAR_SILVER_ORE_KEY = registerKey("lunar_silver_ore");

    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
//        MeteorCraterFeature feature = new MeteorCraterFeature(MeteorCraterConfig.CODEC);
//        register(context, METEOR_CRATER_MOON_SMALL_KEY, feature,
//                new MeteorCraterConfig("moon", MeteorCraterConfig.Size.SMALL, 3));

        RuleTest moonRockReplaceable = new BlockMatchRuleTest(SSEBlocks.MOON_ROCK);

        List<OreFeatureConfig.Target> lunarSilverOres = List.of(
                OreFeatureConfig.createTarget(moonRockReplaceable, SSEBlocks.LUNAR_SILVER_ORE.getDefaultState())
        );

        register(
                context,
                LUNAR_SILVER_ORE_KEY,
                Feature.ORE,
                new OreFeatureConfig(lunarSilverOres, 10) // 9 is the vein size
        );
    }

    private static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String id) {
        return RegistryKey.of(
                RegistryKeys.CONFIGURED_FEATURE,
                new Identifier(StarSkyExplority.MOD_ID, id)
        );
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(
            Registerable<ConfiguredFeature<?, ?>> context,
            RegistryKey<ConfiguredFeature<?, ?>> key,
            F feature,
            FC configuration
    ) {
        // This method would typically register the feature with the registry.
        // Implementation is omitted for brevity.
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
