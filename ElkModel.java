package com.example.elkmod.client;

import com.example.elkmod.ElkMod;
import com.example.elkmod.entity.ElkEntity;
import com.example.elkmod.entity.ElkLook;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

// Body, neck, head, ears, legs, tail, plus a left and right antler with 7 tine slots each.
// Which tines show depends on the genes.
public class ElkModel extends EntityModel<ElkEntity> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(ElkMod.MOD_ID, "elk"), "main");

    private final ModelPart root, head, legFL, legFR, legBL, legBR, antlerL, antlerR;
    private final ModelPart[] tinesL = new ModelPart[7];
    private final ModelPart[] tinesR = new ModelPart[7];
    private ElkLook look;

    public ElkModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.legFL = root.getChild("leg_fl");
        this.legFR = root.getChild("leg_fr");
        this.legBL = root.getChild("leg_bl");
        this.legBR = root.getChild("leg_br");
        this.antlerL = head.getChild("antler_l");
        this.antlerR = head.getChild("antler_r");
        for (int i = 0; i < 7; i++) {
            tinesL[i] = antlerL.getChild("tine_" + i);
            tinesR[i] = antlerR.getChild("tine_" + i);
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-5.5f, -5.5f, -12f, 11f, 11f, 24f), PartPose.offset(0f, 4.5f, 0f));
        root.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(70, 0)
                .addBox(-3f, -14f, -3f, 6f, 14f, 6f), PartPose.offsetAndRotation(0f, 2f, -11f, 0.6f, 0f, 0f));

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(94, 0)
                .addBox(-3f, -3f, -10f, 6f, 6f, 10f), PartPose.offsetAndRotation(0f, -9.5f, -19f, 0.45f, 0f, 0f));
        head.addOrReplaceChild("ear_l", CubeListBuilder.create().texOffs(94, 16)
                .addBox(0f, -4f, -0.5f, 3f, 4f, 1f), PartPose.offsetAndRotation(2.5f, -2f, -2f, 0f, 0f, 0.6f));
        head.addOrReplaceChild("ear_r", CubeListBuilder.create().texOffs(102, 16)
                .addBox(-3f, -4f, -0.5f, 3f, 4f, 1f), PartPose.offsetAndRotation(-2.5f, -2f, -2f, 0f, 0f, -0.6f));

        PartDefinition aL = head.addOrReplaceChild("antler_l", CubeListBuilder.create().texOffs(48, 36)
                .addBox(-1f, -16f, -1f, 2f, 16f, 2f), PartPose.offsetAndRotation(2f, -3f, -3f, -0.5f, 0f, 0.4f));
        PartDefinition aR = head.addOrReplaceChild("antler_r", CubeListBuilder.create().texOffs(56, 36)
                .addBox(-1f, -16f, -1f, 2f, 16f, 2f), PartPose.offsetAndRotation(-2f, -3f, -3f, -0.5f, 0f, -0.4f));
        float[] ty = {-2f, -4.5f, -7f, -9.5f, -12f, -14.5f, -16f};
        for (int i = 0; i < 7; i++) {
            aL.addOrReplaceChild("tine_" + i, CubeListBuilder.create().texOffs(64, 36)
                    .addBox(-0.5f, -6f, -0.5f, 1f, 6f, 1f), PartPose.offsetAndRotation(0f, ty[i], 0f, 0.8f, 0f, 0f));
            aR.addOrReplaceChild("tine_" + i, CubeListBuilder.create().texOffs(68, 36)
                    .addBox(-0.5f, -6f, -0.5f, 1f, 6f, 1f), PartPose.offsetAndRotation(0f, ty[i], 0f, 0.8f, 0f, 0f));
        }

        root.addOrReplaceChild("leg_fl", CubeListBuilder.create().texOffs(0, 36)
                .addBox(-1.5f, 0f, -1.5f, 3f, 14f, 3f), PartPose.offset(-3.5f, 10f, -9f));
        root.addOrReplaceChild("leg_fr", CubeListBuilder.create().texOffs(12, 36)
                .addBox(-1.5f, 0f, -1.5f, 3f, 14f, 3f), PartPose.offset(3.5f, 10f, -9f));
        root.addOrReplaceChild("leg_bl", CubeListBuilder.create().texOffs(24, 36)
                .addBox(-1.5f, 0f, -1.5f, 3f, 14f, 3f), PartPose.offset(-3.5f, 10f, 9f));
        root.addOrReplaceChild("leg_br", CubeListBuilder.create().texOffs(36, 36)
                .addBox(-1.5f, 0f, -1.5f, 3f, 14f, 3f), PartPose.offset(3.5f, 10f, 9f));
        root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(72, 36)
                .addBox(-1.5f, 0f, 0f, 3f, 4f, 2f), PartPose.offsetAndRotation(0f, 0.5f, 12f, 0.4f, 0f, 0f));

        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void setupAnim(ElkEntity elk, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        this.look = elk.getLook();
        head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        head.xRot = 0.45f + headPitch * Mth.DEG_TO_RAD;
        float s = Mth.cos(limbSwing * 0.6662f) * 1.2f * limbSwingAmount;
        legFL.xRot = s;
        legBR.xRot = s;
        legFR.xRot = -s;
        legBL.xRot = -s;
    }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer buf, int light, int overlay,
                               float r, float g, float b, float a) {
        renderWith(look, false, pose, buf, light, overlay, r, g, b, a);
    }

    // Also used by the trophy mount, which draws only the head and antlers.
    public void renderWith(ElkLook look, boolean headOnly, PoseStack pose, VertexConsumer buf,
                           int light, int overlay, float r, float g, float b, float a) {
        antlerL.visible = false;
        antlerR.visible = false;
        if (headOnly) head.render(pose, buf, light, overlay, r, g, b, a);
        else root.render(pose, buf, light, overlay, r, g, b, a);

        if (look == null || look.points <= 0) return;
        for (int i = 0; i < 7; i++) {
            tinesL[i].visible = i < look.tinesLeft;
            tinesR[i].visible = i < look.tinesRight;
        }
        pose.pushPose();
        head.translateAndRotate(pose);
        // Grow or shrink the antlers around their base.
        pose.translate(0f, -3f / 16f, -3f / 16f);
        pose.scale(look.antlerScale, look.antlerScale, look.antlerScale);
        pose.translate(0f, 3f / 16f, 3f / 16f);
        antlerL.visible = true;
        antlerR.visible = true;
        antlerL.render(pose, buf, light, overlay, r * look.tintR, g * look.tintG, b * look.tintB, a);
        antlerR.render(pose, buf, light, overlay, r * look.tintR, g * look.tintG, b * look.tintB, a);
        pose.popPose();
    }
}
