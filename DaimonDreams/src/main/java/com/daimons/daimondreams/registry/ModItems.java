package com.daimons.daimondreams.registry;

import com.daimons.daimondreams.Daimondreams;
import com.daimons.daimondreams.WingsItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class ModItems {

    //Items
    public static final Item TICKET = new Item(new Item.Settings().group(ItemGroup.MISC));
    public static final Item WINGS = new ElytraItem(new FabricItemSettings().fireproof().rarity(Rarity.EPIC).group(ItemGroup.TRANSPORTATION).maxDamage(1000).equipmentSlot(itemStack -> EquipmentSlot.CHEST));


    //Block Items
    public static final BlockItem DEAD_BLOCK = new BlockItem(ModBlocks.DEAD_BLOCK, new Item.Settings().group(ItemGroup.BUILDING_BLOCKS));

    public static void registerItems(){
        Registry.register(Registry.ITEM, new Identifier(Daimondreams.MOD_ID, "ticket"), TICKET);
        Registry.register(Registry.ITEM, new Identifier(Daimondreams.MOD_ID, "dead_block"), DEAD_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(Daimondreams.MOD_ID, "wings"), WINGS);
    }

    public static void registerElytra(Item elytra) {
        FabricModelPredicateProviderRegistry.register(elytra, new Identifier("broken"), (itemStack, clientWorld, livingEntity) -> {
            return ElytraItem.isUsable(itemStack) ? 0.0F : 1.0F;
        });
    }



}
