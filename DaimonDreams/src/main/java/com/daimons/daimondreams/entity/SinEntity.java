package com.daimons.daimondreams.entity;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.MessageType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;


public class SinEntity extends BtsEntity {
    LiteralText messageHurt = new LiteralText("<Sin> I came to blow, blow, blow, blow up everything you've ever known known known known.");
    LiteralText messageDeath = new LiteralText("<Sin> Gineun geos? As namjaaaaaa.");
    LiteralText messageAmbient = new LiteralText("<Sin> I'm the volatilest sort.");
    PlayerEntity source;

    public SinEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.goalSelector.add(4, new SinEntity.SummonExplosionGoal());
        this.initCustomGoals();
    }

    protected void initCustomGoals() {
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1, false));
        this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));

    }

    class SummonExplosionGoal extends Goal {
        private final TargetPredicate closeVexPredicate;

        private SummonExplosionGoal() {
            this.closeVexPredicate = (new TargetPredicate()).setBaseMaxDistance(16.0D).includeHidden().ignoreDistanceScalingFactor().includeInvulnerable().includeTeammates();
        }

        public boolean canStart() {
            int i = SinEntity.this.world.getTargets(VexEntity.class, this.closeVexPredicate, SinEntity.this, SinEntity.this.getBoundingBox().expand(16.0D)).size();
            castSpell();
            return SinEntity.this.random.nextInt(8) + 1 > i;
        }
    }

    protected int getSpellTicks() {
        return 100;
    }

    protected int startTimeDelay() {
        return 340;
    }

    protected int cooldown = 0;
    private int explosionRadius = 4;

    Explosion.DestructionType destructionType = this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.NONE;

    protected void castSpell() {
        ServerWorld serverWorld = (ServerWorld) SinEntity.this.world;
        if (this.cooldown > 0) {
            --this.cooldown;
        } else {
            this.cooldown = 200;

            //spawns Explody boom boom BOOOOOOOM
            this.applyStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 25,3));
            this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), (float)this.explosionRadius, destructionType);
            System.out.println("boom");
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