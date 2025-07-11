package com.climinby.starsky_explority.client.gui.button.analyzer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SSEButton extends ButtonWidget {
    private final Identifier TEXTURE;
    private final int textureX;
    private final int textureY;

    public SSEButton(int x, int y, int width, int height, int textureX, int textureY, Identifier texture, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.textureX = textureX;
        this.textureY = textureY;
        TEXTURE = texture;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);

        boolean isHovered = this.isMouseOver(mouseX, mouseY);
        this.hovered = isHovered;
        boolean isFocused = this.isFocused();
        this.setFocused(isFocused);
        int textureX = this.textureX;
        int textureY = this.textureY;
        int textColor;
        if(!this.active) {
            textureY = textureY + 2 * this.height;
            textColor = 0x555555;
        } else if(this.isSelected()) {
            textureY = textureY + this.height;
            textColor = 0xFFFFFF;
        } else {
            textColor = 0xFFFFFF;
        }

        context.drawTexture(TEXTURE, this.getX(), this.getY(), textureX, textureY, this.width, this.height);
        this.drawMessage(context, MinecraftClient.getInstance().textRenderer, textColor);
    }
}
