package com.daimons.daimondreams.entity;

import com.daimons.daimondreams.Daimondreams;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

/*
 * Our Cube Entity extends PathAwareEntity, which extends MobEntity, which extends LivingEntity.
 *
 * LivingEntity has health and can deal damage.
 * MobEntity has movement controls and AI capabilities.
 * PathAwareEntity has pathfinding favor and slightly tweaked leash behavior.
 */
public class CubeEntity extends PathAwareEntity {
    PlayerEntity owner;
    private int ageWhenTargetSet;
    private int songCooldown;

    public CubeEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    protected void initGoals() {
        this.goalSelector.add(2, new CubeEntity.DestroyObsidianGoal(this, 1.0D, 3));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.initCustomGoals();
    }

    protected void initCustomGoals() {
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0D));
        this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
    }

    /* class playTerrorRadius extends Goal {
        World world;
        CubeEntity entity;
        boolean shouldPlay;
        public playTerrorRadius(CubeEntity entity) {
            System.out.println("play terror");
        }

        public boolean canStart() {
            if(CubeEntity.getDistanceToPlayer(world.getClosestPlayer(entity, 100), entity) >= 10 && songCooldown >= 0) {
                shouldPlay = true;
                System.out.println("PLAY BABY PLAY");
                playSound(this.getBtsMusic(), 1F, 1.0F);
                songCooldown = 6100;
                return true;
            }else{
                shouldPlay = false;
                --songCooldown;
                System.out.println("SILENCE");
                return false;
            }
        }

        protected SoundEvent getBtsMusic() {
            return Daimondreams.BTSMUSIC_SE;
        }
    } */

    class DestroyObsidianGoal extends StepAndDestroyBlockGoal {
        DestroyObsidianGoal(PathAwareEntity mob, double speed, int maxYDifference) {
            super(Blocks.OBSIDIAN, mob, speed, maxYDifference);
        }

        public void tickStepping(WorldAccess world, BlockPos pos) {
            world.playSound((PlayerEntity) null, pos, SoundEvents.ENTITY_BLAZE_HURT, SoundCategory.HOSTILE, 0.5F, 0.9F + CubeEntity.this.random.nextFloat() * 0.2F);
        }

        public void onDestroyBlock(World world, BlockPos pos) {
            world.playSound((PlayerEntity) null, pos, SoundEvents.ENTITY_BLAZE_HURT, SoundCategory.BLOCKS, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);
           // world.setBlockState(pos, Blocks.CHEST.getDefaultState());
            //this.addChest(structureWorldAccess, boundingBox, random, 3, 2, 3, LootTables.STRONGHOLD_CORRIDOR_CHEST);
            System.out.println("get fucked");
            System.out.println("distance is: " + CubeEntity.getDistanceToPlayer(world.getClosestPlayer(mob, 100), mob));

                   world.setBlockState(pos, Blocks.CHEST.getDefaultState());
                    BlockEntity blockEntity = world.getBlockEntity(pos);
                    if (blockEntity instanceof ChestBlockEntity) {
                        ((ChestBlockEntity) blockEntity).setLootTable(LootTables.STRONGHOLD_CORRIDOR_CHEST, random.nextLong());
                    }

        }

        public double getDesiredSquaredDistanceToTarget() {
            return 1.14D;
        }
    }

    public static double getDistanceToPlayer(PlayerEntity entity, Entity pos) {
        double deltaX = entity.getX() - pos.getX();
        double deltaY = entity.getY() - pos.getY();
        double deltaZ = entity.getZ() - pos.getZ();

        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ));
    }

    protected void mobTick() {
        if (this.age >= this.ageWhenTargetSet + 600) {
            if (this.world.isSkyVisible(this.getBlockPos()) && this.random.nextFloat() * 30.0F < (0.4F) * 2.0F) {
                this.setTarget((LivingEntity)null);
                this.teleportRandomly();
            }
        }

        super.mobTick();
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

}