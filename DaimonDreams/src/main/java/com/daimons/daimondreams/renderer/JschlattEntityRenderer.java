package com.daimons.daimondreams.renderer;

import com.daimons.daimondreams.entity.BtsEntity;
import com.daimons.daimondreams.entity.JschlattEntity;
import com.daimons.daimondreams.entity.RpmEntity;
import com.daimons.daimondreams.model.BtsEntityModel;
import com.daimons.daimondreams.model.JschlattEntityModel;
import com.daimons.daimondreams.model.RpmEntityModel;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.util.Identifier;


public class JschlattEntityRenderer extends BtsEntityRenderer<JschlattEntity, JschlattEntityModel> {
    private static final Identifier TEXTURE = new Identifier("entitytesting", "textures/entity/jschlatt/jschlatt.png");


    public JschlattEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new JschlattEntityModel(0.0F, false), new JschlattEntityModel(0.5F, true), new JschlattEntityModel(1.0F, true));
    }

    @Override
    public Identifier getTexture(JschlattEntity entity) {
        return TEXTURE;
    }
}