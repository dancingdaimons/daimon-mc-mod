package com.daimons.daimondreams.entity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.Durations;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class SigmaEntity extends BtsEntity implements Angerable {
    private static final UUID ATTACKING_SPEED_BOOST_ID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private static final EntityAttributeModifier ATTACKING_SPEED_BOOST;
    private static final TrackedData<Boolean> ANGRY;
    private static final TrackedData<Boolean> PROVOKED;
    private static final Predicate<LivingEntity> PLAYER_ENDERMITE_PREDICATE;
    private int lastAngrySoundAge = -2147483648;
    private int ageWhenTargetSet;
    private static final IntRange ANGER_TIME_RANGE;
    private int angerTime;
    private UUID targetUuid;
    LiteralText messageHurt = new LiteralText("<Sigma> get dunked kiddo.");
    LiteralText messageDeath = new LiteralText("<Sigma> sigma bawls.");
    LiteralText messageAmbient = new LiteralText("<Sigma> It's getting quite insane.");
    PlayerEntity source;

    public SigmaEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new SigmaEntity.ChasePlayerGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0D, 0.0F));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new SigmaEntity.TeleportTowardsPlayerGoal(this, this::shouldAngerAt));
        this.targetSelector.add(2, new RevengeGoal(this, new Class[0]));
        this.targetSelector.add(4, new UniversalAngerGoal(this, true));
    }

    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target);
        EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (target == null) {
            this.ageWhenTargetSet = 0;
            this.dataTracker.set(ANGRY, true);
            this.dataTracker.set(PROVOKED, true);
            entityAttributeInstance.removeModifier(ATTACKING_SPEED_BOOST);
        } else {
            this.ageWhenTargetSet = this.age;
            this.dataTracker.set(ANGRY, true);
            if (!entityAttributeInstance.hasModifier(ATTACKING_SPEED_BOOST)) {
                entityAttributeInstance.addTemporaryModifier(ATTACKING_SPEED_BOOST);
            }
        }

    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ANGRY, true);
        this.dataTracker.startTracking(PROVOKED, true);
    }

    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.choose(this.random));
    }

    public void setAngerTime(int ticks) {
        this.angerTime = ticks;
    }

    public int getAngerTime() {
        return this.angerTime;
    }

    public void setAngryAt(@Nullable UUID uuid) {
        this.targetUuid = uuid;
    }

    public UUID getAngryAt() {
        return this.targetUuid;
    }

    public void playAngrySound() {
        if (this.age >= this.lastAngrySoundAge + 400) {
            this.lastAngrySoundAge = this.age;
            if (!this.isSilent()) {
                this.world.playSound(this.getX(), this.getEyeY(), this.getZ(), SoundEvents.ENTITY_ENDERMAN_STARE, this.getSoundCategory(), 2.5F, 1.0F, false);
            }
        }

    }

    public void onTrackedDataSet(TrackedData<?> data) {
        if (ANGRY.equals(data) && this.isProvoked() && this.world.isClient) {
            this.playAngrySound();
        }

        super.onTrackedDataSet(data);
    }

    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        this.angerToTag(tag);
    }

    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        BlockState blockState = null;
        if (tag.contains("carriedBlockState", 10)) {
            blockState = NbtHelper.toBlockState(tag.getCompound("carriedBlockState"));
            if (blockState.isAir()) {
                blockState = null;
            }
        }
        this.angerFromTag((ServerWorld)this.world, tag);
    }

    private boolean isPlayerStaring(PlayerEntity player) {
        ItemStack itemStack = (ItemStack)player.inventory.armor.get(3);
        if (itemStack.getItem() == Blocks.CARVED_PUMPKIN.asItem()) {
            return false;
        } else {
            Vec3d vec3d = player.getRotationVec(1.0F).normalize();
            Vec3d vec3d2 = new Vec3d(this.getX() - player.getX(), this.getEyeY() - player.getEyeY(), this.getZ() - player.getZ());
            double d = vec3d2.length();
            vec3d2 = vec3d2.normalize();
            double e = vec3d.dotProduct(vec3d2);
            return e > 1.0D - 0.025D / d ? player.canSee(this) : false;
        }
    }

    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 2.55F;
    }

    public void tickMovement() {
        this.jumping = false;
        if (!this.world.isClient) {
            this.tickAngerLogic((ServerWorld)this.world, true);
        }

        super.tickMovement();
    }

    protected void mobTick() {
        if (this.world.isDay() && this.age >= this.ageWhenTargetSet + 600) {
            float f = this.getBrightnessAtEyes();
            if (f > 0.5F && this.world.isSkyVisible(this.getBlockPos()) && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
                this.setTarget((LivingEntity)null);
                this.teleportRandomly();
            }
        }


        super.mobTick();
        int i = 1;
        if ((this.age + this.getEntityId()) % 1200 == 0) {
            StatusEffect statusEffect = StatusEffects.HUNGER;
            List<ServerPlayerEntity> list = ((ServerWorld) this.world).getPlayers((serverPlayerEntityx) -> {
                return this.squaredDistanceTo(serverPlayerEntityx) < 2500.0D && serverPlayerEntityx.interactionManager.isSurvivalLike();
            });
            int j = 1;
            int k = 1;
            int l = 1;
            Iterator var7 = list.iterator();

            label33:
            while (true) {
                ServerPlayerEntity serverPlayerEntity;
                do {
                    if (!var7.hasNext()) {
                        break label33;
                    }

                    serverPlayerEntity = (ServerPlayerEntity) var7.next();
                    //wtf is the 0 its comparing idk, it was a 2, but aaaa
                } while (serverPlayerEntity.hasStatusEffect(statusEffect) && serverPlayerEntity.getStatusEffect(statusEffect).getAmplifier() >= 0 && serverPlayerEntity.getStatusEffect(statusEffect).getDuration() >= 1200);

                serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.ELDER_GUARDIAN_EFFECT, this.isSilent() ? 0.0F : 1.0F));
                serverPlayerEntity.addStatusEffect(new StatusEffectInstance(statusEffect, 1200, 0));
            }
        }
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

    private boolean teleportTo(Entity entity) {
        Vec3d vec3d = new Vec3d(this.getX() - entity.getX(), this.getBodyY(0.5D) - entity.getEyeY(), this.getZ() - entity.getZ());
        vec3d = vec3d.normalize();
        double d = 16.0D;
        double e = this.getX() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3d.x * 16.0D;
        double f = this.getY() + (double)(this.random.nextInt(16) - 8) - vec3d.y * 16.0D;
        double g = this.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3d.z * 16.0D;
        return this.teleportTo(e, f, g);
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

    protected SoundEvent getAmbientSound() {
        return this.isAngry() ? SoundEvents.ENTITY_ENDERMAN_SCREAM : SoundEvents.ENTITY_ENDERMAN_AMBIENT;
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



    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        super.dropEquipment(source, lootingMultiplier, allowDrops);
    }

    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (source instanceof ProjectileDamageSource) {
            for(int i = 0; i < 64; ++i) {
                if (this.teleportRandomly()) {
                    return true;
                }
            }

            return false;
        } else {
            boolean bl = super.damage(source, amount);
            if (!this.world.isClient() && !(source.getAttacker() instanceof LivingEntity) && this.random.nextInt(10) != 0) {
                this.teleportRandomly();
            }

            return bl;
        }
    }

    public boolean isAngry() {
        return (Boolean)this.dataTracker.get(ANGRY);
    }

    public boolean isProvoked() {
        return (Boolean)this.dataTracker.get(PROVOKED);
    }

    public void setProvoked() {
        this.dataTracker.set(PROVOKED, true);
    }

    static {
        ATTACKING_SPEED_BOOST = new EntityAttributeModifier(ATTACKING_SPEED_BOOST_ID, "Attacking speed boost", 0.15000000596046448D, EntityAttributeModifier.Operation.ADDITION);
        ANGRY = DataTracker.registerData(SigmaEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        PROVOKED = DataTracker.registerData(SigmaEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        PLAYER_ENDERMITE_PREDICATE = (livingEntity) -> {
            return livingEntity instanceof EndermiteEntity && ((EndermiteEntity)livingEntity).isPlayerSpawned();
        };
        ANGER_TIME_RANGE = Durations.betweenSeconds(20, 39);
    }

    static class ChasePlayerGoal extends Goal {
        private final SigmaEntity enderman;
        private LivingEntity target;

        public ChasePlayerGoal(SigmaEntity enderman) {
            this.enderman = enderman;
            this.setControls(EnumSet.of(Control.JUMP, Control.MOVE));
        }

        public boolean canStart() {
            this.target = this.enderman.getTarget();
            if (!(this.target instanceof PlayerEntity)) {
                return false;
            } else {
                double d = this.target.squaredDistanceTo(this.enderman);
                return d > 256.0D ? false : this.enderman.isPlayerStaring((PlayerEntity)this.target);
            }
        }

        public void start() {
            this.enderman.getNavigation().stop();
        }

        public void tick() {
            this.enderman.getLookControl().lookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
        }
    }

    static class TeleportTowardsPlayerGoal extends FollowTargetGoal<PlayerEntity> {
        private final SigmaEntity enderman;
        private PlayerEntity targetPlayer;
        private int lookAtPlayerWarmup;
        private int ticksSinceUnseenTeleport;
        private final TargetPredicate staringPlayerPredicate;
        private final TargetPredicate validTargetPredicate = (new TargetPredicate()).includeHidden();

        public TeleportTowardsPlayerGoal(SigmaEntity enderman, @Nullable Predicate<LivingEntity> predicate) {
            super(enderman, PlayerEntity.class, 10, false, false, predicate);
            this.enderman = enderman;
            this.staringPlayerPredicate = (new TargetPredicate()).setBaseMaxDistance(this.getFollowRange()).setPredicate((playerEntity) -> {
                return enderman.isPlayerStaring((PlayerEntity)playerEntity);
            });
        }

        public boolean canStart() {
            this.targetPlayer = this.enderman.world.getClosestPlayer(this.enderman, 150);
            return this.targetPlayer != null;
        }

        public void start() {
            this.lookAtPlayerWarmup = 1;
            this.ticksSinceUnseenTeleport = 0;
            this.enderman.setProvoked();
        }

        public void stop() {
            this.targetPlayer = null;
            super.stop();
        }

        public boolean shouldContinue() {
            if (this.targetPlayer != null) {
                if (!this.enderman.isPlayerStaring(this.targetPlayer)) {
                    return false;
                } else {
                    this.enderman.lookAtEntity(this.targetPlayer, 10.0F, 10.0F);
                    return true;
                }
            } else {
                return this.targetEntity != null && this.validTargetPredicate.test(this.enderman, this.targetEntity) ? true : super.shouldContinue();
            }
        }

        public void tick() {
            if (this.enderman.getTarget() == null) {
                super.setTargetEntity((LivingEntity)null);
            }

            if (this.targetPlayer != null) {
                if (--this.lookAtPlayerWarmup <= 0) {
                    this.targetEntity = this.targetPlayer;
                    this.targetPlayer = null;
                    super.start();
                }
            } else {
                if (this.targetEntity != null && !this.enderman.hasVehicle()) {
                    if (this.enderman.isPlayerStaring((PlayerEntity)this.targetEntity)) {
                        if (this.targetEntity.squaredDistanceTo(this.enderman) < 16.0D) {
                            this.enderman.teleportRandomly();
                        }

                        this.ticksSinceUnseenTeleport = 0;
                    } else if (this.targetEntity.squaredDistanceTo(this.enderman) > 256.0D && this.ticksSinceUnseenTeleport++ >= 30 && this.enderman.teleportTo(this.targetEntity)) {
                        this.ticksSinceUnseenTeleport = 0;
                    }
                }
            }

        }
    }
}
