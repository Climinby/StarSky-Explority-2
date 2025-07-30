package com.climinby.starsky_explority.world;

import net.minecraft.world.gen.placementmodifier.*;

import java.util.List;

public class SSEOrePlacement {
    public static List<PlacementModifier> modifiers(PlacementModifier count, PlacementModifier height) {
        return List.of(count, SquarePlacementModifier.of(), height, BiomePlacementModifier.of());
    }

    public static List<PlacementModifier> modifiersWithCount(int count, PlacementModifier height) {
        return modifiers(CountPlacementModifier.of(count), height);
    }

    public static List<PlacementModifier> modifiersWithRarity(int chance, PlacementModifier height) {
        return modifiers(RarityFilterPlacementModifier.of(chance), height);
    }

    public static List<PlacementModifier> modifiersWithRarityAndCount(int chance, int count, PlacementModifier height) {
        return List.of(
                RarityFilterPlacementModifier.of(chance),
                CountPlacementModifier.of(count),
                SquarePlacementModifier.of(),
                height,
                BiomePlacementModifier.of()
        );
    }
}
