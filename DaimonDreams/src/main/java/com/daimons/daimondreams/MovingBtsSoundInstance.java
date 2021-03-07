package com.daimons.daimondreams;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class MovingBtsSoundInstance extends MovingSoundInstance {
    private final ZombieEntity btsEntity;
    private float distance = 0.0F;

    public MovingBtsSoundInstance(ZombieEntity btsEntity) {
        super(Daimondreams.BTSMUSIC_SE, SoundCategory.VOICE);
        this.btsEntity = btsEntity;
        this.repeat = true;
        this.repeatDelay = 0; //6050
        this.volume = 0.0F;
        this.x = (double)((float)btsEntity.getX());
        this.y = (double)((float)btsEntity.getY());
        this.z = (double)((float)btsEntity.getZ());
    }

    public boolean canPlay() {
        return !this.btsEntity.isSilent();
    }

    public boolean shouldAlwaysPlay() {
        return true;
    }

    @Override
    public void tick() {
        if (this.btsEntity.removed) {
            this.setDone();
        } else {
            this.x = (double)((float)this.btsEntity.getX());
            this.y = (double)((float)this.btsEntity.getY());
            this.z = (double)((float)this.btsEntity.getZ());
            float f = MathHelper.sqrt(Entity.squaredHorizontalLength(this.btsEntity.getVelocity()));
            if ((double)f >= 0.01D) {
                this.distance = MathHelper.clamp(this.distance + 0.0025F, 0.0F, 1.0F);
                this.volume = MathHelper.lerp(MathHelper.clamp(f, 0.0F, 0.5F), 0.0F, 0.7F);
            } else {
                this.distance = 0.0F;
                this.volume = 0.0F;
            }

        }
    }
}