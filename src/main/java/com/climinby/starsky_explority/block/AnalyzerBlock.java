package com.climinby.starsky_explority.block;

import com.climinby.starsky_explority.block.entity.AnalyzerBlockEntity;
import com.climinby.starsky_explority.entity.SSEEntities;
import com.climinby.starsky_explority.nbt.tag.SSETags;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AnalyzerBlock extends HorizontalFacingBlock implements BlockEntityProvider {
    public static final BooleanProperty WORKING = BooleanProperty.of("working");
    public static final BooleanProperty AUTO_WORKING = BooleanProperty.of("auto_working");

    public AnalyzerBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(WORKING, false)
                .with(AUTO_WORKING, false)
                .with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return null;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        boolean hasRedstonePower = world.isReceivingRedstonePower(pos);
        world.setBlockState(pos, world.getBlockState(pos).with(AUTO_WORKING, hasRedstonePower), Block.NOTIFY_ALL);
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        if(!world.isClient()) {
            world.getServer().execute(() -> {
                List<ItemEntity> droppedItems = world.getEntitiesByClass(
                        ItemEntity.class,
                        new Box(pos),
                        item -> item.getStack().hasNbt() && item.getStack().getNbt().contains(SSETags.ANALYZER_IS_PREVIEW_KEY)
                );
                for(ItemEntity discardable : droppedItems) {
                    discardable.discard();
                }
            });
        }
        super.onBroken(world, pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(!world.isClient()) {
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
            if(screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof AnalyzerBlockEntity) {
            return (AnalyzerBlockEntity) blockEntity;
        }
        return null;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if(state.hasBlockEntity() && !state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(blockEntity instanceof AnalyzerBlockEntity) {
                ItemScatterer.spawn(world, pos, (Inventory)blockEntity);
                world.updateComparators(pos, this);
            }
        }
        if(newState.getBlock() == Blocks.AIR) {
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : checkType(type, SSEEntities.ANALYZER_BLOCK_ENTITY, AnalyzerBlockEntity::tick);
    }

    private <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(
            BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AnalyzerBlockEntity(pos, state);
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WORKING).add(AUTO_WORKING).add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
}
