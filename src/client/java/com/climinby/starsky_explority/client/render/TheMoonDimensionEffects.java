package com.climinby.starsky_explority.client.render;

import net.minecraft.client.render.DimensionEffects;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class TheMoonDimensionEffects extends DimensionEffects {
    public TheMoonDimensionEffects() {
        super(Float.NaN, false, SkyType.NORMAL, false, false);
    }

    @Override
    public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
        return new Vec3d(0.02, 0.01, 0.03);
    }

    @Override
    public boolean useThickFog(int camX, int camY) {
        return false;
    }

    @Nullable
    @Override
    public float[] getFogColorOverride(float skyAngle, float tickDelta) {
        return null;
    }
}
