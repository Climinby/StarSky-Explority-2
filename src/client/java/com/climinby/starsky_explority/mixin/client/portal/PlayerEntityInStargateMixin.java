package com.climinby.starsky_explority.mixin.client.portal;

import com.climinby.starsky_explority.block.StargatePortalBlock;
import com.climinby.starsky_explority.client.mixin_interface.portal.PlayerEntityInStargate;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ClientPlayerEntity.class)
public abstract class PlayerEntityInStargateMixin extends PlayerEntity implements PlayerEntityInStargate {
    @Unique
    private Block lastStargate = null;

    public PlayerEntityInStargateMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Override
    public void setLastStargate(Block stargate) {
        this.lastStargate = stargate;
    }

    @Override
    public Block getLastStargate() {
        return this.lastStargate;
    }

    @Inject(method = "updateNausea", at = @At("HEAD"))
    private void onUpdateNausea(CallbackInfo ci) {
        World world = this.getWorld();
        Block block = world.getBlockState(this.getBlockPos()).getBlock();

        if (block instanceof NetherPortalBlock) {
            setLastStargate(Blocks.NETHER_PORTAL);
            return;
        }

        if (block instanceof StargatePortalBlock) {
            setLastStargate(block);
        } else {
            List<BlockPos> around = getAround(this.getBlockPos());
            boolean found = false;
            for (BlockPos pos : around) {
                BlockState state = world.getBlockState(pos);
                if (state.getBlock() instanceof StargatePortalBlock) {
                    setLastStargate(state.getBlock());
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                setLastStargate(null);
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
}
