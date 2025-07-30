package com.climinby.starsky_explority.world.feature;

import com.climinby.starsky_explority.block.SSEBlocks;
import com.climinby.starsky_explority.world.biome.SSEBiomeKeys;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MoonVegetationFeature extends Feature<MoonVegetationConfig> {
    public MoonVegetationFeature(Codec<MoonVegetationConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<MoonVegetationConfig> context) {
        StructureWorldAccess world = context.getWorld();
        Random random = context.getRandom();
        MoonVegetationConfig config = context.getConfig();
        BlockPos origin = context.getOrigin();

        if (!config.biome().biomeKey.equals(SSEBiomeKeys.THORNWILDS)) {
            return false; // Only generate in the specified biome
        }

        int dispersion = config.dispersion();
        for (int i = 0; i < 4; i++) {
            if (random.nextInt(dispersion) == 0) {
                int x = origin.getX() + random.nextInt(16);
                int z = origin.getZ() + random.nextInt(16);
                BlockPos genPos = new BlockPos(
                        x,
                        getTopY(world, new BlockPos(x, 0, z)) + 1,
                        z
                );
                generateCryocacta(world, genPos, random);
            }
        }

        return true;
    }

    private void generateCryocacta(StructureWorldAccess world, BlockPos pos, Random random) {
        int height = 10 + random.nextInt(6);
        for (int y = -1; y < height; y++) {
            BlockPos currPos = pos.up(y);
            BlockState state1 = world.getBlockState(currPos);
            BlockState state2 = world.getBlockState(currPos.add(0, 0, 1));
            BlockState state3 = world.getBlockState(currPos.add(1, 0, 0));
            BlockState state4 = world.getBlockState(currPos.add(1, 0, 1));

            if (y == -1) {
                if (
                        (state1.getBlock() == SSEBlocks.MOONVEIL_MOSS) &&
                        (state2.getBlock() == SSEBlocks.MOONVEIL_MOSS) &&
                        (state3.getBlock() == SSEBlocks.MOONVEIL_MOSS) &&
                        (state4.getBlock() == SSEBlocks.MOONVEIL_MOSS)
                ) {
                    continue;
                } else {
                    return;
                }
            }

            if (
                    (state1.isAir() || state1.isReplaceable()) &&
                    (state2.isAir() || state2.isReplaceable()) &&
                    (state3.isAir() || state3.isReplaceable()) &&
                    (state4.isAir() || state4.isReplaceable())
            ) {
                world.setBlockState(currPos, SSEBlocks.CRYOCACTA.getDefaultState(), Block.NOTIFY_ALL);
                world.setBlockState(currPos.add(0, 0, 1), SSEBlocks.CRYOCACTA.getDefaultState(), Block.NOTIFY_LISTENERS);
                world.setBlockState(currPos.add(1, 0, 0), SSEBlocks.CRYOCACTA.getDefaultState(), Block.NOTIFY_LISTENERS);
                world.setBlockState(currPos.add(1, 0, 1), SSEBlocks.CRYOCACTA.getDefaultState(), Block.NOTIFY_LISTENERS);
            } else {
                height = y - 1;
                break;
            }
        }

        int branchStart = (int) (height * 0.45);
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos startPos = pos
                    .up(branchStart)
                    .add(0, random.nextInt(3) - 1, 0);
            if (direction == Direction.NORTH) {
                startPos = startPos.add(random.nextInt(2), 0, -1);
            } else if (direction == Direction.SOUTH) {
                startPos = startPos.add(random.nextInt(2), 0, 2);
            } else if (direction == Direction.WEST) {
                startPos = startPos.add(-1, 0, random.nextInt(2));
            } else if (direction == Direction.EAST) {
                startPos = startPos.add(2, 0, random.nextInt(2));
            }
            int branchHeight = 2 + random.nextInt(3);
            List<BlockPos> branchPos = new ArrayList<>();
            Vec3i vector = direction.getVector();
            int branchHorizontalLength = 2 + random.nextInt(2);
            for(int i = 0; i < branchHorizontalLength; i++) {
                branchPos.add(startPos.add(vector.getX() * i, 0, vector.getZ() * i));
            }
            for(int i = 0; i < branchHeight; i++) {
                branchPos.add(startPos.add(vector.getX() * (branchHorizontalLength - 1), i + 1, vector.getZ() * (branchHorizontalLength - 1)));
            }
            for (BlockPos branch : branchPos) {
                BlockState state = world.getBlockState(branch);
                if (state.isAir() || state.isReplaceable() || state.getBlock() == Blocks.AMETHYST_CLUSTER) {
                    BlockState cryocactaState = SSEBlocks.CRYOCACTA.getDefaultState();
                    List<Direction> amethystDirs = null;
                    if (branch.getY() == startPos.getY()) {
                        cryocactaState = SSEBlocks.CRYOCACTA.getDefaultState()
                                .with(Properties.AXIS, direction.getAxis());
                        amethystDirs = Arrays.stream(Direction.values()).filter(
                                dir -> dir != direction && dir != direction.getOpposite()
                        ).toList();
                    } else {
                        amethystDirs = Direction.Type.HORIZONTAL.stream().toList();
                    }
                    world.setBlockState(branch, cryocactaState, Block.NOTIFY_LISTENERS);

                    for (Direction amethystDir : amethystDirs) {
                        if (random.nextInt(8) != 0) {
                            continue;
                        }
                        BlockPos amethystPos = branch.offset(amethystDir);
                        BlockState amethystState = world.getBlockState(amethystPos);
                        if (amethystState.isAir() || amethystState.isReplaceable()) {
                            world.setBlockState(amethystPos, Blocks.AMETHYST_CLUSTER.getDefaultState()
                                    .with(Properties.FACING, amethystDir), Block.NOTIFY_LISTENERS);
                        }
                    }
                } else {
                    break;
                }
            }

            List<BlockPos> mainAmethystPos = new ArrayList<>();
            BlockPos rootPos = pos;
            if (direction == Direction.NORTH) {
                rootPos = rootPos.add(0, 0, -1);
            } else if (direction == Direction.SOUTH) {
                rootPos = rootPos.add(0, 0, 2);
            } else if (direction == Direction.WEST) {
                rootPos = rootPos.add(-1, 0, 0);
            } else if (direction == Direction.EAST) {
                rootPos = rootPos.add(2, 0, 0);
            }
            for (int i = 0; i < height; i++) {
                mainAmethystPos.add(rootPos.up(i));
                mainAmethystPos.add(rootPos.up(i).add(Math.abs(direction.getOffsetZ()), 0, Math.abs(direction.getOffsetX())));
            }

            for (BlockPos amethystPos : mainAmethystPos) {
                if (random.nextInt(8) != 0) {
                    continue;
                }
                BlockState state = world.getBlockState(amethystPos);
                if (state.isAir() || state.isReplaceable()) {
                    world.setBlockState(amethystPos, Blocks.AMETHYST_CLUSTER.getDefaultState()
                            .with(Properties.FACING, direction), Block.NOTIFY_LISTENERS);
                }
            }
        }
    }

    private int getTopY(StructureWorldAccess world, BlockPos pos) {
        for (int y = world.getTopY(); y >= world.getBottomY(); y--) {
            BlockState state = world.getBlockState(pos.withY(y));
            if (!state.isAir() && !state.isReplaceable()) {
                return y;
            }
        }
        return world.getBottomY(); // No solid block found
    }
}
