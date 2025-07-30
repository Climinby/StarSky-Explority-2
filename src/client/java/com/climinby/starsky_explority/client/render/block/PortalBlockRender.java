package com.climinby.starsky_explority.client.render.block;

import com.climinby.starsky_explority.block.SSEBlocks;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class PortalBlockRender {
    public static void init() {
        BlockRenderLayerMap.INSTANCE.putBlock(SSEBlocks.MOON_PORTAL, RenderLayer.getTranslucent());
    }
}
