package com.climinby.starsky_explority.entity.effect;

import com.climinby.starsky_explority.StarSkyExplority;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SSEStatusEffects {
    public static final StatusEffect REFLUX = register("reflux", new RefluxStatusEffect());

    private static StatusEffect register(String name, StatusEffect statusEffect) {
        Identifier id = new Identifier(StarSkyExplority.MOD_ID, name);
        return Registry.register(Registries.STATUS_EFFECT, id, statusEffect);
    }

    public static void init() {}
}
