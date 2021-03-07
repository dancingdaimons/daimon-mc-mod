package com.daimons.daimondreams.registry;

import com.daimons.daimondreams.Daimondreams;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public class ModBlocks {

    public static final Block DEAD_BLOCK = new Block(FabricBlockSettings
        .of(Material.ORGANIC_PRODUCT)
        .breakByTool(FabricToolTags.PICKAXES, 0)
        .requiresTool()
        .strength(5f, 1F)
        .sounds(BlockSoundGroup.METAL));

    public static void registerBlocks(){
        Registry.register(Registry.BLOCK, new Identifier(Daimondreams.MOD_ID, "dead_block"), DEAD_BLOCK);
    }
}
