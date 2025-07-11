package com.climinby.starsky_explority.client.rei;

import com.climinby.starsky_explority.block.SSEBlocks;
import com.climinby.starsky_explority.client.gui.screen.ExtractorScreen;
import com.climinby.starsky_explority.client.rei.analyzing.AnalyzingCategory;
import com.climinby.starsky_explority.client.rei.analyzing.AnalyzingDisplay;
import com.climinby.starsky_explority.client.rei.extracting.ExtractingCategory;
import com.climinby.starsky_explority.client.rei.extracting.ExtractingDisplay;
import com.climinby.starsky_explority.recipe.AnalysisRecipe;
import com.climinby.starsky_explority.recipe.ExtractRecipe;
import com.climinby.starsky_explority.recipe.SSERecipeType;
import com.climinby.starsky_explority.screen.AnalyzerScreenHandler;
import com.climinby.starsky_explority.screen.ExtractorScreenHandler;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.client.registry.transfer.simple.SimpleTransferHandler;
import me.shedaniel.rei.api.common.util.EntryStacks;

public class SSEREIClientPlugin implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
//        REIClientPlugin.super.registerCategories(registry);

//        registry.add(new ExtractingCategory(), config -> config.setQuickCraftingEnabledByDefault(true));
        registry.add(new ExtractingCategory());
        registry.add(new AnalyzingCategory());

        registry.addWorkstations(ExtractingCategory.EXTRACTING, EntryStacks.of(SSEBlocks.EXTRACTOR));
        registry.addWorkstations(AnalyzingCategory.ANALYZING, EntryStacks.of(SSEBlocks.ANALYZER));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(ExtractRecipe.class, SSERecipeType.EXTRACTING, ExtractingDisplay::new);
        registry.registerRecipeFiller(AnalysisRecipe.class, SSERecipeType.ANALYZING, AnalyzingDisplay::new);
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerClickArea(screen ->
                {
                    int centerX = screen.width / 2;
                    int centerY = screen.height / 2;
                    return new Rectangle(centerX - 21, centerY - 50, 36, 18);
                },
                ExtractorScreen.class, ExtractingCategory.EXTRACTING);
    }

    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
        registry.register(SimpleTransferHandler.create(
                ExtractorScreenHandler.class,
                ExtractingCategory.EXTRACTING,
                new SimpleTransferHandler.IntRange(0, 1)
        ));
        registry.register(SimpleTransferHandler.create(
                AnalyzerScreenHandler.class,
                AnalyzingCategory.ANALYZING,
                new SimpleTransferHandler.IntRange(0, 1)
        ));
    }
}
