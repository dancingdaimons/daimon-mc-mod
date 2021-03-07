package com.daimons.daimondreams;

import com.daimons.daimondreams.armor.ModArmorMaterials;
import com.daimons.daimondreams.entity.*;
import com.daimons.daimondreams.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class Daimondreams implements ModInitializer {

    public static final String MOD_ID = "daidre";
    public static final Item GOOGLES = new ArmorItem(ModArmorMaterials.GOOGLES, EquipmentSlot.HEAD, new Item.Settings().group(ItemGroup.TOOLS).rarity(Rarity.EPIC));
    private static final Identifier EMERALD_ORE_LOOT_TABLE_ID = new Identifier("minecraft", "blocks/emerald_ore");
    private static final Identifier DEAD_BLOCK_LOOT_TABLE_ID = new Identifier("daidre", "blocks/dead_block");
    public static final Identifier BTSANGERY = new Identifier("daidre:btsangery");
    public static final Identifier BTSCAVE = new Identifier("daidre:btscave");
    public static final Identifier BTSMUSIC = new Identifier("daidre:btsmusic");
    public static final Identifier BTSSUMMON = new Identifier("daidre:btsummon");
    public static SoundEvent BTSANGERY_SE = new SoundEvent(BTSANGERY);
    public static SoundEvent BTSCAVE_SE = new SoundEvent(BTSCAVE);
    public static SoundEvent BTSMUSIC_SE = new SoundEvent(BTSMUSIC);
    public static SoundEvent BTSSUMMON_SE = new SoundEvent(BTSSUMMON);

  public static final EntityType<CubeEntity> CUBE = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier("entitytesting", "cube"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, CubeEntity::new).dimensions(EntityDimensions.fixed(0.75f, 0.75f)).build()
    );
    public static final EntityType<RpmEntity> RPM = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier("entitytesting", "rpm"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, RpmEntity::new).dimensions(EntityDimensions.fixed(0.75f, 2f)).build()
    );
    public static final EntityType<JschlattEntity> JSCHLATT = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier("entitytesting", "jschlatt"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, JschlattEntity::new).dimensions(EntityDimensions.fixed(0.75f, 2f)).build()
    ); public static final EntityType<SigmaEntity> SIGMA = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier("entitytesting", "sigma"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, SigmaEntity::new).dimensions(EntityDimensions.fixed(0.75f, 2f)).build()
    ); public static final EntityType<SinEntity> SIN = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier("entitytesting", "sin"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, SinEntity::new).dimensions(EntityDimensions.fixed(0.75f, 2f)).build()
    );
    public static final EntityType<FEntity> F = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier("entitytesting", "f"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, FEntity::new).dimensions(EntityDimensions.fixed(0.75f, 2f)).build()
    );
    public static final EntityType<JimmyEntity> JIMMY = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier("entitytesting", "jimmy"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, JimmyEntity::new).dimensions(EntityDimensions.fixed(0.75f, 2f)).build()
    );
    public static final EntityType<JuhgkoopEntity> JUHGKOOP = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier("entitytesting", "juhgkoop"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, JuhgkoopEntity::new).dimensions(EntityDimensions.fixed(0.75f, 2f)).build()
    );
    @Override
    public void onInitialize() {
        ModItems.registerItems();
        ModBlocks.registerBlocks();
        ModItems.registerElytra(ModItems.WINGS);
        Registry.register(Registry.ITEM, new Identifier("daidre", "googles"), GOOGLES);
        modifyLootTables();
        Registry.register(Registry.SOUND_EVENT, Daimondreams.BTSANGERY, BTSANGERY_SE);
        Registry.register(Registry.SOUND_EVENT, Daimondreams.BTSCAVE, BTSCAVE_SE);
        Registry.register(Registry.SOUND_EVENT, Daimondreams.BTSMUSIC, BTSMUSIC_SE);
        Registry.register(Registry.SOUND_EVENT, Daimondreams.BTSSUMMON, BTSSUMMON_SE);

        FabricDefaultAttributeRegistry.register(CUBE, CubeEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10));
        FabricDefaultAttributeRegistry.register(RPM, RpmEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6));
        FabricDefaultAttributeRegistry.register(JSCHLATT, JschlattEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 20)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6));
        FabricDefaultAttributeRegistry.register(SIGMA, SigmaEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 20)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6));
        FabricDefaultAttributeRegistry.register(SIN, SinEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 20)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6));
        FabricDefaultAttributeRegistry.register(F, FEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 20)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6));
        FabricDefaultAttributeRegistry.register(JIMMY, JimmyEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 30)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6));
        FabricDefaultAttributeRegistry.register(JUHGKOOP, JuhgkoopEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 40)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6));

    }
    private void modifyLootTables(){
        LootTableLoadingCallback.EVENT.register(((resourceManager, lootManager, id, supplier, setter) -> {
            //checks for emeral ore loot table
            if (EMERALD_ORE_LOOT_TABLE_ID.equals(id)){
                //add individual item
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
                        .rolls(ConstantLootTableRange.create(1))
                .with(ItemEntry.builder(Items.GOLD_NUGGET))
                .withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F)).build());
                supplier.withPool(poolBuilder.build());

                //add custom loot table (dead blocks entire table im assuming
                FabricLootPoolBuilder poolBuilder2 = FabricLootPoolBuilder.builder()
                        .rolls(ConstantLootTableRange.create(1))
                        .with(LootTableEntry.builder(DEAD_BLOCK_LOOT_TABLE_ID));
                supplier.withPool(poolBuilder2.build());
            }
        }));
    }
}
