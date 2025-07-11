package com.climinby.starsky_explority.block;

import com.climinby.starsky_explority.block.entity.ExtractorBlockEntity;
import com.climinby.starsky_explority.entity.SSEEntities;
import com.climinby.starsky_explority.util.SSENetworkingConstants;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExtractorBlock extends HorizontalFacingBlock implements BlockEntityProvider {
    public static final BooleanProperty EXTRACTING = BooleanProperty.of("extracting");
    public static final BooleanProperty WATER_CHARGED = BooleanProperty.of("water_charged");
    public static final BooleanProperty LAVA_CHARGED = BooleanProperty.of("lava_charged");

    private final Map<Direction, Block> connectedBlocks = new HashMap<>();

    protected ExtractorBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(EXTRACTING, false)
                .with(WATER_CHARGED, false)
                .with(LAVA_CHARGED, false)
                .with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return null;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

//        boolean isWaterCharged = false;
//        boolean isLavaCharged = false;
//        for(Direction dir : Arrays.stream(DIRECTIONS).toList()) {
//            Block neighborBlock = world.getBlockState(pos.add(dir.getVector())).getBlock();
//            connectedBlocks.put(dir, neighborBlock);
//            if(neighborBlock == Blocks.LAVA || neighborBlock == Blocks.LAVA_CAULDRON) {
//                isLavaCharged = true;
//            } else if(neighborBlock == Blocks.WATER || neighborBlock == Blocks.WATER_CAULDRON) {
//                isWaterCharged = true;
//            }
//        }
//        world.setBlockState(pos, state.with(WATER_CHARGED, isWaterCharged).with(LAVA_CHARGED, isLavaCharged), Block.NOTIFY_ALL);
        updateChargeState(world, pos);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(!world.isClient()) {
            NamedScreenHandlerFactory screen = state.createScreenHandlerFactory(world, pos);
            if(screen != null) {
                player.openHandledScreen(screen);
            }
            sendBlockChargedState((ServerWorld) world, pos);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if(state.hasBlockEntity() && !state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(blockEntity instanceof ExtractorBlockEntity) {
                ItemScatterer.spawn(world, pos, (Inventory)blockEntity);
                world.updateComparators(pos, this);
            }
        }
        if(newState.getBlock() == Blocks.AIR) {
            super.onStateReplaced(state, world, pos, newState, moved);
        } else {
            sendBlockChargedState((ServerWorld) world, pos);
        }
    }

    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof ExtractorBlockEntity) {
            ExtractorBlockEntity extractor = (ExtractorBlockEntity) blockEntity;
            return extractor;
        }
        return null;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        Set<Map.Entry<Direction, Block>> connectedBlocksSet = connectedBlocks.entrySet();

//        boolean isWaterCharged = false;
//        boolean isLavaCharged = false;
//        for(Map.Entry<Direction, Block> directionBlockEntry : connectedBlocksSet) {
//            Direction dir = directionBlockEntry.getKey();
//            Block currentBlock = world.getBlockState(pos.add(dir.getVector())).getBlock();
//            if(!directionBlockEntry.getValue().equals(currentBlock)) {
//                connectedBlocks.put(dir, currentBlock);
//            }
//            if(currentBlock == Blocks.LAVA || currentBlock == Blocks.LAVA_CAULDRON) {
//                isLavaCharged = true;
//            } else if(currentBlock == Blocks.WATER || currentBlock == Blocks.WATER_CAULDRON) {
//                isWaterCharged = true;
//            }
//        }
//        world.setBlockState(pos, state.with(WATER_CHARGED, isWaterCharged).with(LAVA_CHARGED, isLavaCharged), Block.NOTIFY_ALL);
        updateChargeState(world, pos);
        if(!world.isClient()) {
            sendBlockChargedState((ServerWorld) world, pos);
        }
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ExtractorBlockEntity(pos, state);
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(EXTRACTING).add(WATER_CHARGED).add(LAVA_CHARGED).add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    private static void sendBlockChargedState(ServerWorld world, BlockPos pos) {
        BlockState currentState = world.getBlockState(pos);
        boolean isWaterCharged = currentState.get(WATER_CHARGED);
        boolean isLavaCharged = currentState.get(LAVA_CHARGED);
        PacketByteBuf buf = PacketByteBufs.create().writeBlockPos(pos).writeBoolean(isWaterCharged).writeBoolean(isLavaCharged);
        for(ServerPlayerEntity player : PlayerLookup.tracking(world, pos)) {
            ServerPlayNetworking.send(player, SSENetworkingConstants.DATA_EXTRACTOR_CHARGED_STATE, buf);
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : checkType(type, SSEEntities.EXTRACTOR_BLOCK_ENTITY, ExtractorBlockEntity::tick);
    }

    private <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(
            BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    private void updateChargeState(World world, BlockPos pos) {
        boolean isWaterCharged = false;
        boolean isLavaCharged = false;
        for(Direction dir : Arrays.stream(DIRECTIONS).toList()) {
            Block neighborBlock = world.getBlockState(pos.add(dir.getVector())).getBlock();
            connectedBlocks.put(dir, neighborBlock);
            if(neighborBlock == Blocks.LAVA || neighborBlock == Blocks.LAVA_CAULDRON) {
                isLavaCharged = true;
            } else if(neighborBlock == Blocks.WATER || neighborBlock == Blocks.WATER_CAULDRON) {
                isWaterCharged = true;
            }
        }
        world.setBlockState(pos, world.getBlockState(pos).with(WATER_CHARGED, isWaterCharged).with(LAVA_CHARGED, isLavaCharged), Block.NOTIFY_ALL);
    }
}
