package com.climinby.starsky_explority.registry.ink;

import com.climinby.starsky_explority.registry.SSERegistries;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.Objects;

public record InkType(Item item, Identifier texture, int incre, int analysisTime) {
    public InkType {
        if(item == null) {
            throw new NullPointerException("The item of a ink type must not be null");
        }
        if (incre <= 0 || incre > 100) {
            throw new RuntimeException("The increment of a registering ink type must be between 0 and 100 (cannot be 0)");
        }
        if (analysisTime <= 0) {
            throw new RuntimeException("Analysis Time of an Ink Type could not be negative");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InkType inkType = (InkType) o;
        return SSERegistries.INK_TYPE.getId(this).equals(SSERegistries.INK_TYPE.getId(inkType));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(SSERegistries.INK_TYPE.getId(this));
    }
}
