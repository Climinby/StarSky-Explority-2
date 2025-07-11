package com.climinby.starsky_explority.registry.sample;

import com.climinby.starsky_explority.item.SSEItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Sample {
    private final Item sampleItem;
    private final Set<Block> collectableBlocks;
    private final float odds;

    public Sample(Settings settings) {
        this.sampleItem = settings.sampleItem;
        this.collectableBlocks = settings.collectableBlocks;
        this.odds = settings.odds;
    }

    public Set<Block> getCollectable() {
        return collectableBlocks;
    }

    public Item getSampleItem() {
        return sampleItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sample sample = (Sample) o;
        return Objects.equals(sampleItem, sample.sampleItem);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sampleItem);
    }

    public float getOdds() {
        return odds;
    }

    public static class Settings {
        Item sampleItem = SSEItems.SAMPLE_EMPTY;
        Set<Block> collectableBlocks = new HashSet<>();
        float odds = 1.0F;

        public Settings setSampleItem(Item sample) {
            this.sampleItem = sample;
            return this;
        }

        public Settings addBlocks(Block newBlock) {
            this.collectableBlocks.add(newBlock);
            return this;
        }

        public Settings setBlocks(Set<Block> blocks) {
            this.collectableBlocks.addAll(blocks);
            return this;
        }

        public Settings setOdds(float odds) {
            this.odds = odds;
            return this;
        }
    }
}
