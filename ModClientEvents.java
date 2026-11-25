package com.example.elkmod.client;

import com.example.elkmod.ElkMod;
import com.example.elkmod.block.ModBlockEntities;
import com.example.elkmod.entity.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ElkMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ElkModel.LAYER, ElkModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.ELK.get(), ElkRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.ELK_TROPHY.get(), ElkTrophyRenderer::new);
    }
}
