package com.daimons.daimondreams.registry;

import com.daimons.daimondreams.Daimondreams;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModEntities {

    //Items
    public static final Item TICKET = new Item(new Item.Settings().group(ItemGroup.MISC));

    //Block Items
    public static final BlockItem DEAD_BLOCK = new BlockItem(ModBlocks.DEAD_BLOCK, new Item.Settings().group(ItemGroup.BUILDING_BLOCKS));

    public static void registerItems(){
        Registry.register(Registry.ITEM, new Identifier(Daimondreams.MOD_ID, "ticket"), TICKET);
        Registry.register(Registry.ITEM, new Identifier(Daimondreams.MOD_ID, "dead_block"), DEAD_BLOCK);
    }

}
