package com.climinby.starsky_explority.registry.planet;

import java.awt.*;

public abstract class AbstractPlanet {
    protected final Settings settings;
    protected final float dayTimeMultiplier;
    protected final Galaxy belongingGalaxy;
    protected Color atmosphereColor;

    protected AbstractPlanet(Settings settings) {
        this.settings = settings;
        dayTimeMultiplier = settings.dayTime;
        belongingGalaxy = settings.belongingGalaxy;
        atmosphereColor = settings.atmosphereColor;
    }

    protected Galaxy getBelongingGalaxy() {
        return belongingGalaxy;
    }

    public Color getAtmosphereColor() {
        return atmosphereColor;
    }

    public void setAtmosphereColor(Color atmosphereColor) {
        this.atmosphereColor = atmosphereColor;
    }

    public static class Settings {
        float dayTime = 10.0F;
        Galaxy belongingGalaxy = Galaxies.SOLAR_SYSTEM;
        Color atmosphereColor = new Color(0, 0, 0);

        public Settings setAtmosphereColor(Color color) {
            this.atmosphereColor = color;
            return this;
        }

        public Settings setGalaxy(Galaxy galaxy) {
            this.belongingGalaxy = galaxy;
            return this;
        }

        public Settings setDayTime(int dayTime) {
            this.dayTime = dayTime;
            return this;
        }
    }
}
