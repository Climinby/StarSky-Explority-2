package com.climinby.starsky_explority.sound;

import com.climinby.starsky_explority.StarSkyExplority;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class SSESoundEvents {
    public static final SoundEvent ENTITY_LUNARIAN_SIGHT_LOCK = register("entity.lunarian.sight_lock");
    public static final SoundEvent ENTITY_LUNARIAN_TELEPORT = register("entity.lunarian.teleport");

    public static final SoundEvent ENTITY_PLAYER_RESEARCH_COMPLETE = register("entity.player.research_complete");

    public static final SoundEvent ITEM_RESEARCH_BOOK_USE = register("item.research_book.use");
    public static final SoundEvent ITEM_RESEARCH_BOOK_FAIL = register("item.research_book.fail");

    private static SoundEvent register(String id) {
        Identifier soundId = new Identifier(StarSkyExplority.MOD_ID, id);
        return Registry.register(Registries.SOUND_EVENT, soundId, SoundEvent.of(soundId));
    }

    public static void init() {}
}
