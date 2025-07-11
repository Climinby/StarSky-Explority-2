package com.climinby.starsky_explority;

import com.climinby.starsky_explority.block.SSEBlocks;
import com.climinby.starsky_explority.command.SSECommands;
import com.climinby.starsky_explority.entity.SSEEntities;
import com.climinby.starsky_explority.attribute.entity.SSEEntityDefaultAttributes;
import com.climinby.starsky_explority.entity.effect.SSEStatusEffects;
import com.climinby.starsky_explority.item.SSEItemGroups;
import com.climinby.starsky_explority.item.SSEItems;
import com.climinby.starsky_explority.recipe.SSERecipeType;
import com.climinby.starsky_explority.recipe.SSERecipiSerializer;
import com.climinby.starsky_explority.registry.SSERegistries;
import com.climinby.starsky_explority.registry.SSERegistryKeys;
import com.climinby.starsky_explority.registry.ink.InkTypes;
import com.climinby.starsky_explority.registry.material.MaterialTypes;
import com.climinby.starsky_explority.registry.planet.Galaxies;
import com.climinby.starsky_explority.registry.planet.Planets;
import com.climinby.starsky_explority.registry.sample.SampleTypes;
import com.climinby.starsky_explority.screen.SSEScreenHandlers;
import com.climinby.starsky_explority.sound.SSESoundEvents;
import com.climinby.starsky_explority.util.SSEBlockExtend;
import com.climinby.starsky_explority.util.SSEServerDataReceiver;
import com.climinby.starsky_explority.world.biome.SSEBiomeKeys;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StarSkyExplority implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "starsky_explority";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		SSERegistryKeys.initialize();
		SSERegistries.initialize();
		SSEServerDataReceiver.initialize();
		SSEBlockExtend.init();

		SSEBiomeKeys.init();
		SSEItems.initialize();
		SSEBlocks.initialize();
		SSEItemGroups.initialize();
		SSEEntities.initialize();
		SSESoundEvents.init();
		SSEScreenHandlers.initialize();
		InkTypes.initialize();
		Galaxies.initialize();
		Planets.initialize();
		SampleTypes.initialize();
		MaterialTypes.init();
		SSERecipiSerializer.init();
		SSERecipeType.init();
		SSEEntityDefaultAttributes.init();
		SSEStatusEffects.init();

		SSECommands.init();

		LOGGER.info("StarSkyExplority initialized successfully!");
	}
}