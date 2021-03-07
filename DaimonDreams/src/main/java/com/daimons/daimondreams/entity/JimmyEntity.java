package com.daimons.daimondreams.entity;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class JimmyEntity extends BtsEntity {
    private final static TrackedData<Boolean> SHOOTING;
    private int fireballStrength = 1;

    public JimmyEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }
    LiteralText messageHurt = new LiteralText("<Jimmy> Beolag Obama.");
    LiteralText messageDeath = new LiteralText("<Jimmy> 내 페니스를 먹어.");
    LiteralText messageAmbient = new LiteralText("<Jimmy> Ohhh, jag-eun beullog maen-eun mos saeng-gyeoss-eo.");
    PlayerEntity source;

    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.goalSelector.add(7, new JimmyEntity.ShootFireballGoal(this));
        this.initCustomGoals();
    }

    protected void initCustomGoals() {
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1, false));
        this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
    }

    public boolean isShooting() {
        return (Boolean)this.dataTracker.get(SHOOTING);
    }

    public void setShooting(boolean shooting) {
        this.dataTracker.set(SHOOTING, shooting);
    }

    public int getFireballStrength() {
        return this.fireballStrength;
    }

    protected boolean isDisallowedInPeaceful() {
        return true;
    }

    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (source.getSource() instanceof FireballEntity && source.getAttacker() instanceof PlayerEntity) {
            super.damage(source, 1000.0F);
            return true;
        } else {
            return super.damage(source, amount);
        }
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SHOOTING, false);
    }

    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_GHAST_AMBIENT;
    }

    protected float getSoundVolume() {
        return 5.0F;
    }

    public static boolean canSpawn(EntityType<JimmyEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && random.nextInt(20) == 0 && canMobSpawn(type, world, spawnReason, pos, random);
    }

    public int getLimitPerChunk() {
        return 1;
    }

    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("ExplosionPower", this.fireballStrength);
    }

    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.contains("ExplosionPower", 99)) {
            this.fireballStrength = tag.getInt("ExplosionPower");
        }

    }

    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 2.6F;
    }

    static {
        SHOOTING = DataTracker.registerData(JimmyEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }

    static class ShootFireballGoal extends Goal {
        private final JimmyEntity ghast;
        public int cooldown;

        public ShootFireballGoal(JimmyEntity ghast) {
            this.ghast = ghast;
        }

        public boolean canStart() {
            return this.ghast.getTarget() != null;
        }

        public void start() {
            this.cooldown = 0;
        }

        public void stop() {
            this.ghast.setShooting(false);
        }

        public void tick() {
            LivingEntity livingEntity = this.ghast.getTarget();
            double d = 64.0D;
            if (livingEntity.squaredDistanceTo(this.ghast) < 4096.0D && this.ghast.canSee(livingEntity)) {
                World world = this.ghast.world;
                ++this.cooldown;
                if (this.cooldown == 10 && !this.ghast.isSilent()) {
                    world.syncWorldEvent((PlayerEntity)null, 1015, this.ghast.getBlockPos(), 0);
                }

                if (this.cooldown == 20) {
                    double e = 4.0D;
                    Vec3d vec3d = this.ghast.getRotationVec(1.0F);
                    double f = livingEntity.getX() - (this.ghast.getX() + vec3d.x * 4.0D);
                    double g = livingEntity.getBodyY(0.5D) - (0.5D + this.ghast.getBodyY(0.5D));
                    double h = livingEntity.getZ() - (this.ghast.getZ() + vec3d.z * 4.0D);
                    if (!this.ghast.isSilent()) {
                        world.syncWorldEvent((PlayerEntity)null, 1016, this.ghast.getBlockPos(), 0);
                    }

                    FireballEntity fireballEntity = new FireballEntity(world, this.ghast, f, g, h);
                    fireballEntity.explosionPower = this.ghast.getFireballStrength();
                    fireballEntity.updatePosition(this.ghast.getX() + vec3d.x * 4.0D, this.ghast.getBodyY(0.5D) + 0.5D, fireballEntity.getZ() + vec3d.z * 4.0D);
                    world.spawnEntity(fireballEntity);
                    this.cooldown = -40;
                }
            } else if (this.cooldown > 0) {
                --this.cooldown;
            }

            this.ghast.setShooting(this.cooldown > 10);
        }
    }

    protected void mobTick() {
        super.mobTick();
        int i = 1;
        if ((this.age + this.getEntityId()) % 1200 == 0) {
            StatusEffect statusEffect = StatusEffects.NAUSEA;
            List<ServerPlayerEntity> list = ((ServerWorld)this.world).getPlayers((serverPlayerEntityx) -> {
                return this.squaredDistanceTo(serverPlayerEntityx) < 2500.0D && serverPlayerEntityx.interactionManager.isSurvivalLike();
            });
            int j = 1;
            int k = 1;
            int l = 1;
            Iterator var7 = list.iterator();

            label33:
            while(true) {
                ServerPlayerEntity serverPlayerEntity;
                do {
                    if (!var7.hasNext()) {
                        break label33;
                    }

                    serverPlayerEntity = (ServerPlayerEntity)var7.next();
                    //wtf is the 0 its comparing idk, it was a 2, but aaaa
                } while(serverPlayerEntity.hasStatusEffect(statusEffect) && serverPlayerEntity.getStatusEffect(statusEffect).getAmplifier() >= 0 && serverPlayerEntity.getStatusEffect(statusEffect).getDuration() >= 300);

                serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.ELDER_GUARDIAN_EFFECT, this.isSilent() ? 0.0F : 1.0F));
                serverPlayerEntity.addStatusEffect(new StatusEffectInstance(statusEffect, 300, 0));
            }
        }

        if (!this.hasPositionTarget()) {
            this.setPositionTarget(this.getBlockPos(), 16);
        }

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