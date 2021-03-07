package com.daimons.daimondreams.renderer;

import com.daimons.daimondreams.entity.BtsEntity;
import com.daimons.daimondreams.entity.FEntity;
import com.daimons.daimondreams.entity.RpmEntity;
import com.daimons.daimondreams.model.BtsEntityModel;
import com.daimons.daimondreams.model.FEntityModel;
import com.daimons.daimondreams.model.RpmEntityModel;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.util.Identifier;


public class FEntityRenderer extends BtsEntityRenderer<FEntity, FEntityModel> {
    private static final Identifier TEXTURE = new Identifier("entitytesting", "textures/entity/f/f.png");


    public FEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new FEntityModel(0.0F, false), new FEntityModel(0.5F, true), new FEntityModel(1.0F, true));
    }

    @Override
    public Identifier getTexture(FEntity entity) {
        return TEXTURE;
    }
}