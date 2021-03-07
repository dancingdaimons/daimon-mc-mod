package com.daimons.daimondreams.renderer;

import com.daimons.daimondreams.entity.BtsEntity;
import com.daimons.daimondreams.entity.JuhgkoopEntity;
import com.daimons.daimondreams.entity.RpmEntity;
import com.daimons.daimondreams.model.BtsEntityModel;
import com.daimons.daimondreams.model.JuhgkoopEntityModel;
import com.daimons.daimondreams.model.RpmEntityModel;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.util.Identifier;


public class JuhgkoopEntityRenderer extends BtsEntityRenderer<JuhgkoopEntity, JuhgkoopEntityModel> {
    private static final Identifier TEXTURE = new Identifier("entitytesting", "textures/entity/juhgkoop/juhgkoop.png");


    public JuhgkoopEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new JuhgkoopEntityModel(0.0F, false), new JuhgkoopEntityModel(0.5F, true), new JuhgkoopEntityModel(1.0F, true));
    }

    @Override
    public Identifier getTexture(JuhgkoopEntity entity) {
        return TEXTURE;
    }
}