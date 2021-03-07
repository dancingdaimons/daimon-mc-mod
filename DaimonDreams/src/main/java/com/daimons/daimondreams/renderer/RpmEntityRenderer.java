package com.daimons.daimondreams.renderer;

import com.daimons.daimondreams.entity.BtsEntity;
import com.daimons.daimondreams.entity.RpmEntity;
import com.daimons.daimondreams.model.BtsEntityModel;
import com.daimons.daimondreams.model.RpmEntityModel;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.util.Identifier;


public class RpmEntityRenderer extends BtsEntityRenderer<RpmEntity, RpmEntityModel> {
    private static final Identifier TEXTURE = new Identifier("entitytesting", "textures/entity/rpm/rpm.png");


    public RpmEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new RpmEntityModel(0.0F, false), new RpmEntityModel(0.5F, true), new RpmEntityModel(1.0F, true));
    }

    @Override
    public Identifier getTexture(RpmEntity entity) {
        return TEXTURE;
    }
}