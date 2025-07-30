package com.climinby.starsky_explority.world.gen;

public class SSEWorldGeneration {
    public static void init() {
        SSEOreGeneration.generateOres();
        MeteorCraterGeneration.generateMeteorCrater();
        VegetationGeneration.generateVegetation();
        // Add more world generation features here as needed
    }
}
