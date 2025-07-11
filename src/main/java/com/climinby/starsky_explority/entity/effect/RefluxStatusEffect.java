package com.climinby.starsky_explority.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class RefluxStatusEffect extends StatusEffect {
    private static final double DEFAULT_BOUNCING_RADIUS = 2.5;
    private static final double AMPLIFIER_BENEFIT = 0.5;

    protected RefluxStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x50DDAC);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        super.applyUpdateEffect(entity, amplifier);
        double range = DEFAULT_BOUNCING_RADIUS + AMPLIFIER_BENEFIT * amplifier;
        List<ProjectileEntity> projectiles = entity.getEntityWorld()
                .getEntitiesByClass(
                        ProjectileEntity.class, entity.getBoundingBox().expand(range),
                        projectileEntity -> {
                            if(projectileEntity.getOwner() instanceof LivingEntity owner) {
                                if(owner.equals(entity)) {
                                    return false;
                                }
                            }
                            return true;
                        });
        projectileBounce(entity, projectiles);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        super.onApplied(entity, amplifier);
    }

    private void projectileBounce(LivingEntity entity, ProjectileEntity projectile) {
        double velocity = projectile.getVelocity().length();
        Vec3d pos = entity.getEyePos();
        Vec3d projPos = projectile.getPos();
        Vec3d unit = projPos.subtract(pos).normalize();
        Vec3d newVelocity = unit.multiply(velocity);
        projectile.setVelocity(newVelocity);
    }

    private void projectileBounce(LivingEntity entity, List<? extends ProjectileEntity> projectiles) {
        for(ProjectileEntity projectile : projectiles) {
            this.projectileBounce(entity, projectile);
        }
    }
}
