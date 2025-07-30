package com.climinby.starsky_explority.mixin;

import com.climinby.starsky_explority.registry.SSERegistries;
import com.climinby.starsky_explority.registry.stargate.Stargate;
import com.climinby.starsky_explority.registry.stargate.Stargates;
import com.climinby.starsky_explority.world.dimension.SSEDimensions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(ItemEntity.class)
public abstract class StargateActivatingDetector extends Entity {
    public StargateActivatingDetector(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (!this.getWorld().isClient()) {
            World world = this.getWorld();
            RegistryKey<World> registryKey = world.getRegistryKey();
            if (registryKey != SSEDimensions.THE_MOON_LEVEL_KEY && registryKey != World.OVERWORLD) {
                return;
            }
            ItemEntity self = (ItemEntity) (Object) this;
            BlockPos itemPos = self.getBlockPos();
            Item item = self.getStack().getItem();
            stargate: for (Stargate stargate : SSERegistries.STARGATE) {
                if (stargate.getActivationItem() == item) {
                    portal: for (Vec3i oriPos : stargate.getPortalPos()) {
                        for (Map.Entry<Vec3i, Block> entry : stargate.getGateStructure().entrySet()) {
                            BlockPos pos = itemPos.subtract(oriPos).add(entry.getKey());
                            if (world.getBlockState(pos).getBlock() != entry.getValue()) {
                                continue portal;
                            }
                        }

                        for (Vec3i vec : stargate.getPortalPos()) {
                            BlockPos portalPos = itemPos.subtract(oriPos).add(vec);
                            if (!world.getBlockState(portalPos).isAir()) {
                                continue portal;
                            }
                        }

                        activateEffect(world, itemPos.subtract(oriPos), stargate);

                        activateStargate(
                                world,
                                stargate.getPortalPos().stream()
                                        .map(vec -> itemPos.subtract(oriPos).add(vec))
                                        .toList(),
                                stargate.getPortalBlock().getDefaultState()
                                        .with(Properties.HORIZONTAL_AXIS, Direction.Axis.X)
                        );
                        self.kill();
                        break stargate;
                    }

                    portal: for (Vec3i oriPos : stargate.get90RotatedPortalPos()) {
                        for (Map.Entry<Vec3i, Block> entry : stargate.getGateStructure90Rotated().entrySet()) {
                            BlockPos pos = itemPos.subtract(oriPos).add(entry.getKey());
                            if (world.getBlockState(pos).getBlock() != entry.getValue()) {
                                continue portal;
                            }
                        }

                        for (Vec3i vec : stargate.get90RotatedPortalPos()) {
                            BlockPos portalPos = itemPos.subtract(oriPos).add(vec);
                            if (!world.getBlockState(portalPos).isAir()) {
                                continue portal;
                            }
                        }

                        activateEffect(world, itemPos.subtract(oriPos), stargate);

                        activateStargate(
                                world,
                                stargate.get90RotatedPortalPos().stream()
                                        .map(vec -> itemPos.subtract(oriPos).add(vec))
                                        .toList(),
                                stargate.getPortalBlock().getDefaultState()
                                        .with(Properties.HORIZONTAL_AXIS, Direction.Axis.Z)
                        );
                        self.kill();
                        break stargate;
                    }
                }
            }
        }
    }

    @Unique
    private void activateEffect(World world, BlockPos pos, Stargate stargate) {
        if (world.isClient()) {
            return;
        }

        if (stargate == Stargates.MOON_STARGATE) {
            Box box = new Box(
                    pos.getX() - 12, pos.getY() - 3, pos.getZ() - 12,
                    pos.getX() + 12, pos.getY() + 12, pos.getZ() + 12
            );
            List<Entity> entities = world.getEntitiesByClass(Entity.class, box, entity -> true);
            Vec3d forceSource = pos.toCenterPos().subtract(0, 3, 0);
            double velocity = 1.5;
            for (Entity entity : entities) {
                Vec3d vec3d = entity.getPos().subtract(forceSource).normalize().multiply(velocity);
                entity.addVelocity(vec3d);
                entity.velocityDirty = true;
                entity.velocityModified = true;
            }
        }
    }

    @Unique
    private void activateStargate(World world, List<BlockPos> portalPos, BlockState state) {
        for (BlockPos pos : portalPos) {
            world.setBlockState(pos, state, Block.NOTIFY_ALL);
        }
    }
}
