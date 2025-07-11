package com.climinby.starsky_explority.client.gui.button.analyzer;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PageButton extends SSEButton {
    private PageDestination pageDestination;

    public PageButton(int x, int y, int width, int height, int textureX, int textureY, Identifier texture, Text message, PageDestination pageDestination, PressAction onPress) {
        super(x, y, width, height, textureX, textureY, texture, message, onPress);
        this.pageDestination = pageDestination;
    }

    public PageDestination getPageDestination() {
        return pageDestination;
    }

    public boolean getPageDesBool() {
        return pageDestination == PageDestination.RIGHT;
    }

    public static enum PageDestination {
        RIGHT,
        LEFT
    }
}
