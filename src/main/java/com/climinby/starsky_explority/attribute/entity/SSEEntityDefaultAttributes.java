package com.climinby.starsky_explority.attribute.entity;

import com.climinby.starsky_explority.entity.SSEEntities;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;

public class SSEEntityDefaultAttributes {
    public static void init() {
        FabricDefaultAttributeRegistry.register(SSEEntities.LUNARIAN_ENTITY, MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.5)
        );
    }
}
