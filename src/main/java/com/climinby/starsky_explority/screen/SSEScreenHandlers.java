package com.climinby.starsky_explority.screen;

import com.climinby.starsky_explority.StarSkyExplority;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class SSEScreenHandlers {
    public static final ScreenHandlerType<AnalyzerScreenHandler> ANALYZER_SCREEN_HANDLER;
    public static final ScreenHandlerType<ProfileScreenHandler> PROFILE_SCREEN_HANDLER;
    public static final ScreenHandlerType<ExtractorScreenHandler> EXTRACTOR_SCREEN_HANDLER;

    static {
        ANALYZER_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(
                new Identifier(StarSkyExplority.MOD_ID, "analyzer"),
                AnalyzerScreenHandler::new
        );
        PROFILE_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(
                new Identifier(StarSkyExplority.MOD_ID, "analyzer_profile"),
                ProfileScreenHandler::new
        );
        EXTRACTOR_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(
                new Identifier(StarSkyExplority.MOD_ID, "extractor"),
                ExtractorScreenHandler::new
        );
    }

    public static void initialize() {}
}
