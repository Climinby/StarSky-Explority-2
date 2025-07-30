package com.climinby.starsky_explority.world.feature;

import com.climinby.starsky_explority.block.SSEBlocks;
import com.climinby.starsky_explority.world.biome.SSEBiomeKeys;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.dynamic.Range;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MoonMeteorCraterFeature extends Feature<MoonMeteorCraterConfig> {
    private static final double RATIO_PEEK_TO_BOTTOM = 0.5;
    private static final double RATIO_RADIUS_TO_DEPTH = Math.tan(Math.PI / 6.0); // 30 degrees

    private static final int SUM_OF_WEIGHTS = 100;
    private static final int WEIGHT_OF_ORIGINAL = 96;
    private static final int WEIGHT_OF_OBSIDIAN = 3;
    private static final int WEIGHT_OF_AEROLITE_SOIL = 1;

    public MoonMeteorCraterFeature(Codec<MoonMeteorCraterConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<MoonMeteorCraterConfig> context) {
        StructureWorldAccess world = context.getWorld();
//        if (world.getServer() == null) return false;
//        StructureTemplateManager manager = world.getServer().getStructureTemplateManager();

        Random random = context.getRandom();

        MoonMeteorCraterConfig config = context.getConfig();


        if (random.nextInt(config.dispersion()) > 0) {
            return false;
        }

        BlockPos center = context.getOrigin().add(
                random.nextInt(16),
                0,
                random.nextInt(16)
        );

        int height = world.getTopY();
        for (; height > world.getBottomY(); height--) {
            BlockState blockState = world.getBlockState(center.add(0, height, 0));
            if (!blockState.isAir()) {
                break;
            }
        }

        // Check if the biome is "Lunar Plains" or "Lunar Wasteland"
        Optional<RegistryKey<Biome>> biomeKey = world.getBiome(center.add(0, height, 0)).getKey();
        if (biomeKey.isEmpty() || (!biomeKey.get().equals(SSEBiomeKeys.LUNAR_PLAINS) && !biomeKey.get().equals(SSEBiomeKeys.LUNAR_WASTELAND))) {
            return false; // Only generate in specific biomes
        }

        int radius = random.nextInt(getLength(config.radiusRange())) + config.radiusRange().minInclusive();
        int maxImpact = getMaxImpact(radius);

        List<BlockPos> craterPos = new ArrayList<>();
        for (int x = -maxImpact; x <= maxImpact; x++) {
            for (int z = -maxImpact; z <= maxImpact; z++) {
                if (x * x + z * z > maxImpact * maxImpact) {
                    continue; // Outside the maximum impact area
                }
                craterPos.add(center.add(x, 0, z));
            }
        }

        for (BlockPos pos : craterPos) {
            double elevation = getElevation(center, pos ,radius);
            if (elevation > 0) {
                BlockPos topPos = getTopBlockPos(world, pos.getX(), pos.getZ());
                BlockState topState = getTopBlockState(world, pos.getX(), pos.getZ());
                if (topState.getBlock() == SSEBlocks.MOONVEIL_MOSS) {
                    topState = SSEBlocks.MOON_SOIL.getDefaultState();
                    genCraterBlock(world, topPos, topState, random);
                }
                int growth = (int) elevation;
                double fractionalPart = elevation - growth;

                for (int i = 0; i < growth; i++) {
                    BlockPos targetPos = topPos.up(i + 1);
                    genCraterBlock(world, targetPos, topState, random);
                }

                if (random.nextDouble() < fractionalPart / 2.0) {
                    BlockPos targetPos = topPos.up(growth + 1);
                    genCraterBlock(world, targetPos, topState, random);
                }
            } else {
                BlockPos topPos = getTopBlockPos(world, pos.getX(), pos.getZ());
                int depth = (int) -elevation;
                double fractionalPart = -depth - elevation;

                for (int i = -1; i < depth; i++) {
                    BlockPos targetPos = topPos.down(i + 1);
                    world.setBlockState(targetPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                }
                if (random.nextDouble() < fractionalPart / 2.0) {
                    BlockPos targetPos = topPos.down(depth + 1);
                    world.setBlockState(targetPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                    int blockChooser = random.nextInt(SUM_OF_WEIGHTS);
                    if (blockChooser < WEIGHT_OF_OBSIDIAN) {
                        world.setBlockState(targetPos.down(), Blocks.OBSIDIAN.getDefaultState(), Block.NOTIFY_LISTENERS);
                    } else if (blockChooser - WEIGHT_OF_OBSIDIAN < WEIGHT_OF_AEROLITE_SOIL) {
                        world.setBlockState(targetPos.down(), SSEBlocks.AEROLITE_MOON_SOIL.getDefaultState(), Block.NOTIFY_LISTENERS);
                    }
                } else {
                    int blockChooser = random.nextInt(SUM_OF_WEIGHTS);
                    if (blockChooser < WEIGHT_OF_OBSIDIAN) {
                        world.setBlockState(topPos.down(depth + 1), Blocks.OBSIDIAN.getDefaultState(), Block.NOTIFY_LISTENERS);
                    } else if (blockChooser - WEIGHT_OF_OBSIDIAN < WEIGHT_OF_AEROLITE_SOIL) {
                        world.setBlockState(topPos.down(depth + 1), SSEBlocks.AEROLITE_MOON_SOIL.getDefaultState(), Block.NOTIFY_LISTENERS);
                    }
                }
            }
        }

        return true;
    }

    private void genCraterBlock(StructureWorldAccess world, BlockPos pos, BlockState topState, Random random) {
        double blockChooser = random.nextInt(SUM_OF_WEIGHTS);
        if (blockChooser < WEIGHT_OF_OBSIDIAN) {
            world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState(), Block.NOTIFY_LISTENERS);
        } else if (blockChooser - WEIGHT_OF_OBSIDIAN < WEIGHT_OF_AEROLITE_SOIL) {
            world.setBlockState(pos, SSEBlocks.AEROLITE_MOON_SOIL.getDefaultState(), Block.NOTIFY_LISTENERS);
        } else {
            world.setBlockState(pos, topState, Block.NOTIFY_LISTENERS);
        }
    }

    private int getMaxImpact(int radius) {
        double k = Math.pow(radius, 3) * RATIO_RADIUS_TO_DEPTH * (1 - RATIO_PEEK_TO_BOTTOM);
        double epsilon = 0.5;
        return (int) Math.pow(k / epsilon, 0.5);
    }

    private double getElevation(BlockPos center, BlockPos targetPos, int radius) {
        double squaredDistance = center.getSquaredDistance(targetPos);

        double depth = RATIO_RADIUS_TO_DEPTH * radius;
        double peek = depth * (1 - RATIO_PEEK_TO_BOTTOM);

        if (squaredDistance > radius * radius) {
            // Outside the crater, the elevation is following the function k / r^2
            double k = Math.pow(radius, 2) * peek;
            return k / squaredDistance;
        } else {
            // Inside the crater, the elevation surface is a ball surface
            double R = Math.sqrt(Math.pow(RATIO_RADIUS_TO_DEPTH * radius, 2) + Math.pow(radius, 2));

            double targetDepth = Math.sqrt(R * R - squaredDistance) - (R - depth);
            return peek - targetDepth;
        }
    }

    private int getLength(Range<Integer> range) {
        return range.maxInclusive() - range.minInclusive() + 1;
    }

    private BlockPos getTopBlockPos(StructureWorldAccess world, int x, int z) {
        BlockPos pos = new BlockPos(x, world.getTopY(), z);
        while (pos.getY() > world.getBottomY() && world.getBlockState(pos).isAir()) {
            pos = pos.down();
        }
        return pos;
    }

    private BlockState getTopBlockState(StructureWorldAccess world, int x, int z) {
        return world.getBlockState(getTopBlockPos(world, x, z));
    }
}
