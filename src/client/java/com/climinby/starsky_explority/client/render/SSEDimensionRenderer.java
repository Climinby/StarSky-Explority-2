package com.climinby.starsky_explority.client.render;

import com.climinby.starsky_explority.StarSkyExplority;
import com.climinby.starsky_explority.world.dimension.SSEDimensions;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

public class SSEDimensionRenderer {
    private static final Identifier THE_EARTH_TEXTURE = new Identifier(StarSkyExplority.MOD_ID, "textures/environment/earth.png");
    private static final Identifier THE_SUN_TEXTURE = new Identifier(StarSkyExplority.MOD_ID, "textures/environment/sun.png");
    private static final List<Double> RANDOM_DOUBLE = new ArrayList<>();
    private static final List<Double> RANDOM_GAUSSIAN = new ArrayList<>();
    public static int pointerDouble = 0;
    public static int pointerGaussian = 0;

    public static int getStarColor(int rand) {
        int newRand = rand % 32;
        if(newRand == 0) {
            return 0xFFD86455;
        } else if(newRand <= 5) {
            return 0xFF95D1F4;
        } else if(newRand <= 17) {
            return 0xFFFEFFE9;
        } else {
            return 0xFFFBFBFB;
        }
    }

    public static double getRandomDouble() {
        if(pointerDouble >= RANDOM_DOUBLE.size() || pointerDouble < 0) pointerDouble = 0;
        double rand = RANDOM_DOUBLE.get(pointerDouble);
        pointerDouble++;
        if(pointerDouble == RANDOM_DOUBLE.size()) {
            pointerDouble = 0;
        }
        return rand;
    }

    public static double getRandomGaussian() {
        if(pointerGaussian >= RANDOM_GAUSSIAN.size() || pointerGaussian < 0) pointerGaussian = 0;
        double rand = RANDOM_GAUSSIAN.get(pointerGaussian);
        pointerGaussian++;
        if(pointerGaussian == RANDOM_GAUSSIAN.size()) {
            pointerGaussian = 0;
        }
        return rand;
    }

