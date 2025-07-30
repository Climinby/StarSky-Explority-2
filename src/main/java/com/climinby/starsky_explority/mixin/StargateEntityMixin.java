package com.climinby.starsky_explority.mixin;

import com.climinby.starsky_explority.block.StargatePortalBlock;
import com.climinby.starsky_explority.registry.stargate.Stargate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(Entity.class)
public abstract class StargateEntityMixin {
    @Shadow protected boolean inNetherPortal;

    @Shadow protected int netherPortalTime;

    @Shadow protected abstract void tickPortalCooldown();

    @Shadow public abstract void resetPortalCooldown();

    @Shadow public abstract @Nullable Entity moveToWorld(ServerWorld destination);

    @Shadow public abstract World getWorld();

    @Shadow public abstract boolean hasVehicle();

    @Shadow public abstract int getMaxNetherPortalTime();

    @Shadow public abstract BlockPos getBlockPos();

    @Shadow public abstract boolean teleport(ServerWorld world, double destX, double destY, double destZ, Set<PositionFlag> flags, float yaw, float pitch);

    @Shadow public abstract Vec3d getPos();

    @Shadow public Optional<BlockPos> supportingBlockPos;

    @Shadow public abstract boolean collidesWithStateAtPos(BlockPos pos, BlockState state);

    @Inject(method = "tickPortal", at = @At("HEAD"), cancellable = true)
    private void onTickStargatePortal(CallbackInfo ci) {
        if (this.getWorld() instanceof ServerWorld) {
            Block block = this.getWorld().getBlockState(this.getBlockPos()).getBlock();
            if (block instanceof NetherPortalBlock) {
                return;
            }
            BlockPos pos = this.getBlockPos();

            List<BlockPos> around = getAround(pos);
            boolean foundStargate = block instanceof StargatePortalBlock;
            if (this.inNetherPortal && !foundStargate) {
                for (BlockPos aroundPos : around) {
                    BlockState state = this.getWorld().getBlockState(aroundPos);
                    if (state.getBlock() instanceof StargatePortalBlock) {
                        block = state.getBlock();
                        pos = aroundPos;
                        foundStargate = true;
                        break;
                    }
                }
            }

            if (foundStargate) {
                StargatePortalBlock portalBlock = (StargatePortalBlock) block;
                int i = this.getMaxNetherPortalTime();
                ServerWorld serverWorld = (ServerWorld) this.getWorld();
                if (this.inNetherPortal) {
                    RegistryKey<World> worldKey =
                            this.getWorld().getRegistryKey() == World.OVERWORLD ? portalBlock.getTargetWorld() : World.OVERWORLD;
                    MinecraftServer minecraftServer = serverWorld.getServer();
                    ServerWorld serverWorld2 = minecraftServer.getWorld(worldKey);
                    if (serverWorld2 != null && !this.hasVehicle() && this.netherPortalTime++ >= i) {
                        this.getWorld().getProfiler().push("portal");
                        this.netherPortalTime = i;
                        this.resetPortalCooldown();

                        int portal = findPortal(serverWorld2, pos, portalBlock.getStargate().getPortalBlock());
                        int teleportY = getTopSafePos(serverWorld2, pos);
                        if (portal < serverWorld2.getBottomY()) {
                            BlockPos newPortalPos = new BlockPos(pos.getX(), teleportY + portalBlock.getStargate().getHeightToCenter() + 1, pos.getZ());
                            newPortalPos = newPortalPos.subtract(new Vec3i(0, 1, 0));

                            genNewPortal(serverWorld2, newPortalPos, portalBlock.getStargate());
                        }

                        Box box = portalBlock.getStargate().getBox();
                        BlockPos teleportPos;
                        if (box.getLengthX() > box.getLengthZ()) {
                            teleportPos = pos.withY(getTopSafePos(serverWorld2, pos.add(0, 0, 1))).add(0, 0, 1);
                        } else {
                            teleportPos = pos.withY(getTopSafePos(serverWorld2, pos.add(1, 0, 0))).add(1, 0, 0);
                        }
                        this.teleport(
                                serverWorld2,
                                teleportPos.getX() + 0.5,
                                teleportPos.getY() + 0.5,
                                teleportPos.getZ() + 0.5,
                                EnumSet.allOf(PositionFlag.class),
                                0,
                                0
                        );
                        this.getWorld().getProfiler().pop();
                    }
                    this.inNetherPortal = false;
                } else {
                    if (this.netherPortalTime > 0) {
                        this.netherPortalTime -= 4;
                    }
                    if (this.netherPortalTime < 0) {
                        this.netherPortalTime = 0;
                    }
                }
                this.tickPortalCooldown();

                ci.cancel();
            }
        }
    }

    @Unique
    private List<BlockPos> getAround(BlockPos pos) {
        return List.of(
                pos.add(1, 0, 0), pos.add(-1, 0, 0),
                pos.add(0, 0, 1), pos.add(0, 0, -1),
                pos.add(1, 0, 1), pos.add(-1, 0, -1),
                pos.add(1, 0, -1), pos.add(-1, 0, 1),
                pos.add(1, 1, 0), pos.add(-1, 1, 0),
                pos.add(0, 1, 1), pos.add(0, 1, -1),
                pos.add(1, 1, 1), pos.add(-1, 1, -1),
                pos.add(1, 1, -1), pos.add(-1, 1, 1)
        );
    }

    @Unique
    private int findPortal(World world, BlockPos pos, Block portal) {
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
                    state1.isOf(portal) ||
                            state2.isOf(portal) ||
                            state3.isOf(portal) ||
                            state4.isOf(portal) ||
                            state5.isOf(portal) ||
                            state6.isOf(portal) ||
                            state7.isOf(portal) ||
                            state8.isOf(portal) ||
                            state9.isOf(portal)
            ) {
                return height;
            }
        }
        return world.getBottomY() - 1;
    }

    @Unique
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

    @Unique
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
                world.setBlockState(portalBlockPos, stargate.getPortalBlock().getDefaultState().with(StargatePortalBlock.AXIS, Direction.Axis.X), Block.NOTIFY_LISTENERS);
            } else {
                world.setBlockState(portalBlockPos, stargate.getPortalBlock().getDefaultState().with(StargatePortalBlock.AXIS, Direction.Axis.Z), Block.NOTIFY_LISTENERS);
            }
        }
    }
}
