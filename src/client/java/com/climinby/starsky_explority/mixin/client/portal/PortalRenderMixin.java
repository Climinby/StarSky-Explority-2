package com.climinby.starsky_explority.mixin.client.portal;

import com.climinby.starsky_explority.client.mixin_interface.portal.PlayerEntityInStargate;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(InGameHud.class)
public class PortalRenderMixin {
    @Shadow @Final private MinecraftClient client;

    @Shadow private int scaledWidth;

    @Shadow private int scaledHeight;

    @Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderPortalOverlay(DrawContext context, float nauseaStrength, CallbackInfo ci) {
        BlockState texture;
        if (((PlayerEntityInStargate) this.client.player).getLastStargate() != null) {
            texture = ((PlayerEntityInStargate) this.client.player).getLastStargate().getDefaultState();
            if (nauseaStrength < 1.0F) {
                nauseaStrength *= nauseaStrength;
                nauseaStrength *= nauseaStrength;
                nauseaStrength = nauseaStrength * 0.8F + 0.2F;
            }

            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            context.setShaderColor(1.0F, 1.0F, 1.0F, nauseaStrength);
            Sprite sprite = this.client.getBlockRenderManager().getModels().getModelParticleSprite(texture);
            context.drawSprite(0, 0, -90, this.scaledWidth, this.scaledHeight, sprite);
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            ci.cancel();
        }
    }
}
