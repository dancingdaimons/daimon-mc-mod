package com.daimons.daimondreams.renderer;

import com.daimons.daimondreams.entity.BtsEntity;
import com.daimons.daimondreams.entity.JimmyEntity;
import com.daimons.daimondreams.entity.RpmEntity;
import com.daimons.daimondreams.model.BtsEntityModel;
import com.daimons.daimondreams.model.JimmyEntityModel;
import com.daimons.daimondreams.model.RpmEntityModel;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.util.Identifier;


public class JimmyEntityRenderer extends BtsEntityRenderer<JimmyEntity, JimmyEntityModel> {
    private static final Identifier TEXTURE = new Identifier("entitytesting", "textures/entity/jimmy/jimmy.png");


    public JimmyEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new JimmyEntityModel(0.0F, false), new JimmyEntityModel(0.5F, true), new JimmyEntityModel(1.0F, true));
    }

    @Override
    public Identifier getTexture(JimmyEntity entity) {
        return TEXTURE;
    }
}