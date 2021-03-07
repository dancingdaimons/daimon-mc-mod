package com.daimons.daimondreams;

import com.daimons.daimondreams.renderer.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

public class DaimondreamsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        /*
         * Registers our Cube Entity's renderer, which provides a model and texture for the entity.
         *
         * Entity Renderers can also manipulate the model before it renders based on entity context (EndermanEntityRenderer#render).
         */
        EntityRendererRegistry.INSTANCE.register(Daimondreams.CUBE, (dispatcher, context) -> {
            return new CubeEntityRenderer(dispatcher);
        });
        EntityRendererRegistry.INSTANCE.register(Daimondreams.RPM, (dispatcher, context) -> {
            return new RpmEntityRenderer(dispatcher);
        });
        EntityRendererRegistry.INSTANCE.register(Daimondreams.JSCHLATT, (dispatcher, context) -> {
            return new JschlattEntityRenderer(dispatcher);
        });
        EntityRendererRegistry.INSTANCE.register(Daimondreams.SIGMA, (dispatcher, context) -> {
            return new SigmaEntityRenderer(dispatcher);
        });
        EntityRendererRegistry.INSTANCE.register(Daimondreams.SIN, (dispatcher, context) -> {
            return new SinEntityRenderer(dispatcher);
        });
        EntityRendererRegistry.INSTANCE.register(Daimondreams.F, (dispatcher, context) -> {
            return new FEntityRenderer(dispatcher);
        });
        EntityRendererRegistry.INSTANCE.register(Daimondreams.JIMMY, (dispatcher, context) -> {
            return new JimmyEntityRenderer(dispatcher);
        });
        EntityRendererRegistry.INSTANCE.register(Daimondreams.JUHGKOOP, (dispatcher, context) -> {
            return new JuhgkoopEntityRenderer(dispatcher);
        });
    }
}
