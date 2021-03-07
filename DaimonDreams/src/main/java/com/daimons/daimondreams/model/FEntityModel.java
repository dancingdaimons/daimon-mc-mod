package com.daimons.daimondreams.model;

// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import com.daimons.daimondreams.entity.FEntity;

// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


public class FEntityModel extends BtsEntityModel<FEntity> {
    public FEntityModel(float scale, boolean bl) {
        this(scale, 0.0F, 64, bl ? 32 : 64);
    }

    protected FEntityModel(float f, float g, int i, int j) {
        super(f, g, i, j);
    }

    public boolean isAttacking(FEntity btsEntity) {
        return btsEntity.isAttacking();
    }
}
