package com.climinby.starsky_explority.util;

import com.climinby.starsky_explority.block.SSEBlocks;
import com.climinby.starsky_explority.item.SSEItems;
import com.climinby.starsky_explority.nbt.player.ResearchLevel;
import com.climinby.starsky_explority.registry.material.MaterialTypes;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.math.Box;

import java.util.List;
import java.util.Random;

public class SSEBlockExtend {
    public static void init() {
        orePopping();
    }

    private static void orePopping() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if(!world.isClient()) {
                if(!player.isCreative()) {
                    Item miningItem = player.getMainHandStack().getItem();
                    if(miningItem instanceof PickaxeItem) {
                        Block brokenBlock = state.getBlock();
                        //Pop out raw aluminium
                        if (brokenBlock == Blocks.IRON_ORE ||
                                brokenBlock == Blocks.DEEPSLATE_IRON_ORE) {
                            float level = ResearchLevel.getLevel(player, MaterialTypes.ALUMINIUM);
                            if (level == 100.0F) {
                                Random random = new Random();
                                int i = random.nextInt(30);
                                System.out.println(i);
                                ItemStack droppedItem;
                                if (i < 5) {
                                    droppedItem = new ItemStack(SSEItems.RAW_ALUMINIUM, 1);
                                } else if (i < 10) {
                                    droppedItem = new ItemStack(SSEItems.RAW_ALUMINIUM, 2);
                                } else {
                                    droppedItem = null;
                                }
                                if (droppedItem != null)
                                    world.getServer().execute(() -> Block.dropStack(world, pos, droppedItem));
                            }
                        }
                        //Pop out raw silver
                        if (brokenBlock == Blocks.GOLD_ORE ||
                                brokenBlock == Blocks.DEEPSLATE_GOLD_ORE) {
                            float level = ResearchLevel.getLevel(player, MaterialTypes.SILVER);
                            if (level == 100.0F) {
                                Random random = new Random();
                                int i = random.nextInt(30);
                                ItemStack droppedItem;
                                if (i < 10) {
                                    droppedItem = new ItemStack(SSEItems.RAW_SILVER, 1);
                                } else {
                                    droppedItem = null;
                                }
                                if (droppedItem != null)
                                    world.getServer().execute(() -> Block.dropStack(world, pos, droppedItem));
                            }
                        }

                        //Anorthosite popping decider
                        if(brokenBlock == SSEBlocks.ANORTHOSITE) {
                            float level = ResearchLevel.getLevel(player, MaterialTypes.LUNAR_CRYSTAL);
                            if(level < 100.0F) {
                                world.getServer().execute(() -> {
                                    List<ItemEntity> itemEntities = world.getEntitiesByClass(
                                            ItemEntity.class,
                                            new Box(pos).expand(3.0),
                                            itemEntity -> true
                                    );
                                    System.out.println("Discarding!!!!!!!!!!!!!!!");
                                    for(ItemEntity itemEntity : itemEntities) {
                                        System.out.println(itemEntity.getStack().getItem());
                                        itemEntity.discard();
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
    }
}