    public static void renderCelestialObject(Identifier texture, Matrix4f matrix, float size, float distance) {
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(515);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, texture);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix, -size,-size,-distance).texture(0.0F, 1.0F).next();
        bufferBuilder.vertex(matrix, size,-size,-distance).texture(1.0F, 1.0F).next();
        bufferBuilder.vertex(matrix, size,size,-distance).texture(1.0F, 0.0F).next();
        bufferBuilder.vertex(matrix, -size,size,-distance).texture(0.0F, 0.0F).next();
        tessellator.draw();

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public static void renderStar(Matrix4f matrix, float size, float distance) {
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(515);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        int argb = getStarColor((int) (getRandomDouble() * 16));
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, -size,-size,-distance).color(argb).next();
        bufferBuilder.vertex(matrix, size,-size,-distance).color(argb).next();
        bufferBuilder.vertex(matrix, size,size,-distance).color(argb).next();
        bufferBuilder.vertex(matrix, -size,size,-distance).color(argb).next();
        tessellator.draw();

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public static void renderStar(Matrix4f matrix, float size, float distance, float rotation) {
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(515);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        int argb = getStarColor((int) (getRandomDouble() * 16));
        float cos = (float) Math.cos(rotation);
        float sin = (float) Math.sin(rotation);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, (-size * cos) - (-size * sin),(-size * sin) + (-size * cos),-distance).color(argb).next();
        bufferBuilder.vertex(matrix, (size * cos) - (-size * sin),(size * sin) + (-size * cos),-distance).color(argb).next();
        bufferBuilder.vertex(matrix, (size * cos) - (size * sin),(size * sin) + (size * cos),-distance).color(argb).next();
        bufferBuilder.vertex(matrix, (-size * cos) - (size * sin),(-size * sin) + (size * cos),-distance).color(argb).next();
        tessellator.draw();

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public static void renderStar(MatrixStack matrix, float size, float distance, int density) {
        pointerDouble = 103;
        for(int i = 0; i < density; i++) {
            double z = getRandomDouble() * 360.0F;
            double y = getRandomDouble() * 360.0F;
            double x = getRandomDouble() * 360.0F;
            matrix.push();
            Quaternionf rotation = new Quaternionf()
                    .rotateZ((float) Math.toRadians(z))
                    .rotateY((float) Math.toRadians(y))
                    .rotateX((float) Math.toRadians(x));
            matrix.multiply(rotation);
            Matrix4f matrices = matrix.peek().getPositionMatrix();
            renderStar(matrices, size, distance);
            matrix.pop();
        }
    }

    public static void init() {
        DimensionRenderingRegistry.registerDimensionEffects(SSEDimensions.THE_MOON_ID, new TheMoonDimensionEffects());

        DimensionRenderingRegistry.SkyRenderer theMoonSkyRenderer = context -> {
            if(RANDOM_DOUBLE.isEmpty()){
                Random random = MinecraftClient.getInstance().world.getRandom();
                for (int i = 0; i < 1009; i++) {
                    RANDOM_DOUBLE.add(random.nextDouble());
                }
            }

            if(RANDOM_GAUSSIAN.isEmpty()) {
                Random random = MinecraftClient.getInstance().world.getRandom();
                for(int i = 0; i < 1009; i++) {
                    RANDOM_GAUSSIAN.add(random.nextGaussian());
                }
            }

            MatrixStack matrixStack = context.matrixStack();
            Quaternionf rotation = null;
            Matrix4f matrices = null;

            //Star
            renderStar(matrixStack, 0.2F, 196, 700);
//            int density = 700;
//            pointerDouble = 103;
//            for(int i = 0; i < density; i++) {
//                double z = getRandomDouble() * 360.0F;
//                double y = getRandomDouble() * 360.0F;
//                double x = getRandomDouble() * 360.0F;
//                matrixStack.push();
//                rotation = new Quaternionf()
//                        .rotateZ((float) Math.toRadians(z))
//                        .rotateY((float) Math.toRadians(y))
//                        .rotateX((float) Math.toRadians(x));
//                matrixStack.multiply(rotation);
//                matrices = matrixStack.peek().getPositionMatrix();
//                renderStar(matrices, 0.2F, 196);
//                matrixStack.pop();
//            }

            //Galaxy
            int galaxyDensity = 500;
            pointerGaussian = 107;
            for(int i = 0; i < galaxyDensity / 2; i++) {
                matrixStack.push();
                double xGaussian = getRandomGaussian();
                double yGaussian = getRandomGaussian();
                double kX = 21.0;
                double kY = 1.0;
                double theta = Math.toRadians(30.0);
                double x = 10.0 + kX * xGaussian * Math.cos(theta) - kY * yGaussian * Math.sin(theta);
                double y = 170.0 + kX * xGaussian * Math.sin(theta) + kY * yGaussian * Math.cos(theta);
                rotation = new Quaternionf()
                        .rotateY((float) Math.toRadians(y))
                        .rotateX((float) Math.toRadians(x));
                matrixStack.multiply(rotation);
                matrices = matrixStack.peek().getPositionMatrix();
                renderStar(matrices, 0.2F, 196, (float) Math.toRadians(getRandomDouble() * 360.0));
                matrixStack.pop();
            }
            for(int i = 0; i < galaxyDensity; i++) {
                matrixStack.push();
                double xGaussian = getRandomGaussian();
                double yGaussian = getRandomGaussian();
                double kX = 25.0;
                double kY = 5.0;
                double theta = Math.toRadians(30.0);
                double x = 5.0 + kX * xGaussian * Math.cos(theta) - kY * yGaussian * Math.sin(theta);
                double y = 170.0 + kX * xGaussian * Math.sin(theta) + kY * yGaussian * Math.cos(theta);
                rotation = new Quaternionf()
                        .rotateY((float) Math.toRadians(y))
                        .rotateX((float) Math.toRadians(x));
                matrixStack.multiply(rotation);
                matrices = matrixStack.peek().getPositionMatrix();
                renderStar(matrices, 0.2F, 196, (float) Math.toRadians(getRandomDouble() * 360.0));
                matrixStack.pop();
            }

            //Earth
            matrixStack.push();
            long time = MinecraftClient.getInstance().world.getTimeOfDay();
            rotation = new Quaternionf()
                    .rotateZ((float) Math.toRadians(360.0F * time / 24000))
                    .rotateY((float) Math.toRadians(270));
            matrixStack.multiply(rotation);
            matrices = matrixStack.peek().getPositionMatrix();
            renderCelestialObject(THE_SUN_TEXTURE, matrices, 65.0F, 196);
            matrixStack.pop();

            //Sun
            matrixStack.push();
            rotation = new Quaternionf()
                    .rotateX((float) Math.toRadians(40));
            matrixStack.multiply(rotation);
            matrices = matrixStack.peek().getPositionMatrix();
            renderCelestialObject(THE_EARTH_TEXTURE, matrices, 80.0F, 196);
            matrixStack.pop();
        };

        DimensionRenderingRegistry.registerSkyRenderer(
                RegistryKeys.toWorldKey(SSEDimensions.THE_MOON_KEY),
                theMoonSkyRenderer
        );
    }
}
