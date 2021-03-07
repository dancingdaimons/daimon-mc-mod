package com.daimons.daimondreams.renderer;

import com.daimons.daimondreams.entity.BtsEntity;
import com.daimons.daimondreams.entity.JuhgkoopEntity;
import com.daimons.daimondreams.entity.SigmaEntity;
import com.daimons.daimondreams.model.BtsEntityModel;
import com.daimons.daimondreams.model.JuhgkoopEntityModel;
import com.daimons.daimondreams.model.SigmaEntityModel;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.util.Identifier;


public class SigmaEntityRenderer extends BtsEntityRenderer<SigmaEntity, SigmaEntityModel> {
    private static final Identifier TEXTURE = new Identifier("entitytesting", "textures/entity/sigma/sigma.png");


    public SigmaEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new SigmaEntityModel(0.0F, false), new SigmaEntityModel(0.5F, true), new SigmaEntityModel(1.0F, true));
    }

    @Override
    public Identifier getTexture(SigmaEntity entity) {
        return TEXTURE;
    }
}