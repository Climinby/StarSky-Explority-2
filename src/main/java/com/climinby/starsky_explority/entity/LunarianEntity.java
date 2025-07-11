package com.climinby.starsky_explority.entity;

import com.climinby.starsky_explority.StarSkyExplority;
import com.climinby.starsky_explority.sound.SSESoundEvents;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;
import java.util.Random;

public class LunarianEntity extends PathAwareEntity {
    private int sightLockCoolDown = 100;
    private LivingEntity attacker = null;
    private boolean isFloating = false;
    private boolean isDiving = false;
    private boolean isBreathing = false;
    private int depth = 0;
    private BlockPos prePos = this.getBlockPos();
    private int maxSubmergeDepth = 42;
    private boolean canTeleport = false;
    private int teleportCoolDown = 80;
    private int attackCoolDown = 0;

    protected LunarianEntity(EntityType<? extends LunarianEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean canBeHitByProjectile() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if(this.attackCoolDown != 0) this.attackCoolDown--;
        if(this.attacker != null && this.sightLockCoolDown != 0) this.sightLockCoolDown--;
        else this.sightLockCoolDown = 100;
        this.getAir();

        if(this.attacker instanceof PlayerEntity playerTarget) {
            if(playerTarget.isCreative() || playerTarget.isSpectator()) {
                this.attacker = null;
                if(!this.getWorld().isClient()) {
                    this.setTarget(null);
                }
            }
        }
        if(this.attacker != null && !this.isInRange(this.attacker, 96.0)) {
            this.attacker = null;
            if(!this.getWorld().isClient()) {
                this.setTarget(null);
            }
        }
        if(this.attacker != null) {
            Item offHandItem = this.attacker.getOffHandStack().getItem();
            Item mainHandItem = this.attacker.getMainHandStack().getItem();
            this.canTeleport = offHandItem == Items.SHIELD || mainHandItem == Items.SHIELD;
            if (!this.getWorld().isClient() && this.getTarget() != this.attacker) {
                this.setTarget(this.attacker);
            }
        }

        if(!this.isDead() && this.attacker != null) {
            if(!this.getWorld().isClient()) {
                LivingEntity target = this.getTarget();
                if (this.attackCoolDown == 0) {
                    tryAttack(target);
                    this.attackCoolDown = 15;
                }
            }

            if(this.getHealth() > 10 && this.sightLockCoolDown == 0) {
                int random = new Random().nextInt(3);
                if (random < 2) {
                    this.sightLock(this.attacker);
                }
                this.sightLockCoolDown = 100;
            }
        }

        if(this.attacker != null && this.attacker.isDead()) {
            this.attacker = null;
            if(!this.getWorld().isClient()) {
                this.setTarget(null);
            }
        }

        //Projectile Reflecting
        List<ProjectileEntity> projectiles = this.getEntityWorld().getEntitiesByClass(ProjectileEntity.class, this.getBoundingBox().expand(2.5),
                projectileEntity -> true);
        this.projectileBounce(projectiles);

        //In-water Conduction
        boolean isInWater = false;
        if(this.isTouchingWater()) {
            isInWater = true;
        }
        if(isInWater) {
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 30, 2, false, false, false));
        } else {
            StatusEffectInstance dolphinsGrace = this.getStatusEffect(StatusEffects.DOLPHINS_GRACE);
            if(dolphinsGrace != null && !dolphinsGrace.isAmbient()) {
                this.removeStatusEffect(StatusEffects.DOLPHINS_GRACE);
            }
        }
        if(this.isTouchingWater() && this.getTarget() != null && this.getTarget().isTouchingWater()) {
            LivingEntity target = this.getTarget();
            if (!this.getBlockPos().equals(this.prePos)) {
                int depth = 0;
                BlockPos pos = this.getBlockPos().add(0, 1, 0);
                BlockState blockState = this.getWorld().getBlockState(pos);
                while (!(blockState.getBlock() instanceof AirBlock)) {
                    depth++;
                    pos = pos.add(0, 1, 0);
                    blockState = this.getWorld().getBlockState(pos);
                }
                this.depth = depth;
                this.maxSubmergeDepth = (int) ((double) this.getAir() * 0.3);
                if (depth >= maxSubmergeDepth - 1) {
                    this.setTarget(null);
                    isDiving = false;
                    isFloating = true;
                }
            }
            if (!this.isSubmergedInWater() && isFloating) {
                this.isBreathing = true;
                this.depth = 0;
                isFloating = false;
            }
            if(!this.isFloating) {
                if (!this.isBreathing && target.getPos().subtract(this.getPos()).getY() < 0.0) {
                    if (!(depth >= maxSubmergeDepth - 1)) {
                        this.isDiving = true;
                    }
                } else {
                    isDiving = false;
                }
            }
            this.updateWaterState();
        } else {
            this.isDiving = false;
            this.isFloating = false;
            this.updateWaterState();
        }
        if(this.isBreathing) this.isBreathing = !(this.getAir() == this.getMaxAir());
        if(this.getTarget() != null) {
            if(this.isTouchingWater()) {
                this.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, this.getTarget().getEyePos());
                if(this.isSubmergedInWater()) {
                    this.setSwimming(true);
                }
            }
        }

        //Teleport
        if(this.getHealth() > 15 && this.attacker != null) {
            if (this.teleportCoolDown == 0) {
                if (canTeleport && canTeleportToBack()) {
                    this.teleportation();
                    this.teleportCoolDown = 80;
                }
            } else {
                this.teleportCoolDown--;
            }
        }

        this.prePos = this.getBlockPos();
    }

    @Override
    public void handleStatus(byte status) {
        super.handleStatus(status);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if(super.damage(source, amount)) {
            Entity attackerEntity = source.getAttacker();
            if(attackerEntity != null) {
                if(attackerEntity instanceof PlayerEntity attackingPlayer) {
                    if(!attackingPlayer.isCreative() && !attackingPlayer.isSpectator()) {
                        this.attacker = attackingPlayer;
                    }
                } else if(attackerEntity instanceof LivingEntity attacker) {
                    this.attacker = attacker;
                }
            }
            return true;
        } else if(this.getWorld().isClient()) {
            Entity attackerEntity = source.getAttacker();
            if(attackerEntity != null) {
                if(attackerEntity instanceof PlayerEntity attackingPlayer) {
                    if(!attackingPlayer.isCreative() && !attackingPlayer.isSpectator()) {
                        this.attacker = attackingPlayer;
                    }
                } else if(attackerEntity instanceof LivingEntity attacker) {
                    this.attacker = attacker;
                }
            }
        }
        return false;
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.5, true));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 96.0F));

        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, 2, false, false, this::isPlayerAttacker));
    }

    private boolean isPlayerAttacker(LivingEntity entity) {
        return entity.equals(this.attacker);
    }

    @Override
    public boolean tryAttack(Entity target) {
        float damage = (float) this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (this.isInRange(target, 2.9)) target.damage(this.getDamageSources().mobAttack(this), damage);
        return true;
    }

    @Override
    protected Identifier getLootTableId() {
        return new Identifier(StarSkyExplority.MOD_ID, "entities/lunarian");
    }

    @Override
    protected void dropXp() {
        super.dropXp();
    }

    @Override
    public int getXpToDrop() {
        super.getXpToDrop();
        int rand = new Random().nextInt(10);
        if(rand < 1) return 4;
        else if(rand < 3) return 5;
        else if(rand < 7) return 6;
        else if(rand < 9) return 7;
        return 8;
    }

    private void sightLock(LivingEntity target) {
        target.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, this.getEyePos());
        World world = this.getWorld();
        if(world.isClient()) {
            for(PlayerEntity player : world.getPlayers()) {
                world.playSound(
                        player,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        SSESoundEvents.ENTITY_LUNARIAN_SIGHT_LOCK,
                        SoundCategory.HOSTILE,
                        2.0F,
                        1.0F
                );
            }
        }
    }

    private void projectileBounce(Entity projectile) {
        if(isProjectile(projectile)) {
            Vec3d prePos = new Vec3d(projectile.prevX, projectile.prevY, projectile.prevZ);
            Vec3d projPos = projectile.getPos();
            if(prePos.distanceTo(projPos) >= 0.0001) {
                double velocity = projectile.getVelocity().length();
                Vec3d pos = this.getEyePos();
                Vec3d unit = projPos.subtract(pos).normalize();
                Vec3d newVelocity = unit.multiply(velocity);
                World world = this.getWorld();
                if (world.isClient()) {
                    net.minecraft.util.math.random.Random random = world.getRandom();
                    int count = 10;
                    DustParticleEffect shieldParticle = new DustParticleEffect(
                            new Vector3f(139.0F / 255.0F, 229.0F / 255.0F, 246.0F / 255.0F),
                            1.0F
                    );
                    Vec3d u1 = new Vec3d(-unit.y, unit.x, 0.0).normalize();
                    Vec3d u2 = unit.crossProduct(u1).normalize();
                    for (int i = 0; i < count; i++) {
                        double t = random.nextDouble() * 2 * Math.PI;
                        Vec3d aVec = u1.multiply(Math.cos(t)).add(u2.multiply(Math.sin(t))).normalize().add(unit.multiply(random.nextDouble() * velocity * 0.3));
                        for(double d = 0.0; d <= velocity * 0.5; d += 0.15) {
                            world.addParticle(
                                    shieldParticle,
                                    projPos.x + aVec.x * d,
                                    projPos.y + aVec.y * d,
                                    projPos.z + aVec.z * d,
                                    (unit.x + random.nextGaussian() * 0.1) * velocity,
                                    (unit.y + random.nextGaussian() * 0.1) * velocity,
                                    (unit.z + random.nextGaussian() * 0.1) * velocity
                            );
                        }
                    }
                }
                projectile.setVelocity(newVelocity);
            }
        }
    }

    private void projectileBounce(List<? extends Entity> projectiles) {
        for(Entity projectile : projectiles) {
            this.projectileBounce(projectile);
        }
    }

    private boolean isProjectile(Entity entity) {
        return entity instanceof ProjectileEntity;
    }

    @Override
    protected boolean updateWaterState() {
        boolean b = super.updateWaterState();
        if(this.isTouchingWater()) {
            if (this.isDiving) {
                this.addVelocity(0.0, -0.04, 0.0);
            } else if (this.isFloating) {
                this.addVelocity(0.0, 0.01, 0.0);
            }
        }
        return b;
    }

    private boolean canTeleportToBack() {
        if(this.attacker != null) {
            Vec3d attackerPos = this.attacker.getPos();
            double rYaw = this.attacker.getYaw() * Math.PI / 180.0;
            Vec3d facingDir = new Vec3d(-Math.sin(rYaw), 0, Math.cos(rYaw));
            Vec3d backPos = attackerPos.subtract(facingDir);
            BlockPos backBlockPos = new BlockPos(
                    (int) Math.round(backPos.getX()),
                    (int) Math.round(backPos.getY()) + 1,
                    (int) Math.round(backPos.getZ())
            );
            BlockState backBlock = this.getWorld().getBlockState(backBlockPos);
            return !backBlock.shouldSuffocate(this.getWorld(), backBlockPos);
        }
        return false;
    }

    private void teleportation() {
        Vec3d attackerPos = this.attacker.getPos();
        double rYaw = this.attacker.getYaw() * Math.PI / 180;
        Vec3d facingDir = new Vec3d(-Math.sin(rYaw), 0, Math.cos(rYaw));
        Vec3d desPos = attackerPos.subtract(facingDir);
        World world = this.getWorld();
        if(world.isClient()) {
            Vec3d thisPos = this.getPos();
            Vec3d dVec = desPos.subtract(thisPos);
            double distance = dVec.length();
            int count = 4;
            net.minecraft.util.math.random.Random random = world.getRandom();
            for(double d = 0.0; d <= distance; d += 0.2) {
                double multiple = d / distance;
                Vec3d particleCenter = thisPos.add(dVec.multiply(multiple)).add(0, 1, 0);
                for(int i = 0; i < count; i++) {
                    world.addParticle(
                            ParticleTypes.GLOW,
                            particleCenter.x + (random.nextDouble() - 0.5) * 0.7,
                            particleCenter.y + (random.nextDouble() - 0.5),
                            particleCenter.z + (random.nextDouble() - 0.5) * 0.7,
                            0.0,
                            -Math.abs(random.nextGaussian()) * 0.05,
                            0.0
                    );
                }
            }
            for(PlayerEntity player : world.getPlayers()) {
                world.playSound(
                        player,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        SSESoundEvents.ENTITY_LUNARIAN_TELEPORT,
                        SoundCategory.HOSTILE,
                        2.0F,
                        1.0F
                );
            }
        }
        this.teleport(desPos.x, desPos.y, desPos.z);
    }
}
