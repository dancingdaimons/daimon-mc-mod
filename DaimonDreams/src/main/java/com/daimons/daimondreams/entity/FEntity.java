package com.daimons.daimondreams.entity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.network.MessageType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

import java.util.EnumSet;


public class FEntity extends BtsEntity {

    private float eyeOffset = 0.5F;
    private int eyeOffsetCooldown;
    boolean isFire;
    LiteralText messageHurt = new LiteralText("<F> How you dare. How you dare. How you dare.");
    LiteralText messageDeath = new LiteralText("<F> Bring the pain.");
    LiteralText messageAmbient = new LiteralText("<F> Gotta go insane to stay sane.");
    PlayerEntity source;

    public FEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(4, new FEntity.ShootFireballGoal(this));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));

        this.initCustomGoals();
    }

    protected void initCustomGoals() {
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1, false));
        this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));

    }



    public boolean isOnFire() {
        return this.isFireActive();
    }

     private boolean isFireActive() {
        //return ((Byte)this.dataTracker.get(BLAZE_FLAGS) & 1) != 0;
         return isFire;
    }

    public boolean tryAttack(Entity target) {
        if (super.tryAttack(target)) {
            if (target instanceof LivingEntity) {
                int i = 0;
                if (this.world.getDifficulty() == Difficulty.NORMAL) {
                    i = 7;
                } else if (this.world.getDifficulty() == Difficulty.HARD) {
                    i = 15;
                }

                if (i > 0) {
                    ((LivingEntity)target).addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, i * 20, 0));
                }
            }

            return true;
        } else {
            return false;
        }
    }

    /* protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(BLAZE_FLAGS, (byte)0);
    } */

    static class ShootFireballGoal extends Goal {
        private final FEntity blaze;
        private int fireballsFired;
        private int fireballCooldown;
        private int targetNotVisibleTicks;

        public ShootFireballGoal(FEntity blaze) {
            this.blaze = blaze;
            System.out.println("ShootFireballGoal");
            //this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        public boolean canStart() {
            LivingEntity livingEntity = this.blaze.getTarget();
            return livingEntity != null && livingEntity.isAlive() && this.blaze.canTarget(livingEntity);
        }

        public void start() {
            this.fireballsFired = 0;
        }

        public void stop() {
            this.blaze.setFireActive(false);
            this.targetNotVisibleTicks = 0;
        }

        public void tick() {
            --this.fireballCooldown;
            LivingEntity livingEntity = this.blaze.getTarget();
            if (livingEntity != null) {
                boolean bl = this.blaze.getVisibilityCache().canSee(livingEntity);
                if (bl) {
                    this.targetNotVisibleTicks = 0;
                } else {
                    ++this.targetNotVisibleTicks;
                }

                double d = this.blaze.squaredDistanceTo(livingEntity);
                if (d < 4.0D) {
                    if (!bl) {
                        return;
                    }

                    if (this.fireballCooldown <= 0) {
                        this.fireballCooldown = 20;
                        this.blaze.tryAttack(livingEntity);
                    }

                    this.blaze.getMoveControl().moveTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 1.0D);
                } else if (d < this.getFollowRange() * this.getFollowRange() && bl) {
                    double e = livingEntity.getX() - this.blaze.getX();
                    double f = livingEntity.getBodyY(0.5D) - this.blaze.getBodyY(0.5D);
                    double g = livingEntity.getZ() - this.blaze.getZ();
                    if (this.fireballCooldown <= 0) {
                        ++this.fireballsFired;
                        if (this.fireballsFired == 1) {
                            this.fireballCooldown = 30;
                            this.blaze.setFireActive(true);
                        } else if (this.fireballsFired <= 4) {
                            this.fireballCooldown = 6;
                        } else {
                            this.fireballCooldown = 50;
                            this.fireballsFired = 0;
                            this.blaze.setFireActive(false);
                        }

                        if (this.fireballsFired > 1) {
                            float h = MathHelper.sqrt(MathHelper.sqrt(d)) * 0.5F;
                            if (!this.blaze.isSilent()) {
                                this.blaze.world.syncWorldEvent((PlayerEntity)null, 1018, this.blaze.getBlockPos(), 0);
                            }

                            for(int i = 0; i < 1; ++i) {
                                SmallFireballEntity smallFireballEntity = new SmallFireballEntity(this.blaze.world, this.blaze, e + this.blaze.getRandom().nextGaussian() * (double)h, f, g + this.blaze.getRandom().nextGaussian() * (double)h);
                                smallFireballEntity.updatePosition(smallFireballEntity.getX(), this.blaze.getBodyY(0.5D) + 0.5D, smallFireballEntity.getZ());
                                this.blaze.world.spawnEntity(smallFireballEntity);
                            }
                        }
                    }

                    this.blaze.getLookControl().lookAt(livingEntity, 10.0F, 10.0F);
                } else if (this.targetNotVisibleTicks < 5) {
                    this.blaze.getMoveControl().moveTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 1.0D);
                }

                super.tick();
            }
        }

        private double getFollowRange() {
            return this.blaze.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
        }
    }

    private void setFireActive(boolean fireActive) {
        isFire = fireActive;
       /* byte b = (Byte)this.dataTracker.get(BLAZE_FLAGS);
        if (fireActive) {
            b = (byte)(b | 1);
        } else {
            b &= -2;
        }

        this.dataTracker.set(BLAZE_FLAGS, b);
       */
    }
    public SoundEvent getHurtSound(DamageSource source) {
        if(source.getSource() instanceof Entity) {
            double x = Math.random();
            if(x < 0.3) {
                world.getServer().getPlayerManager().broadcastChatMessage(messageHurt, MessageType.SYSTEM, source.getSource().getUuid());
            }else if (x < 0.6){
                world.getServer().getPlayerManager().broadcastChatMessage(messageAmbient, MessageType.SYSTEM, source.getSource().getUuid());
            }else{
                //Nothing
            }
        }
        return SoundEvents.ENTITY_ZOMBIE_HURT;
    }

    public SoundEvent getDeathSound() {
        source = world.getClosestPlayer(this,130);
        world.getServer().getPlayerManager().broadcastChatMessage(messageDeath, MessageType.SYSTEM, source.getUuid());
        return SoundEvents.ENTITY_ZOMBIE_DEATH;
    }

}