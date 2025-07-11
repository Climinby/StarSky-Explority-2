package com.climinby.starsky_explority.item;

import com.climinby.starsky_explority.StarSkyExplority;
import com.climinby.starsky_explority.entity.SSEEntities;
import com.climinby.starsky_explority.entity.effect.SSEStatusEffects;
import com.climinby.starsky_explority.registry.planet.Planets;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SSEItems {
    public static final Item IRON_COLLECTOR = register("iron_collector", new CollectorItem(ToolMaterials.IRON, new Item.Settings()
            .maxCount(1)
            .maxDamage(120)));
    public static final Item DIAMOND_COLLECTOR = register("diamond_collector", new CollectorItem(ToolMaterials.DIAMOND, new Item.Settings()
            .maxCount(1)
            .maxDamage(360)));
    public static final Item NETHERITE_COLLECTOR = register("netherite_collector", new CollectorItem(ToolMaterials.NETHERITE, new Item.Settings()
            .maxCount(1)
            .maxDamage(630)));

    public static final Item RAW_ALUMINIUM = register("raw_aluminium", new Item(new Item.Settings()));
    public static final Item RAW_SILVER = register("raw_silver", new Item(new Item.Settings()));
    public static final Item SILVER_NUGGET = register("silver_nugget", new Item(new Item.Settings()));
    public static final Item SILVER_INGOT = register("silver_ingot", new Item(new Item.Settings()));
    public static final Item ALUMINIUM_INGOT = register("aluminium_ingot", new Item(new Item.Settings()));
    public static final Item LUNAR_CRYSTAL = register("lunar_crystal", new Item(new Item.Settings()));
    public static final Item STELLARIUM_INGOT = register("stellarium_ingot", new StellariumItem(new Item.Settings()));
    public static final Item SAMPLE_EMPTY = register("sample_empty", new SampleItem(new Item.Settings(), Planets.EMPTY));
    public static final Item SAMPLE_EARTH = register("sample_earth", new SampleItem(new Item.Settings(), Planets.EARTH));
    public static final Item SAMPLE_MOON = register("sample_moon", new SampleItem(new Item.Settings(), Planets.MOON));
    public static final Item SAMPLE_MARS = register("sample_mars", new SampleItem(new Item.Settings(), Planets.MARS));
    public static final Item SAMPLE_VENUS = register("sample_venus", new SampleItem(new Item.Settings(), Planets.VENUS));
    public static final Item SAMPLE_MERCURY = register("sample_mercury", new SampleItem(new Item.Settings(), Planets.MERCURY));
    public static final Item RESEARCH_BOOK = register("research_book_empty", new ResearchBookItem(new Item.Settings()));
    public static final Item RESEARCH_BOOK_SILVER = register("research_book_silver", new ResearchBookItem(SILVER_INGOT,0.25F ,new Item.Settings()));
    public static final Item RESEARCH_BOOK_ALUMINIUM = register("research_book_aluminium", new ResearchBookItem(ALUMINIUM_INGOT, 0.125F, new Item.Settings()));
    public static final Item RESEARCH_BOOK_LUNAR_CRYSTAL = register("research_book_lunar_crystal", new ResearchBookItem(LUNAR_CRYSTAL, 0.16667F, new Item.Settings()));
    public static final Item RESEARCH_BOOK_STELLARIUM = register("research_book_stellarium", new ResearchBookItem(STELLARIUM_INGOT, 0.015625F,new Item.Settings()));

    public static final Item MOON_PEBBLE = register("moon_pebble", new Item(new Item.Settings()));

    public static final Item MOON_CAKE = register("moon_cake", new Item(new Item.Settings()
            .food(new FoodComponent.Builder()
                    .hunger(6)
                    .saturationModifier(5.5F)
                    .statusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 180 * 20, 0), 1.0F)
                    .build())));
    public static final Item SILVER_MOON_CAKE = register("silver_moon_cake", new Item(new Item.Settings()
            .food(new FoodComponent.Builder()
                    .hunger(4)
                    .saturationModifier(3.0F)
                    .statusEffect(new StatusEffectInstance(SSEStatusEffects.REFLUX, 60 * 20, 0), 1.0F)
                    .build())));

    public static final Item LUNARIAN_SPAWN_EGG = register("lunarian_spawn_egg", new SpawnEggItem(
            SSEEntities.LUNARIAN_ENTITY,
            0xF1F1F1,
            0xCB5DE4,
            new Item.Settings()
    ));

    public static Item register(String id, Item item) {
        Identifier itemID = Identifier.of(StarSkyExplority.MOD_ID, id);
        Item registeredItem = Registry.register(Registries.ITEM, itemID, item);
        return registeredItem;
    }

    public static void initialize() {}
}
