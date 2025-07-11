package com.climinby.starsky_explority;

import com.climinby.starsky_explority.block.SSEBlocks;
import com.climinby.starsky_explority.client.entity.SSEEntityRenderers;
import com.climinby.starsky_explority.client.gui.screen.AnalyzerScreen;
import com.climinby.starsky_explority.client.gui.screen.ExtractorScreen;
import com.climinby.starsky_explority.client.render.SSEDimensionRenderer;
import com.climinby.starsky_explority.screen.SSEScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.render.RenderLayer;

public class StarSkyExplorityClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		SSEClientDataReceiver.initialize();

		SSEDimensionRenderer.init();

		SSEEntityRenderers.init();
		ScreenRegistry.register(SSEScreenHandlers.ANALYZER_SCREEN_HANDLER, AnalyzerScreen::new);
		ScreenRegistry.register(SSEScreenHandlers.PROFILE_SCREEN_HANDLER, AnalyzerScreen.ProfileScreen::new);
		ScreenRegistry.register(SSEScreenHandlers.EXTRACTOR_SCREEN_HANDLER, ExtractorScreen::new);

		BlockRenderLayerMap.INSTANCE.putBlock(SSEBlocks.ANALYZER, RenderLayer.getSolid());
	}
}