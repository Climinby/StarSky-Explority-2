package com.climinby.starsky_explority.registry.planet;

import com.climinby.starsky_explority.StarSkyExplority;
import com.climinby.starsky_explority.registry.SSERegistries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class Planets {
    public static final Planet EMPTY = register("empty", new Planet(new AbstractPlanet.Settings()));
    public static final Planet EARTH = register("earth", new Planet(new AbstractPlanet.Settings()));
    public static final Planet MOON = register("moon", new Planet(new AbstractPlanet.Settings()));
    public static final Planet MARS = register("mars", new Planet(new AbstractPlanet.Settings()));
    public static final Planet VENUS = register("venus", new Planet(new AbstractPlanet.Settings()));
    public static final Planet MERCURY = register("mercury", new Planet(new AbstractPlanet.Settings()));

    public static Planet register(String id, Planet planet) {
        Planet registeredPlanet = Registry.register(SSERegistries.PLANET, new Identifier(StarSkyExplority.MOD_ID, id), planet);
        return registeredPlanet;
    }

    public static void initialize() {}
}
