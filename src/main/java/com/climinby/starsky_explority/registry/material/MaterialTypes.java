package com.climinby.starsky_explority.registry.material;

import com.climinby.starsky_explority.StarSkyExplority;
import com.climinby.starsky_explority.item.ResearchBookItem;
import com.climinby.starsky_explority.item.SSEItems;
import com.climinby.starsky_explority.registry.SSERegistries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class MaterialTypes {
    public static final MaterialType ALUMINIUM = register("aluminium", new MaterialType((ResearchBookItem) SSEItems.RESEARCH_BOOK_ALUMINIUM));
    public static final MaterialType SILVER = register("silver", new MaterialType((ResearchBookItem) SSEItems.RESEARCH_BOOK_SILVER));
    public static final MaterialType LUNAR_CRYSTAL = register("lunar_crystal", new MaterialType((ResearchBookItem) SSEItems.RESEARCH_BOOK_LUNAR_CRYSTAL));
    public static final MaterialType STELLARIUM = register("stellarium", new MaterialType((ResearchBookItem) SSEItems.RESEARCH_BOOK_STELLARIUM));

    public static MaterialType register(String id, MaterialType materialType) {
        MaterialType registeredMaterialType = Registry.register(SSERegistries.MATERIAL_TYPE, new Identifier(StarSkyExplority.MOD_ID, id), materialType);
        return registeredMaterialType;
    }

    public static void init() {}
}
