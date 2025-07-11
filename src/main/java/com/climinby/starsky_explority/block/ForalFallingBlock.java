package com.climinby.starsky_explority.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.tick.TickPriority;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForalFallingBlock extends FallingBlock {
    private static final MapCodec<ForalFallingBlock> CODEC = createCodec(settings -> new ForalFallingBlock(SSEBlocks.MOON_SOIL, settings));
    private final Block soilBlock;
    private boolean firstDegenerationCheck = true;
    private int degenerateTime = -1;
    private Map<BlockPos, Integer> degenerateTimes = new HashMap<>();

    public ForalFallingBlock(Block soilBlock, Settings settings) {
        super(settings);
        this.soilBlock = soilBlock;
    }

    @Override
    protected MapCodec<? extends FallingBlock> getCodec() {
        return CODEC;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if(!(canFallThrough(world.getBlockState(pos.down())) && pos.getY() >= world.getBottomY())) {
            BlockState upState = world.getBlockState(pos.up());
            if(!upState.isAir() && !upState.isTransparent(world, pos.up())) {
                Integer degenerateTime = this.degenerateTimes.get(pos);
                if (degenerateTime == null) {
                    this.degenerateTimes.put(pos, (30 + random.nextInt(61)) * 20);
                } else {
                    if(degenerateTime == 0) {
                        this.degenerateTimes.remove(pos);
                        world.setBlockState(pos, this.soilBlock.getDefaultState());
                    } else {
                        this.degenerateTimes.put(pos, degenerateTime - 1);
                    }
                }
                world.scheduleBlockTick(pos, this, 0, TickPriority.VERY_LOW);
            } else {
                Integer degenerateTime = this.degenerateTimes.get(pos);
                if(degenerateTime != null) this.degenerateTimes.remove(pos);
            }
        } else {
            FallingBlockEntity fallingBlockEntity = FallingBlockEntity.spawnFromBlock(world, pos, state);
            this.configureFallingBlockEntity(fallingBlockEntity);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack mainHandStack = player.getMainHandStack();
        ItemStack offHandStack = player.getOffHandStack();
        if((hand == Hand.MAIN_HAND && mainHandStack.isOf(Items.BONE_MEAL)) ||
                (hand == Hand.OFF_HAND && offHandStack.isOf(Items.BONE_MEAL))) {
            if(!world.isClient()) {
                if (!player.isCreative() && !player.isSpectator()) {
                    if (hand == Hand.MAIN_HAND) {
                        mainHandStack.decrement(1);
                    } else {
                        offHandStack.decrement(1);
                    }
                }
                List<BlockPos> spreadingPoses = new ArrayList<>();
                for (int deltaX = -4; deltaX <= 4; deltaX++) {
                    for (int deltaY = -4; deltaY <= 4; deltaY++) {
                        for (int deltaZ = -4; deltaZ <= 4; deltaZ++) {
                            if (deltaX == 0 && deltaY == 0 && deltaZ == 0) {
                                continue;
                            }
                            BlockPos matchingPos = pos.add(deltaX, deltaY, deltaZ);
                            if (!canSpread(world, matchingPos)) {
                                continue;
                            }
                            spreadingPoses.add(matchingPos);
                        }
                    }
                }
                for (BlockPos spreadingPos : spreadingPoses) {
                    double squaredDistance = pos.getSquaredDistance(spreadingPos);
                    double probability = Math.exp(-squaredDistance * 0.3);
                    if (Random.create().nextInt(10000) <= 10000 * probability) {
                        world.setBlockState(spreadingPos, this.getDefaultState(), Block.NOTIFY_ALL);
                    }
                }
            } else {
                for(PlayerEntity receiver : world.getPlayers()) {
                    world.playSound(receiver, pos, SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.BLOCKS);
                }
                world.addParticle(
                        ParticleTypes.HAPPY_VILLAGER,
                        pos.getX() + 0.5,
                        pos.getY() + 1.5,
                        pos.getZ() + 0.5,
                        0,
                        0,
                        0
                );
                double radius = 3.5;
                Random rand = world.getRandom();
                for(int i = 0; i < 15; i++) {
                    world.addParticle(
                            ParticleTypes.HAPPY_VILLAGER,
                            pos.getX() + 0.5 + (rand.nextDouble() - 0.5) * 2 * radius,
                            pos.getY() + 1.1 + rand.nextDouble() * 0.8,
                            pos.getZ() + 0.5 + (rand.nextDouble() - 0.5) * 2 * radius,
                            rand.nextGaussian() * 0.02,
                            rand.nextGaussian() * 0.02,
                            rand.nextGaussian() * 0.02
                    );
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

//        if(!world.isClient()) {
//            BlockState upperState = world.getBlockState(pos.up());
//            if(!upperState.isAir() && !upperState.isTransparent(world, pos.up())) {
//                state.randomTick((ServerWorld) world, pos, world.getRandom());
//            }
//        }
//        world.scheduleBlockTick(pos, this, 0);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
//        if(!world.isClient()) {
//            if (direction == Direction.UP) {
//                if (!neighborState.isAir() && !neighborState.isTransparent(world, neighborPos)) {
//                    state.randomTick((ServerWorld) world, pos, world.getRandom());
//                }
//            }
//        }

        world.scheduleBlockTick(pos, this, this.getFallDelay(), TickPriority.HIGH);
        return state;
//        if(direction == Direction.UP) {
//            if(!neighborState.isAir() && !neighborState.isTransparent(world, neighborPos)) {
//
//                this.degenerateTime = (30 + (world.getRandom().nextInt(61))) * 20;
////                world.scheduleBlockTick(pos, this, (30 + Random.create().nextInt(60)) * 20);
//            }
//        } else if(direction == Direction.DOWN) {
////            super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
//        }
////        world.scheduleBlockTick(pos, this, (30 + Random.create().nextInt(60)) * 20);
//        world.scheduleBlockTick(pos, this, 0);
//        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    private boolean canSpread(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        BlockState upperState = world.getBlockState(pos.up());
        if(state.isOf(this.soilBlock)) {
            if(upperState.isAir() || upperState.isTransparent(world, pos.up())) {
                return true;
            }
        }
        return false;
    }
}
