package com.climinby.starsky_explority.registry.planet;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class Galaxy {
    protected final Map<Identifier, Planet> containedPlanets;
    protected final Settings settings;

    public Galaxy(Settings settings) {
        this.settings = settings;
        containedPlanets = settings.containedPlanets;
    }

    public static class Settings {
        Map<Identifier, Planet> containedPlanets = new HashMap<>();
    }
}
