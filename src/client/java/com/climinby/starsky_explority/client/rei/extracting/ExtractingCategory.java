package com.climinby.starsky_explority.client.rei.extracting;

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

public class ExtractingCategory implements DisplayCategory<BasicDisplay> {
    private static final Identifier TEXTURE = new Identifier(
            StarSkyExplority.MOD_ID, "textures/gui/container/extractor_rei.png"
    );
    public static final CategoryIdentifier<ExtractingDisplay> EXTRACTING = CategoryIdentifier.of(
            StarSkyExplority.MOD_ID, "extracting"
    );

    @Override
    public CategoryIdentifier<? extends BasicDisplay> getCategoryIdentifier() {
        return EXTRACTING;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("category.rei.starsky_explority.extracting");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(SSEBlocks.EXTRACTOR);
    }

    @Override
    public List<Widget> setupDisplay(BasicDisplay display, Rectangle bounds) {
        final Point startPoint = new Point(bounds.getCenterX() - 72/*71*/, bounds.getCenterY() - 37/*35*/);
        List<Widget> widgets = new LinkedList<>();
        widgets.add(Widgets.createTexturedWidget(TEXTURE, new Rectangle(startPoint.x, startPoint.y, 144, 74)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 17, startPoint.y + 29))
                .markInput().entries(display.getInputEntries().get(0)));
        for(int i = 0; i < 3; i++) {
            widgets.add(Widgets.createSlot(new Point(startPoint.x + 107, startPoint.y + 47 - 18 * i))
                    .markOutput().entries(display.getOutputEntries().get(2 - i)));
        }
        widgets.add(Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) -> {
            graphics.drawTexture(
                    TEXTURE,
                    startPoint.x + 51,
                    startPoint.y + 26,
                    0,
                    74,
                    (int) (System.currentTimeMillis() % 5000L * 36L / 5000L),
                    18
            );
        }));

        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 74;
    }

    @Override
    public int getDisplayWidth(BasicDisplay display) {
        return 144;
    }

    @Override
    public int getMaximumDisplaysPerPage() {
        return 2;
    }
}
