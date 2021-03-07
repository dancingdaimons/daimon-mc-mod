package com.daimons.daimondreams.renderer;

import com.daimons.daimondreams.entity.BtsEntity;
import com.daimons.daimondreams.model.BtsEntityModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BtsEntityRenderer<T extends BtsEntity, M extends BtsEntityModel<T>> extends BipedEntityRenderer<T,M> {
    private static final Identifier TEXTURE = new Identifier("entitytesting", "textures/entity/rpm/rpm.png");

    protected BtsEntityRenderer(EntityRenderDispatcher dispatcher, M zombieEntityModel, M zombieEntityModel2, M zombieEntityModel3) {
        super(dispatcher, zombieEntityModel, 0.5F);
        this.addFeature(new ArmorFeatureRenderer(this, zombieEntityModel2, zombieEntityModel3));
    }

    public Identifier getTexture(T zombieEntity) {
        return TEXTURE;
    }
}
