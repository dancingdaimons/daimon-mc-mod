package com.daimons.daimondreams.renderer;

import com.daimons.daimondreams.entity.BtsEntity;
import com.daimons.daimondreams.entity.SigmaEntity;
import com.daimons.daimondreams.entity.SinEntity;
import com.daimons.daimondreams.model.BtsEntityModel;
import com.daimons.daimondreams.model.SigmaEntityModel;
import com.daimons.daimondreams.model.SinEntityModel;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.util.Identifier;


public class SinEntityRenderer extends BtsEntityRenderer<SinEntity, SinEntityModel> {
    private static final Identifier TEXTURE = new Identifier("entitytesting", "textures/entity/sin/sin.png");


    public SinEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new SinEntityModel(0.0F, false), new SinEntityModel(0.5F, true), new SinEntityModel(1.0F, true));
    }

    @Override
    public Identifier getTexture(SinEntity entity) {
        return TEXTURE;
    }
}