package com.example.elkmod.client;

import com.example.elkmod.block.ElkTrophyBlock;
import com.example.elkmod.block.ElkTrophyBlockEntity;
import com.example.elkmod.entity.ElkLook;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

// If the mount looks backwards on east or west walls, swap the sign in the rotation line.
public class ElkTrophyRenderer implements BlockEntityRenderer<ElkTrophyBlockEntity> {
    private final ElkModel model;

    public ElkTrophyRenderer(BlockEntityRendererProvider.Context ctx) {
        this.model = new ElkModel(ctx.bakeLayer(ElkModel.LAYER));
    }

    @Override
    public void render(ElkTrophyBlockEntity be, float partialTick, PoseStack pose,
                       MultiBufferSource buffers, int light, int overlay) {
        BlockState state = be.getBlockState();
        if (!(state.getBlock() instanceof ElkTrophyBlock)) return;
        Direction facing = state.getValue(ElkTrophyBlock.FACING);
        boolean wall = state.getValue(ElkTrophyBlock.WALL);
        ElkLook look = be.getLook();

        pose.pushPose();
        pose.translate(0.5, wall ? 0.5 : 0.25, 0.5);
        pose.mulPose(Axis.YP.rotationDegrees(180f - facing.toYRot()));
        if (wall) pose.translate(0, 0, 0.3);
        pose.scale(-0.55f, -0.55f, 0.55f);
        pose.translate(0, 0.594, 1.19);   // move the head's pivot to the middle
        VertexConsumer vc = buffers.getBuffer(RenderType.entityCutoutNoCull(ElkRenderer.textureFor(look.coat)));
        model.renderWith(look, true, pose, vc, light, overlay, 1f, 1f, 1f, 1f);
        pose.popPose();
    }
}
