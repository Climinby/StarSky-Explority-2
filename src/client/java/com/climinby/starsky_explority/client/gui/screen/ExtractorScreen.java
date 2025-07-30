package com.climinby.starsky_explority.client.gui.screen;

import com.climinby.starsky_explority.StarSkyExplority;
import com.climinby.starsky_explority.screen.ExtractorScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ExtractorScreen extends HandledScreen<ExtractorScreenHandler> {
    private BlockPos pos;
    private boolean isWaterCharged = false;
    private boolean isLavaCharged = false;
    private float process = 0;

    private static final Identifier TEXTURE = new Identifier(
            StarSkyExplority.MOD_ID,
            "textures/gui/container/extractor.png"
    );

    public ExtractorScreen(ExtractorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        pos = this.handler.getPos();
        isWaterCharged = this.handler.isWaterCharged();
        isLavaCharged = this.handler.isLavaCharged();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        //renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        titleX = ((backgroundWidth - textRenderer.getWidth(title)) / 2);
        titleY = backgroundHeight - 160;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
        if(isWaterCharged) {
            context.drawItem(new ItemStack(Items.WATER_BUCKET), titleX - 16, titleY + 51);
        }
        if(isLavaCharged) {
            context.drawItem(new ItemStack(Items.LAVA_BUCKET), titleX + 24, titleY + 51);
        }
        context.drawTexture(TEXTURE, titleX + 4, titleY + 27, 0, 166, (int)(36.0F * process), 18);
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setWaterCharged(boolean isWaterCharged) {
        this.isWaterCharged = isWaterCharged;
    }

    public void setLavaCharged(boolean isLavaCharged) {
        this.isLavaCharged = isLavaCharged;
    }

    public void setProcess(float process) {
        this.process = process;
    }
}
