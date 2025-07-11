package com.climinby.starsky_explority.entity;

import com.climinby.starsky_explority.StarSkyExplority;
import com.climinby.starsky_explority.block.SSEBlocks;
import com.climinby.starsky_explority.block.entity.AnalyzerBlockEntity;
import com.climinby.starsky_explority.block.entity.ExtractorBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SSEEntities {
    public static final BlockEntityType<AnalyzerBlockEntity> ANALYZER_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(StarSkyExplority.MOD_ID, "analyzer"),
            BlockEntityType.Builder.create(AnalyzerBlockEntity::new, SSEBlocks.ANALYZER).build()
    );
    public static final BlockEntityType<ExtractorBlockEntity> EXTRACTOR_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(StarSkyExplority.MOD_ID, "extractor"),
            BlockEntityType.Builder.create(ExtractorBlockEntity::new, SSEBlocks.EXTRACTOR).build()
    );
    public static final EntityType<LunarianEntity> LUNARIAN_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(StarSkyExplority.MOD_ID, "lunarian"),
            EntityType.Builder.create(LunarianEntity::new, SpawnGroup.CREATURE)
                    .setDimensions(0.6F, 1.875F)
                    .build()
    );

    public static void initialize() {}
}
