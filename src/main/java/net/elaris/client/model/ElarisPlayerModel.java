package net.elaris.client.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.network.AbstractClientPlayerEntity;

public class ElarisPlayerModel extends PlayerEntityModel<AbstractClientPlayerEntity> {

    public ElarisPlayerModel(ModelPart root) {
        super(root, false);
    }

    @Override
    public void setAngles(AbstractClientPlayerEntity entity, float limbAngle, float limbDistance,
                          float ageInTicks, float headYaw, float headPitch) {
        super.setAngles(entity, limbAngle, limbDistance, ageInTicks, headYaw, headPitch);

        // Example custom animation logic:
        if (entity.isSprinting()) {
            this.rightArm.pitch = -1.2f;
            this.leftArm.pitch = 1.2f;
        }
    }
}
