package com.daimons.daimondreams.entity;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.MessageType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


public class JuhgkoopEntity extends BtsEntity {
    LiteralText messageHurt = new LiteralText("<juhg,koop> 방탄소년단.");
    LiteralText messageDeath = new LiteralText("<juhg,koop> :( (Translated).");
    LiteralText messageAmbient = new LiteralText("<juhg,koop> I used to rule the world.");
    PlayerEntity source;

    public JuhgkoopEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.goalSelector.add(4, new JuhgkoopEntity.SummonVexGoal());
        this.initCustomGoals();
    }

    protected void initCustomGoals() {
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1, false));
        this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));

    }
    class SummonVexGoal extends Goal {
        private final TargetPredicate closeVexPredicate;

        private SummonVexGoal() {
            this.closeVexPredicate = (new TargetPredicate()).setBaseMaxDistance(16.0D).includeHidden().ignoreDistanceScalingFactor().includeInvulnerable().includeTeammates();
        }

        public boolean canStart() {
            int i = JuhgkoopEntity.this.world.getTargets(VexEntity.class, this.closeVexPredicate, JuhgkoopEntity.this, JuhgkoopEntity.this.getBoundingBox().expand(16.0D)).size();
            castSpell();
            return JuhgkoopEntity.this.random.nextInt(8) + 1 > i;
        }
    }

    protected int getSpellTicks() {
        return 100;
    }

    protected int startTimeDelay() {
        return 340;
    }

    protected int cooldown = 0;

    protected void castSpell() {
        ServerWorld serverWorld = (ServerWorld) JuhgkoopEntity.this.world;
        if (this.cooldown > 0) {
            --this.cooldown;
        } else {
            this.playSound(getSoundPrepare(), 2, 1);
            this.cooldown = 1200;
            for (int i = 0; i < 3; ++i) {
                BlockPos blockPos = JuhgkoopEntity.this.getBlockPos().add(-2 + JuhgkoopEntity.this.random.nextInt(5), 1, -2 + JuhgkoopEntity.this.random.nextInt(5));
                VexEntity vexEntity = (VexEntity) EntityType.VEX.create(JuhgkoopEntity.this.world);
                vexEntity.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);
                vexEntity.initialize(serverWorld, JuhgkoopEntity.this.world.getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, (EntityData) null, (CompoundTag) null);
                vexEntity.setOwner(JuhgkoopEntity.this);
                vexEntity.setBounds(blockPos);
                vexEntity.setLifeTicks(20 * (30 + JuhgkoopEntity.this.random.nextInt(90)));
                serverWorld.spawnEntityAndPassengers(vexEntity);

            }
            //spawns phantoms
            BlockPos blockPos = JuhgkoopEntity.this.getBlockPos().add(-2 + JuhgkoopEntity.this.random.nextInt(5), 1, -2 + JuhgkoopEntity.this.random.nextInt(5));
            PhantomEntity phantomEntity = (PhantomEntity) EntityType.PHANTOM.create(JuhgkoopEntity.this.world);
            phantomEntity.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);
            phantomEntity.initialize(serverWorld, JuhgkoopEntity.this.world.getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, (EntityData) null, (CompoundTag) null);
            serverWorld.spawnEntityAndPassengers(phantomEntity);
            //spawn lightning yes yes GOD DAMN IT I NEED THE PLAYER'S COORDS AAAA
            System.out.println("Lightning??? Maybe?");

            this.playSound(getSoundThunder(), 2, 1);
            PlayerEntity player = this.world.getClosestPlayer(this, 100);
            LightningEntity lightningEntity = (LightningEntity)EntityType.LIGHTNING_BOLT.create(player.world);
            int posX = (int)player.getX();
            int posY = (int)player.getY();
            int posZ = (int)player.getZ();
            BlockPos playerPos = new BlockPos(posX, posY, posZ);
            lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(playerPos));
            player.world.spawnEntity(lightningEntity);
        }
    }

    protected SoundEvent getSoundPrepare() {
        return SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON;
    }

    protected SoundEvent getSoundThunder() {
        return SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT;
    }

    protected SpellcastingIllagerEntity.Spell getSpell() {
        return SpellcastingIllagerEntity.Spell.SUMMON_VEX;
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

