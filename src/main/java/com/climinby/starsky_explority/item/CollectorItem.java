package com.climinby.starsky_explority.item;

import com.climinby.starsky_explority.registry.SSERegistries;
import com.climinby.starsky_explority.registry.sample.Sample;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class CollectorItem extends ToolItem {
    private final ToolMaterial material;

    public CollectorItem(ToolMaterial material, Settings settings) {
        super(material, settings);
        this.material = material;
    }

    @Override
    public boolean isSuitableFor(BlockState state) {
        Block block = state.getBlock();
        for(Sample sampleType : SSERegistries.SAMPLE_TYPE) {
            for(Block suitable : sampleType.getCollectable()) {
                if(suitable == block) return true;
            }
        }
        return false;
    }
    private Set<Item> getRegionOf(BlockState state) {
        Block block = state.getBlock();
        Set<Item> items = new HashSet<>();
        for(Sample sampleType : SSERegistries.SAMPLE_TYPE) {
            for(Block suitable : sampleType.getCollectable()) {
                if(suitable == block) {
                    items.add(sampleType.getSampleItem());
                }
            }
        }
        if(items.isEmpty()) {
            items.add(SSEItems.SAMPLE_EMPTY);
        }
        return items;
    }

    private float getOdds(Item sample) {
        for(Sample sampleType : SSERegistries.SAMPLE_TYPE) {
            if(sampleType.getSampleItem() == sample) {
                return sampleType.getOdds();
            }
        }
        return 0.0F;
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack itemStack, BlockState state) {
        if (isSuitableFor(state)) {
            if(material == ToolMaterials.IRON) {
                return 0.5F;
            } else if(material == ToolMaterials.DIAMOND) {
                return 0.85F;
            }
            return 1.2F;
        }
        return super.getMiningSpeedMultiplier(itemStack, state);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient()) {
            if (isSuitableFor(state)) {
                world.getServer().execute(() -> {
                    List<ItemEntity> droppedItems = world.getEntitiesByClass(
                            ItemEntity.class, new Box(pos), item -> true
                    );
                    for(ItemEntity itemEntity : droppedItems) {
                        itemEntity.discard();
                    }
                    for(Item sample : getRegionOf(state)) {
                        int portion = 100000000;
                        portion = (int)((float)portion * getOdds(sample));
                        if(new Random().nextInt(100000000) < portion) {
                            ItemStack droppedSample = new ItemStack(sample);
                            Block.dropStack(world, pos, droppedSample);
                        }
                    }
                    stack.damage(1, miner, (entity) -> entity.sendToolBreakStatus(miner.getActiveHand()));
                });
                return true;
            } else {
                stack.damage(1, miner, (entity) -> entity.sendToolBreakStatus(miner.getActiveHand()));
                super.postMine(stack, world, state, pos, miner);
                return true;
            }
        }
        return true;
    }
}

