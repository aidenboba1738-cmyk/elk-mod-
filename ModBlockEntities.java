package com.example.elkmod.block;

import com.example.elkmod.ElkMod;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ElkMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<ElkTrophyBlockEntity>> ELK_TROPHY =
            BLOCK_ENTITIES.register("elk_trophy", () -> BlockEntityType.Builder
                    .of(ElkTrophyBlockEntity::new, ModBlocks.ELK_TROPHY.get()).build(null));
}
