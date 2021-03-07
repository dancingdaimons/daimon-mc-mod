package com.daimons.daimondreams.model;

// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import com.daimons.daimondreams.entity.BtsEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;

import java.util.function.Function;

// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


public class BtsEntityModel<T extends BtsEntity> extends AbstractZombieModel<T> {
    public BtsEntityModel(float scale, boolean bl) {
        this(scale, 0.0F, 64, bl ? 32 : 64);
    }

    protected BtsEntityModel(float f, float g, int i, int j) {
        super(f, g, i, j);
    }

    public boolean isAttacking(T btsEntity) {
        return btsEntity.isAttacking();
    }
}
