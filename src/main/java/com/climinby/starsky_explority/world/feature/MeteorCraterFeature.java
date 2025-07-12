package com.climinby.starsky_explority.world.feature;

import com.climinby.starsky_explority.StarSkyExplority;
import com.mojang.serialization.Codec;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class MeteorCraterFeature extends Feature<MeteorCraterConfig> {
    public MeteorCraterFeature(Codec<MeteorCraterConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<MeteorCraterConfig> context) {
        StructureWorldAccess world = context.getWorld();

        Random random = context.getRandom();

        MeteorCraterConfig config = context.getConfig();

        int variance = random.nextInt(config.varianceCount()) + 1;

        String path = "meteor_crater_" + config.planet() + "_" + config.size().name + "_" + variance;

        BlockPos pos = context.getOrigin();

        StructureTemplateManager structureTemplateManager = world.getServer().getStructureTemplateManager();

        if (structureTemplateManager == null) return false;

        StructureTemplate template = structureTemplateManager
                .getTemplate(new Identifier(StarSkyExplority.MOD_ID, path))
                .orElse(null);
        if (template == null) {
            return false;
        }

        template.place(world, pos, pos, new StructurePlacementData(), world.getRandom(), 2);

        return true;
    }
}
