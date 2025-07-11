package com.climinby.starsky_explority.nbt.player;

import com.climinby.starsky_explority.registry.SSERegistries;
import com.climinby.starsky_explority.registry.material.MaterialType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class ResearchLevel {
    public static final String RESEARCH_LEVEL_KEY = "ResearchLevel";

    public static float getLevel(PlayerEntity player, MaterialType material) {
        float level = 0.0F;
        SSEDataHandler handler = (SSEDataHandler) player;
        NbtCompound nbt = handler.getSSEData();
        if(nbt.contains(RESEARCH_LEVEL_KEY)) {
            NbtCompound researchLevels = nbt.getCompound(RESEARCH_LEVEL_KEY);
            if(researchLevels.contains(material.getId())) {
                level = researchLevels.getFloat(material.getId());
            }
        }
        return level;
    }

    public static boolean setLevel(PlayerEntity player, MaterialType material, float level) {
        if(level > 100.0F || level < 0.0F) {
            return false;
        }

        SSEDataHandler handler = (SSEDataHandler) player;
        NbtCompound nbt = handler.getSSEData();
        if(nbt.contains(RESEARCH_LEVEL_KEY)) {
            NbtCompound researchLevels = nbt.getCompound(RESEARCH_LEVEL_KEY);
            researchLevels.putFloat(material.getId(), level);
            nbt.put(RESEARCH_LEVEL_KEY, researchLevels);
        } else {
            NbtCompound researchLevels = new NbtCompound();
            for(MaterialType material2 : SSERegistries.MATERIAL_TYPE) {
                if(material2.equals(material)) {
                    researchLevels.putFloat(material.getId(), level);
                } else {
                    researchLevels.putFloat(material2.getId(), 0.0F);
                }
            }
            nbt.put(RESEARCH_LEVEL_KEY, researchLevels);
        }
        handler.setSSEData(nbt);
        return true;
    }

    public static void addLevel(PlayerEntity player, MaterialType material, float incre) {
        float preLevel = getLevel(player, material);
        float newLevel = preLevel + incre;
        if(newLevel < 0.0F) {
            newLevel = 0.0F;
        } else if(newLevel > 100.0F) {
            newLevel = 100.0F;
        }
        setLevel(player, material, newLevel);
    }
}
