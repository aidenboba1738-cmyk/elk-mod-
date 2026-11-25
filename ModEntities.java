package com.example.elkmod.entity;

import com.example.elkmod.ElkMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ElkMod.MOD_ID);

    public static final RegistryObject<EntityType<ElkEntity>> ELK = ENTITIES.register("elk",
            () -> EntityType.Builder.of(ElkEntity::new, MobCategory.CREATURE)
                    .sized(1.4f, 1.9f)
                    .clientTrackingRange(10)
                    .build("elk"));
}
