package com.climinby.starsky_explority.client.entity.model;

import com.climinby.starsky_explority.entity.LunarianEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class LunarianEntityModel extends EntityModel<LunarianEntity> {
    private static final float RESPIRATORY_RATE = (float) (Math.PI) / 51.2F;
    private static final float SWING_RANGE = (float) (Math.PI / 48.0);

    private final ModelPart main;
    private final ModelPart right_hand;
    private final ModelPart left_hand;
    private final ModelPart head;
    private final ModelPart hat;
    private final ModelPart ears;
    private final ModelPart ohead;
    private final ModelPart body;
    private final ModelPart left_leg;
    private final ModelPart right_leg;

    public LunarianEntityModel() {
        this(getTexturedModelData().createModel());
    }
    public LunarianEntityModel(ModelPart root) {
        this.main = root.getChild("main");
        this.right_hand = this.main.getChild("right_hand");
        this.left_hand = this.main.getChild("left_hand");
        this.head = this.main.getChild("head");
        this.hat = this.head.getChild("hat");
        this.ears = this.head.getChild("ears");
        this.ohead = this.head.getChild("ohead");
        this.body = this.main.getChild("body");
        this.left_leg = this.main.getChild("left_leg");
        this.right_leg = this.main.getChild("right_leg");
    }

    @Override
    public void setAngles(LunarianEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float deltaArmPitchL = -SWING_RANGE * MathHelper.sin(RESPIRATORY_RATE * animationProgress);
        float deltaArmRollL = -SWING_RANGE / 2 * (MathHelper.cos(RESPIRATORY_RATE * animationProgress) + 1);
        float deltaArmPitchR = -SWING_RANGE * MathHelper.sin(RESPIRATORY_RATE * animationProgress - (float) (Math.PI));
        float deltaArmRollR = -SWING_RANGE / 2 * (MathHelper.cos(RESPIRATORY_RATE * animationProgress - (float) (Math.PI)) - 1);
        this.left_hand.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance + deltaArmPitchL;
        this.left_hand.roll = deltaArmRollL;
        this.right_hand.pitch = MathHelper.cos(limbAngle * 0.6662F + (float)Math.PI) * 1.4F * limbDistance + deltaArmPitchR;
        this.right_hand.roll = deltaArmRollR;
        this.right_leg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
        this.left_leg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float)Math.PI) * 1.4F * limbDistance;
        this.head.yaw = headYaw * ((float)Math.PI / 180F);
        this.head.pitch = headPitch * ((float)Math.PI / 180F);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData right_hand = main.addChild("right_hand", ModelPartBuilder.create().uv(12, 41).cuboid(-1.5F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-5.5F, -22.0F, 0.0F));

        ModelPartData left_hand = main.addChild("left_hand", ModelPartBuilder.create().uv(26, 41).cuboid(-1.5F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(5.5F, -22.0F, 0.0F));

        ModelPartData head = main.addChild("head", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -24.0F, 1.0F));

        ModelPartData hat = head.addChild("hat", ModelPartBuilder.create(), ModelTransform.pivot(-2.0F, 1.0F, 3.0F));

        ModelPartData hat_r1 = hat.addChild("hat_r1", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -10.0F, -4.0F, 9.0F, 10.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, 1.0F, -1.0F, -0.1745F, 0.0F, 0.0F));

        ModelPartData ears = head.addChild("ears", ModelPartBuilder.create(), ModelTransform.pivot(-2.0F, 1.0F, 3.0F));

        ModelPartData right_ear_r1 = ears.addChild("right_ear_r1", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -5.0F, 0.0F, 2.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, -8.0F, -5.0F, 0.0F, 0.0F, -0.1745F));

        ModelPartData left_ear_r1 = ears.addChild("left_ear_r1", ModelPartBuilder.create().uv(0, 15).cuboid(-1.0F, -5.0F, 0.0F, 2.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(4.5F, -8.0F, -5.0F, 0.0F, 0.0F, 0.1745F));

        ModelPartData ohead = head.addChild("ohead", ModelPartBuilder.create().uv(0, 15).cuboid(-2.0F, -9.0F, -7.0F, 8.0F, 8.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 1.0F, 3.0F));

        ModelPartData body = main.addChild("body", ModelPartBuilder.create().uv(24, 25).cuboid(-4.0F, -6.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -18.0F, 0.0F));

        ModelPartData left_leg = main.addChild("left_leg", ModelPartBuilder.create().uv(0, 29).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, -10.0F, 0.0F));

        ModelPartData right_leg = main.addChild("right_leg", ModelPartBuilder.create().uv(28, 0).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, -10.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }
}
