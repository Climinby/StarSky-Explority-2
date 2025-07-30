package com.climinby.starsky_explority.block;

import com.climinby.starsky_explority.registry.SSERegistries;
import com.climinby.starsky_explority.registry.stargate.Stargate;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.InstantStatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.tick.TickPriority;
import org.joml.Vector3f;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StargatePortalBlock extends Block {
    public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;
    protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
    protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);
    private final RegistryKey<World> targetWorld;
    private Stargate stargate = null;
    private final Map<UUID, Integer> playerCountdown = new HashMap<>();

    public StargatePortalBlock(Settings settings, RegistryKey<World> targetWorld) {
        super(settings);
        this.targetWorld = targetWorld;
        this.setDefaultState(this.getStateManager().getDefaultState().with(AXIS, Direction.Axis.X));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(AXIS)) {
            case Z -> Z_SHAPE;
            default -> X_SHAPE;
        };
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        Direction.Axis updateAxis = direction.getAxis();
        Direction.Axis thisAxis = state.get(AXIS);
        if ((thisAxis == updateAxis || updateAxis.isVertical()) && !neighborState.isOf(this)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (this.stargate == null) {
            initStargate();
        }

        if (entity.canUsePortals()) {
            if (entity.isPlayer() && !((PlayerEntity) entity).isCreative()) {
                entity.setInNetherPortal(pos);
                return;
            }

            MinecraftServer server = world.getServer();
            if (server != null) {
                World target;
                if (world.getRegistryKey() == World.OVERWORLD) {
                    target = server.getWorld(this.targetWorld);
                } else {
                    target = server.getWorld(World.OVERWORLD);
                }
                ServerWorld targetServer = (ServerWorld) target;
                if (target != null && !target.isClient()) {
                    int portal = findPortal(target, pos);
                    int teleportY = getTopSafePos(target, pos);
                    if (portal < target.getBottomY()) {
                        BlockPos newPortalPos = new BlockPos(pos.getX(), teleportY + stargate.getHeightToCenter() + 1, pos.getZ());
                        newPortalPos = newPortalPos.subtract(new Vec3i(0, 1, 0));

                        genNewPortal(target, newPortalPos, stargate);
                    }

                    Box box = stargate.getBox();
                    BlockPos teleportPos;
                    if (box.getLengthX() > box.getLengthZ()) {
                        teleportPos = pos.withY(getTopSafePos(target, pos.add(0, 0, 1))).add(0, 0, 1);
                    } else {
                        teleportPos = pos.withY(getTopSafePos(target, pos.add(1, 0, 0))).add(1, 0, 0);
                    }

                    entity.teleport(
                            targetServer,
                            teleportPos.getX() + 0.5,
                            teleportPos.getY() + 0.5,
                            teleportPos.getZ() + 0.5,
                            EnumSet.allOf(PositionFlag.class),
                            0,
                            0
                    );
                }
            }
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
//        if (world.isClient()) {
//            return;
//        }
//        if (!playerCountdown.isEmpty()) {
//            for (Map.Entry<UUID, Integer> entry : playerCountdown.entrySet()) {
//                UUID playerId = entry.getKey();
//                int countdown = entry.getValue();
//                if (countdown == 0) {
//                    playerCountdown.remove(playerId);
//                    PlayerEntity player = world.getPlayerByUuid(playerId);
//                    if (player != null) {
//                        player.removeStatusEffect(StatusEffects.NAUSEA);
//                        MinecraftServer server = world.getServer();
//                        ServerWorld target = world;
//                        if (world.getRegistryKey() == World.OVERWORLD) {
//                            target = server.getWorld(this.targetWorld);
//                        } else {
//                            target = server.getWorld(World.OVERWORLD);
//                        }
//                        ServerWorld targetServer = target;
//                        if (target != null && !target.isClient()) {
//                            if (this.stargate == null) {
//                                initStargate();
//                            }
//                            Box box = stargate.getBox();
//                            BlockPos teleportPos;
//                            if (box.getLengthX() > box.getLengthZ()) {
//                                teleportPos = pos.withY(getTopSafePos(target, pos.add(0, 0, 1))).add(0, 0, 1);
//                            } else {
//                                teleportPos = pos.withY(getTopSafePos(target, pos.add(1, 0, 0))).add(1, 0, 0);
//                            }
//                            player.teleport(
//                                    targetServer,
//                                    teleportPos.getX() + 0.5,
//                                    teleportPos.getY() + 0.5,
//                                    teleportPos.getZ() + 0.5,
//                                    EnumSet.allOf(PositionFlag.class),
//                                    0,
//                                    0
//                            );
//                        }
//                    }
//                } else {
//                    PlayerEntity player = world.getPlayerByUuid(playerId);
//                    if (player == null || !world.getBlockState(player.getBlockPos()).isOf(this)) {
//                        if (player != null) player.removeStatusEffect(StatusEffects.NAUSEA);
//                        playerCountdown.remove(playerId);
//                        continue;
//                    }
//                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 500, 4, false, false, false));
//                    playerCountdown.put(playerId, countdown - 1);
//                }
//            }
//            world.scheduleBlockTick(pos, this, 1, TickPriority.VERY_LOW);
//        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(100) == 0) {
            world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5f, random.nextFloat() * 0.4f + 0.8f, false);
        }
        for (int i = 0; i < 4; ++i) {
            double d = (double)pos.getX() + random.nextDouble();
            double e = (double)pos.getY() + random.nextDouble();
            double f = (double)pos.getZ() + random.nextDouble();
            double g = ((double)random.nextFloat() - 0.5) * 1.2;
            double h = ((double)random.nextFloat() - 0.5) * 1.2;
            double j = ((double)random.nextFloat() - 0.5) * 1.2;
            int k = random.nextInt(2) * 2 - 1;
            if (world.getBlockState(pos.west()).isOf(this) || world.getBlockState(pos.east()).isOf(this)) {
                f = (double)pos.getZ() + 0.5 + 0.25 * (double)k;
                j = random.nextFloat() * 4.8f * (float)k;
            } else {
                d = (double)pos.getX() + 0.5 + 0.25 * (double)k;
                g = random.nextFloat() * 4.8f * (float)k;
            }
            DustParticleEffect effect = new DustParticleEffect(new Vector3f(
                    (float) (63.0 / 255.0), (float) (145.0 / 255.0), (float) (246.0 / 255.0)), 0.5f);
            world.addParticle(effect, d, e, f, g, h, j);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    private void initStargate() {
        for (Stargate stargate : SSERegistries.STARGATE) {
            if (stargate.getPortalBlock() == this) {
                this.stargate = stargate;
                return;
            }
        }
    }

    private int findPortal(World world, BlockPos pos) {
        for (int height = world.getTopY(); height >= world.getBottomY(); height--) {
            BlockPos checkPos = new BlockPos(pos.getX(), height, pos.getZ());
            BlockState state1 = world.getBlockState(checkPos);
            BlockState state2 = world.getBlockState(checkPos.add(0, 0, 1));
            BlockState state3 = world.getBlockState(checkPos.add(1, 0, 0));
            BlockState state4 = world.getBlockState(checkPos.add(0, 0, -1));
            BlockState state5 = world.getBlockState(checkPos.add(-1, 0, 0));
            BlockState state6 = world.getBlockState(checkPos.add(1, 0, 1));
            BlockState state7 = world.getBlockState(checkPos.add(-1, 0, -1));
            BlockState state8 = world.getBlockState(checkPos.add(1, 0, -1));
            BlockState state9 = world.getBlockState(checkPos.add(-1, 0, 1));
            if (
                    state1.isOf(this) ||
                    state2.isOf(this) ||
                    state3.isOf(this) ||
                    state4.isOf(this) ||
                    state5.isOf(this) ||
                    state6.isOf(this) ||
                    state7.isOf(this) ||
                    state8.isOf(this) ||
                    state9.isOf(this)
            ) {
                return height;
            }
        }
        return world.getBottomY() - 1;
    }

    private int getTopSafePos(World world, BlockPos pos) {
        for (int height = world.getTopY(); height >= world.getBottomY(); height--) {
            BlockPos checkPos = new BlockPos(pos.getX(), height, pos.getZ());
            BlockState state = world.getBlockState(checkPos);
            if (!state.isAir()) {
                return height + 1;
            }
        }
        return world.getBottomY();
    }

    private void genNewPortal(World world, BlockPos pos, Stargate stargate) {
        Box box = stargate.getBox();
        if (box.getLengthX() > box.getLengthZ()) {
            box = box.expand(0, 0, 1);
        } else {
            box = box.expand(1, 0, 0);
        }
        for (int x = (int) box.minX; x <= box.maxX; x++) {
            for (int z = (int) box.minZ; z <= box.maxZ; z++) {
                for (int y = (int) box.minY; y <= box.maxY; y++) {
                    BlockPos airPos = pos.add(x, y, z);
                    BlockState state = world.getBlockState(airPos);
                    if (!state.isAir() && !state.isReplaceable()) {
                        world.setBlockState(airPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                    }
                }
            }
        }

        for (Map.Entry<Vec3i, Block> entry : stargate.getGateStructure().entrySet()) {
            BlockPos framePos = pos.add(entry.getKey());
            world.setBlockState(framePos, entry.getValue().getDefaultState(), Block.NOTIFY_LISTENERS);
        }

        for (Vec3i portalPos : stargate.getPortalPos()) {
            BlockPos portalBlockPos = pos.add(portalPos);
            if (stargate.getBox().getLengthX() > stargate.getBox().getLengthZ()) {
                world.setBlockState(portalBlockPos, stargate.getPortalBlock().getDefaultState().with(AXIS, Direction.Axis.X), Block.NOTIFY_LISTENERS);
            } else {
                world.setBlockState(portalBlockPos, stargate.getPortalBlock().getDefaultState().with(AXIS, Direction.Axis.Z), Block.NOTIFY_LISTENERS);
            }
        }
    }

    public RegistryKey<World> getTargetWorld() {
        return targetWorld;
    }

    public Stargate getStargate() {
        if (this.stargate == null) {
            initStargate();
        }
        return stargate;
    }
}
