package com.example.elkmod.item;

import com.example.elkmod.ElkMod;
import com.example.elkmod.block.ModBlocks;
import com.example.elkmod.entity.ModEntities;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ElkMod.MOD_ID);

    public static final RegistryObject<Item> RAW_ELK_MEAT = ITEMS.register("raw_elk_meat",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(3).saturationMod(0.3f).meat().build())));

    public static final RegistryObject<Item> COOKED_ELK_MEAT = ITEMS.register("cooked_elk_meat",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(8).saturationMod(0.8f).meat().build())));

    public static final RegistryObject<Item> ELK_STEW = ITEMS.register("elk_stew",
            () -> new BowlFoodItem(new Item.Properties().stacksTo(1).food(
                    new FoodProperties.Builder().nutrition(11).saturationMod(0.8f).build())));

    public static final RegistryObject<Item> ELK_JERKY = ITEMS.register("elk_jerky",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(5).saturationMod(0.6f).fast().meat().build())));

    public static final RegistryObject<Item> ELK_SANDWICH = ITEMS.register("elk_sandwich",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(9).saturationMod(0.7f).build())));

    public static final RegistryObject<Item> ELK_HIDE = ITEMS.register("elk_hide",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ELK_BUGLE = ITEMS.register("elk_bugle",
            () -> new ElkBugleItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ELK_TROPHY = ITEMS.register("elk_trophy",
            () -> new ElkTrophyItem(ModBlocks.ELK_TROPHY.get(), new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ELK_SPAWN_EGG = ITEMS.register("elk_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.ELK, 0x6B4423, 0xD9C7A0, new Item.Properties()));
}
