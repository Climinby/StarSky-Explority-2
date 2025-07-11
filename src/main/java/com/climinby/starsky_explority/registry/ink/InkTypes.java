package com.climinby.starsky_explority.registry.ink;

import com.climinby.starsky_explority.StarSkyExplority;
import com.climinby.starsky_explority.registry.SSERegistries;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class InkTypes {
    public static final InkType SQUID_INK = register(
            "squid_ink",
            new InkType(Items.INK_SAC, new Identifier(StarSkyExplority.MOD_ID, "textures/gui/container/ink.png"), 20, 100)
    );
    public static final InkType GLOW_INK = register(
            "glow_squid_ink",
            new InkType(Items.GLOW_INK_SAC, new Identifier(StarSkyExplority.MOD_ID, "textures/gui/container/glow_ink.png"), 10, 60)
    );

    private static InkType register(String id, InkType inkType) {
        InkType registeredInkType = Registry.register(SSERegistries.INK_TYPE, new Identifier(StarSkyExplority.MOD_ID, id), inkType);
        return registeredInkType;
    }

    public static void initialize() {
//        AnalyzerBlockEntity.registerInkType(Items.INK_SAC, 20, new Identifier(StarSkyExplority.MOD_ID, "textures/gui/container/ink.png"), 100);
//        AnalyzerBlockEntity.registerInkType(Items.GLOW_INK_SAC, 10, new Identifier(StarSkyExplority.MOD_ID, "textures/gui/container/glow_ink.png"), 60);
    }
}
