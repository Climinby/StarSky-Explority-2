package com.climinby.starsky_explority.registry.gravity_changed_dimension;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

public record GravityChangedDimension(RegistryKey<World> dimensionKey, double gravityMultiplier) {
}
