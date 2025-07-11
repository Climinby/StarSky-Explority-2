package com.climinby.starsky_explority.client.rei.analyzing;

import com.climinby.starsky_explority.StarSkyExplority;
import com.climinby.starsky_explority.block.SSEBlocks;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;

public class AnalyzingCategory implements DisplayCategory<BasicDisplay> {
    private static final Identifier TEXTURE = new Identifier(
            StarSkyExplority.MOD_ID, "textures/gui/container/analyzer_rei.png"
    );
    public static final CategoryIdentifier<AnalyzingDisplay> ANALYZING = CategoryIdentifier.of(
            StarSkyExplority.MOD_ID, "analyzing"
    );

    @Override
    public CategoryIdentifier<? extends BasicDisplay> getCategoryIdentifier() {
        return ANALYZING;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("category.rei.starsky_explority.analyzing");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(SSEBlocks.ANALYZER);
    }

    @Override
    public List<Widget> setupDisplay(BasicDisplay display, Rectangle bounds) {
        final Point startPoint = new Point(bounds.getCenterX() - 56, bounds.getCenterY() - 32);
        List<Widget> widgets = new LinkedList<>();
        widgets.add(Widgets.createTexturedWidget(TEXTURE, new Rectangle(startPoint.x, startPoint.y, 112, 65)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 48, startPoint.y + 10))
                .disableBackground().markInput().entries(display.getInputEntries().get(0)));
        for(int i = 0; i < 5; i++) {
            widgets.add(Widgets.createSlot(new Point(startPoint.x + 12 + i * 18, startPoint.y + 37))
                    .disableBackground().markOutput().entries(display.getOutputEntries().get(i)));
        }
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 65;
    }

    @Override
    public int getDisplayWidth(BasicDisplay display) {
        return 112;
    }

    @Override
    public int getMaximumDisplaysPerPage() {
        return DisplayCategory.super.getMaximumDisplaysPerPage();
    }
}
