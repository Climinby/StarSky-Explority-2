package com.climinby.starsky_explority.registry.planet;

import com.climinby.starsky_explority.StarSkyExplority;
import com.climinby.starsky_explority.registry.SSERegistries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class Galaxies {
    public static final Galaxy SOLAR_SYSTEM = register("solar", new Galaxy(new Galaxy.Settings()));

    private static Galaxy register(String id, Galaxy galaxy) {
        Galaxy registeredGalaxy = Registry.register(SSERegistries.GALAXY, new Identifier(StarSkyExplority.MOD_ID, id), galaxy);
        return registeredGalaxy;
    }

    public static void initialize() {}
}
