package com.daimons.daimondreams.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

public class RpmEntity extends BtsEntity{
    PlayerEntity source;
    public RpmEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    LiteralText messageHurt = new LiteralText("<RPM> Throw away the fear.");
    LiteralText messageDeath = new LiteralText("<RPM> Can’t hold me down 'cause you know I’m a fighter.");
    LiteralText messageAmbient = new LiteralText("<RPM> Getting bored, of Walls 2...");

    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.initCustomGoals();
    }

    protected void initCustomGoals() {
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1, false));
        this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));

    }

    protected void mobTick() {
        super.mobTick();
        source = world.getClosestPlayer(this, 128);
        if( source!= null){
            if(getDistanceToPlayer(source, this) > 50){
                this.teleportToPlayer();
                System.out.println("TELEPORT POG?");
            }
        }

        int i = 1;
        if ((this.age + this.getEntityId()) % 600 == 0) {
            StatusEffect statusEffect = StatusEffects.MINING_FATIGUE;
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
                } while(serverPlayerEntity.hasStatusEffect(statusEffect) && serverPlayerEntity.getStatusEffect(statusEffect).getAmplifier() >= 0 && serverPlayerEntity.getStatusEffect(statusEffect).getDuration() >= 1200);
                serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.ELDER_GUARDIAN_EFFECT, this.isSilent() ? 0.0F : 1.0F));
                serverPlayerEntity.addStatusEffect(new StatusEffectInstance(statusEffect, 600, 0));
                this.applyStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 200,0));
                this.applyStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 600,0));
            }
        }

        if (!this.hasPositionTarget()) {
            this.setPositionTarget(this.getBlockPos(), 16);
        }

    }

    public SoundEvent getHurtSound(DamageSource source) {
        if(source.getSource() instanceof Entity) {
            double x = Math.random();
            if(x < 0.25) {
                world.getServer().getPlayerManager().broadcastChatMessage(messageHurt, MessageType.SYSTEM, source.getSource().getUuid());
            }else if (x < 0.5){
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

    public static double getDistanceToPlayer(PlayerEntity entity, Entity pos) {
        double deltaX = entity.getX() - pos.getX();
        double deltaY = entity.getY() - pos.getY();
        double deltaZ = entity.getZ() - pos.getZ();

        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ));
    }

    protected boolean teleportRandomly() {
        if (!this.world.isClient() && this.isAlive()) {
            double d = this.getX() + (this.random.nextDouble() - 0.5D) * 64.0D;
            double e = this.getY() + (double)(this.random.nextInt(64) - 32);
            double f = this.getZ() + (this.random.nextDouble() - 0.5D) * 64.0D;
            return this.teleportTo(d, e, f);
        } else {
            return false;
        }
    }

    protected boolean teleportToPlayer() {
        source = world.getClosestPlayer(this, 128);
        if (!this.world.isClient() && this.isAlive()) {
            double d = source.getX() + (this.random.nextDouble() - 0.5D) * 34.0D;
            double e = source.getY() + (double)(this.random.nextInt(64) - 32);
            double f = source.getZ() + (this.random.nextDouble() - 0.5D) * 34.0D;
            return this.teleportTo(d, e, f);
        } else {
            return false;
        }
    }

    private boolean teleportTo(double x, double y, double z) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, y, z);

        while(mutable.getY() > 0 && !this.world.getBlockState(mutable).getMaterial().blocksMovement()) {
            mutable.move(Direction.DOWN);
        }

        BlockState blockState = this.world.getBlockState(mutable);
        boolean bl = blockState.getMaterial().blocksMovement();
        boolean bl2 = blockState.getFluidState().isIn(FluidTags.WATER);
        if (bl && !bl2) {
            boolean bl3 = this.teleport(x, y, z, true);
            if (bl3 && !this.isSilent()) {
                this.world.playSound((PlayerEntity)null, this.prevX, this.prevY, this.prevZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
                this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }

            return bl3;
        } else {
            return false;
        }
    }

    public void checkDespawn() {
            this.despawnCounter = 0;
    }
}
