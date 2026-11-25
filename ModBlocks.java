package com.example.elkmod.block;

import com.example.elkmod.ElkMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ElkMod.MOD_ID);

    public static final RegistryObject<Block> ELK_TROPHY = BLOCKS.register("elk_trophy",
            () -> new ElkTrophyBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD).strength(1.0f).sound(SoundType.WOOD).noOcclusion()));
}
