package com.climinby.starsky_explority.mixin;

import com.climinby.starsky_explority.registry.SSERegistries;
import com.climinby.starsky_explority.registry.gravity_changed_dimension.GravityChangedDimension;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class GravityChangedEntityMixin extends Entity implements Attackable {
    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

    @Shadow protected abstract boolean shouldSwimInFluids();

    @Shadow public abstract boolean canWalkOnFluid(FluidState state);

    @Shadow public abstract boolean isFallFlying();

    @Shadow public abstract boolean hasNoDrag();

    @Shadow public abstract @Nullable StatusEffectInstance getStatusEffect(StatusEffect effect);

    public GravityChangedEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "travel", at = @At("RETURN"))
    private void onTravel(Vec3d movementInput, CallbackInfo ci) {
        RegistryKey<World> registryKey = this.getWorld().getRegistryKey();
        double gravityMultiplier = 1.0;
        boolean isGravityChanged = false;
        for (GravityChangedDimension dim : SSERegistries.GRAVITY_CHANGED_DIMENSION) {
            if (dim.dimensionKey() == registryKey) {
                gravityMultiplier = dim.gravityMultiplier();
                isGravityChanged = true;
                break;
            }
        }

        if (isGravityChanged) {
            if (this.isLogicalSideForUpdatingMovement()) {
                boolean bl;
                double d = 0.08;
                bl = this.getVelocity().y <= 0.0;
                if (bl && this.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
                    d = 0.01;
                }
                FluidState fluidState = this.getWorld().getFluidState(this.getBlockPos());
                if (
                        !(this.isTouchingWater() && this.shouldSwimInFluids() && !this.canWalkOnFluid(fluidState)) &&
                        !(this.isInLava() && this.shouldSwimInFluids() && !this.canWalkOnFluid(fluidState)) &&
                        !(this.isFallFlying())
                ) {
                    BlockPos blockPos = this.getVelocityAffectingPos();
                    if (!this.getWorld().isClient || this.getWorld().isChunkLoaded(blockPos)) {
                        if (!this.hasNoGravity()) {
                            if (this.hasNoDrag()) {
                                this.addVelocity(0, (1 - gravityMultiplier) * d, 0);
                            } else {
                                this.addVelocity(0, (1 - gravityMultiplier) * d * (double)0.98f, 0);
                            }
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "computeFallDamage", at = @At("RETURN"), cancellable = true)
    private void onComputeFallDamage(float fallDistance, float damageMultiplier, CallbackInfoReturnable<Integer> cir) {
        RegistryKey<World> registryKey = this.getWorld().getRegistryKey();
        double gravityMultiplier = 1.0;
        boolean isGravityChanged = false;
        for (GravityChangedDimension dim : SSERegistries.GRAVITY_CHANGED_DIMENSION) {
            if (dim.dimensionKey() == registryKey) {
                gravityMultiplier = dim.gravityMultiplier();
                isGravityChanged = true;
                break;
            }
        }

        if (isGravityChanged) {
            if (!this.getType().isIn(EntityTypeTags.FALL_DAMAGE_IMMUNE)) {
                StatusEffectInstance statusEffectInstance = this.getStatusEffect(StatusEffects.JUMP_BOOST);
                float f = statusEffectInstance == null ? 0.0f : (float)(statusEffectInstance.getAmplifier() + 1);
                int damage = MathHelper.ceil((fallDistance - (3.0 / gravityMultiplier) - f) * damageMultiplier);
                cir.setReturnValue(damage);
            }
        }
    }
}
