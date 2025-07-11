package com.climinby.starsky_explority.registry.material;

import com.climinby.starsky_explority.item.ResearchBookItem;
import com.climinby.starsky_explority.item.SSEItems;
import com.climinby.starsky_explority.registry.SSERegistries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.io.Serializable;
import java.util.Objects;

public class MaterialType implements Serializable {
    public static final MaterialType EMPTY = new MaterialType((ResearchBookItem) SSEItems.RESEARCH_BOOK);
    private transient final ResearchBookItem researchBookItem;
    private String translationKey;

    public MaterialType(ResearchBookItem researchBookItem) {
        this.researchBookItem = researchBookItem;
        translationKey = null;
//        if(materialTypeId != null) {
//            String path = materialTypeId.getPath();
//            String namespace = materialTypeId.getNamespace();
//            String translatableKey = "material." + namespace + "." + path;
//            this.translationKey = Text.translatable(translatableKey);
//        } else {
//            translationKey = Text.empty();
//        }
    }

    public ResearchBookItem getResearchBookItem() {
        return researchBookItem;
    }

    public String getId() {
        Identifier id = SSERegistries.MATERIAL_TYPE.getId(this);
        if(id != null) return id.toString();
        return "starsky_explority:empty";
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        MaterialType that = (MaterialType) o;
//        return Objects.equals(scrollItem, that.scrollItem) && Objects.equals(nbtKey, that.nbtKey);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(scrollItem, nbtKey);
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaterialType that = (MaterialType) o;
        return Objects.equals(SSERegistries.MATERIAL_TYPE.getId(this), SSERegistries.MATERIAL_TYPE.getId(that));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(SSERegistries.MATERIAL_TYPE.getId(this));
    }

    public boolean isEmpty() {
        if(this == EMPTY) {
            return true;
        }
        return false;
    }

    public String getTranslationKey() {
        return getOrCreateTranslationKey();
    }

    protected String getOrCreateTranslationKey() {
        if(this.translationKey == null) {
            this.translationKey = Util.createTranslationKey("material", SSERegistries.MATERIAL_TYPE.getId(this));
        }

        return this.translationKey;
    }
}
