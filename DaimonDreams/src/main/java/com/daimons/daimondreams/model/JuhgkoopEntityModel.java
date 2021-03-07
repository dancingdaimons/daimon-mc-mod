package com.daimons.daimondreams.model;

// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import com.daimons.daimondreams.entity.JuhgkoopEntity;
import com.daimons.daimondreams.entity.RpmEntity;

// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


public class JuhgkoopEntityModel extends BtsEntityModel<JuhgkoopEntity> {
    public JuhgkoopEntityModel(float scale, boolean bl) {
        this(scale, 0.0F, 64, bl ? 32 : 64);
    }

    protected JuhgkoopEntityModel(float f, float g, int i, int j) {
        super(f, g, i, j);
    }

    public boolean isAttacking(JuhgkoopEntity btsEntity) {
        return btsEntity.isAttacking();
    }
}
