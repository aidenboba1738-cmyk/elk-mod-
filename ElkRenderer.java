package com.example.elkmod.client;

import com.example.elkmod.ElkMod;
import com.example.elkmod.entity.ElkEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ElkRenderer extends MobRenderer<ElkEntity, ElkModel> {
    private static final ResourceLocation[] TEXTURES = {
            texture("tan"), texture("gray"), texture("brown"), texture("blonde")
    };

    private static ResourceLocation texture(String coat) {
        return new ResourceLocation(ElkMod.MOD_ID, "textures/entity/elk/elk_" + coat + ".png");
    }

    public static ResourceLocation textureFor(int coat) {
        return TEXTURES[Mth.clamp(coat, 0, 3)];
    }

    public ElkRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ElkModel(ctx.bakeLayer(ElkModel.LAYER)), 0.9f);
    }

    @Override
    public ResourceLocation getTextureLocation(ElkEntity elk) {
        return textureFor(elk.getGenes().visibleCoat());
    }

    @Override
    protected void scale(ElkEntity elk, PoseStack pose, float partialTick) {
        float s = elk.getLook().bodyScale;
        pose.scale(s, s, s);
        this.shadowRadius = 0.9f * s;
    }
}
